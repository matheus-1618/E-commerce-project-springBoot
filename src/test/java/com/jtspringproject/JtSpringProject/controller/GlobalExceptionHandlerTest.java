package com.jtspringproject.JtSpringProject.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.GetMapping;

import com.jtspringproject.JtSpringProject.services.categoryService;
import com.jtspringproject.JtSpringProject.services.productService;
import com.jtspringproject.JtSpringProject.services.userService;

/**
 * WebMvcTest suite for GlobalExceptionHandler.
 *
 * <p>Uses lightweight stub controllers to trigger each exception handler and
 * verifies correct HTTP status codes, view names, and absence of exception
 * detail strings in response bodies (CWE-209 no-leakage assertion per
 * [[security-design]]).</p>
 *
 * <p>Spring Security and Hibernate JPA auto-configurations are excluded so the
 * test runs without a live database or security filter chain, per
 * dec-webmvctest-mock-services.</p>
 */
@WebMvcTest(
    controllers = {
        GlobalExceptionHandler.class,
        GlobalExceptionHandlerTest.NotFoundTrigger.class,
        GlobalExceptionHandlerTest.ServerErrorTrigger.class,
        GlobalExceptionHandlerTest.ValidationErrorTrigger.class
    },
    excludeAutoConfiguration = {
        HibernateJpaAutoConfiguration.class,
        SecurityAutoConfiguration.class
    }
)
@TestPropertySource(properties = {
    "spring.mvc.throw-exception-if-no-handler-found=true",
    "spring.web.resources.add-mappings=false"
})
class GlobalExceptionHandlerTest {

    static final String SENTINEL_NOT_FOUND = "SENTINEL_NOT_FOUND_12345";
    static final String SENTINEL_INTERNAL = "SENTINEL_INTERNAL_67890";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private userService userService;

    @MockBean
    private categoryService categoryService;

    @MockBean
    private productService productService;

    // ---------------------------------------------------------------------------
    // Stub controllers that trigger specific exceptions
    // ---------------------------------------------------------------------------

    /**
     * Triggers a NoSuchElementException with a sentinel message so tests can
     * verify the message does NOT appear in the HTTP response body.
     */
    @Controller
    static class NotFoundTrigger {
        @GetMapping("/test/not-found")
        public void trigger() {
            throw new NoSuchElementException(SENTINEL_NOT_FOUND);
        }
    }

    /**
     * Triggers a generic RuntimeException with a sentinel message so tests can
     * verify the message does NOT appear in the HTTP response body.
     */
    @Controller
    static class ServerErrorTrigger {
        @GetMapping("/test/server-error")
        public void trigger() {
            throw new RuntimeException(SENTINEL_INTERNAL);
        }
    }

    /**
     * Triggers a BindException with a field error so the validation handler is
     * exercised and FieldError propagation can be verified.
     */
    @Controller
    static class ValidationErrorTrigger {
        @GetMapping("/test/validation-error")
        public void trigger() throws BindException {
            BindException ex = new BindException(new Object(), "product");
            ex.rejectValue(null, "NotNull", "Name is required");
            throw ex;
        }
    }

    // ---------------------------------------------------------------------------
    // Tests — HTTP status codes and view routing
    // ---------------------------------------------------------------------------

    /**
     * NoSuchElementException must yield HTTP 404 and route to the 404 view
     * (story-safe-404-page, req-not-found-handler).
     */
    @Test
    void handleNotFound_returns404StatusAndView() throws Exception {
        mockMvc.perform(get("/test/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(view().name("404"));
    }

    /**
     * Generic Exception must yield HTTP 500 and route to the error view
     * (story-safe-500-page, req-catchall-handler).
     */
    @Test
    void handleAll_returns500StatusAndErrorView() throws Exception {
        mockMvc.perform(get("/test/server-error"))
                .andExpect(status().isInternalServerError())
                .andExpect(view().name("error"));
    }

    /**
     * BindException must yield HTTP 400 and route to the error view
     * (req-validation-handler).
     */
    @Test
    void handleValidation_returns400StatusAndErrorView() throws Exception {
        mockMvc.perform(get("/test/validation-error"))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("error"));
    }

    // ---------------------------------------------------------------------------
    // Tests — Validation model attribute (FR-02, dec-validation-field-errors)
    // ---------------------------------------------------------------------------

    /**
     * BindException must place the BindingResult in the model under key "errors"
     * so the view can render field-level validation messages.
     */
    @Test
    void handleValidation_placesBindingResultInModel() throws Exception {
        mockMvc.perform(get("/test/validation-error"))
                .andExpect(status().isBadRequest())
                .andExpect(model().attributeExists("errors"));
    }

    // ---------------------------------------------------------------------------
    // Tests — CWE-209 no-leakage assertions (req-nfr-no-leakage)
    // ---------------------------------------------------------------------------

    /**
     * The NoSuchElementException sentinel message must NOT appear in the HTTP
     * response body — verifies CWE-209 compliance for the 404 handler.
     */
    @Test
    void handleNotFound_doesNotLeakExceptionMessageInResponseBody() throws Exception {
        mockMvc.perform(get("/test/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(not(containsString(SENTINEL_NOT_FOUND))));
    }

    /**
     * The RuntimeException sentinel message must NOT appear in the HTTP response
     * body — verifies CWE-209 compliance for the catch-all handler.
     */
    @Test
    void handleAll_doesNotLeakExceptionMessageInResponseBody() throws Exception {
        mockMvc.perform(get("/test/server-error"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(not(containsString(SENTINEL_INTERNAL))));
    }

    // ---------------------------------------------------------------------------
    // Tests — NoHandlerFoundException (FR-04, story-not-found-unmapped-url)
    // ---------------------------------------------------------------------------

    /**
     * A request to a URL with no handler mapping must yield HTTP 404 and route to
     * the 404 view via NoHandlerFoundException (requires throw-exception-if-no-handler-found=true
     * and add-mappings=false).
     */
    @Test
    void handleNoHandlerFound_returns404StatusAndView() throws Exception {
        mockMvc.perform(get("/nonexistent-unmapped-path"))
                .andExpect(status().isNotFound())
                .andExpect(view().name("404"));
    }

    /**
     * The NoHandlerFoundException handler must NOT leak exception class name or
     * request path in the HTTP response body — CWE-209 compliance.
     */
    @Test
    void handleNoHandlerFound_doesNotLeakExceptionDetailsInResponseBody() throws Exception {
        mockMvc.perform(get("/nonexistent-unmapped-path"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(not(containsString("NoHandlerFoundException"))))
                .andExpect(content().string(not(containsString("/nonexistent-unmapped-path"))));
    }
}
