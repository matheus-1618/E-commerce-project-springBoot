package com.jtspringproject.JtSpringProject.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.lang.reflect.Method;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.GetMapping;

import com.jtspringproject.JtSpringProject.services.categoryService;
import com.jtspringproject.JtSpringProject.services.productService;
import com.jtspringproject.JtSpringProject.services.userService;

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
class GlobalExceptionHandlerTest {

    static final String SENTINEL_NOT_FOUND = "SENTINEL_NOT_FOUND_12345";
    static final String SENTINEL_INTERNAL = "SENTINEL_INTERNAL_67890";
    static final String SENTINEL_VALIDATION = "SENTINEL_VALIDATION_24680";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private userService userService;

    @MockBean
    private categoryService categoryService;

    @MockBean
    private productService productService;

    // -------------------------------------------------------------------------
    // Stub controllers that trigger specific exceptions
    // -------------------------------------------------------------------------

    @Controller
    static class NotFoundTrigger {
        @GetMapping("/test/not-found")
        public void trigger() {
            throw new NoSuchElementException(SENTINEL_NOT_FOUND);
        }
    }

    @Controller
    static class ServerErrorTrigger {
        @GetMapping("/test/server-error")
        public void trigger() {
            throw new RuntimeException(SENTINEL_INTERNAL);
        }
    }

    @Controller
    static class ValidationErrorTrigger {
        @GetMapping("/test/validation-error")
        public void trigger() throws BindException {
            throw new BindException(new Object(), SENTINEL_VALIDATION);
        }
    }

    // -------------------------------------------------------------------------
    // Tests — HTTP status codes and view routing (FR-01, FR-02, FR-03)
    // -------------------------------------------------------------------------

    @Test
    void handleNotFound_returns404StatusAndView() throws Exception {
        mockMvc.perform(get("/test/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(view().name("404"));
    }

    @Test
    void handleAll_returns500StatusAndErrorView() throws Exception {
        mockMvc.perform(get("/test/server-error"))
                .andExpect(status().isInternalServerError())
                .andExpect(view().name("error"));
    }

    @Test
    void handleValidation_returns400StatusAndErrorView() throws Exception {
        mockMvc.perform(get("/test/validation-error"))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("error"));
    }

    // -------------------------------------------------------------------------
    // CWE-R1: Sentinel message absence (FR-08, NFR-01)
    // -------------------------------------------------------------------------

    @Test
    void handleNotFound_doesNotLeakExceptionMessageInResponseBody() throws Exception {
        mockMvc.perform(get("/test/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(not(containsString(SENTINEL_NOT_FOUND))));
    }

    @Test
    void handleAll_doesNotLeakExceptionMessageInResponseBody() throws Exception {
        mockMvc.perform(get("/test/server-error"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(not(containsString(SENTINEL_INTERNAL))));
    }

    @Test
    void handleValidation_doesNotLeakExceptionMessageInResponseBody() throws Exception {
        mockMvc.perform(get("/test/validation-error"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(not(containsString(SENTINEL_VALIDATION))));
    }

    // -------------------------------------------------------------------------
    // CWE-R2: Exception class name absence (FR-08, NFR-01)
    // -------------------------------------------------------------------------

    @Test
    void handleNotFound_doesNotLeakExceptionClassName() throws Exception {
        mockMvc.perform(get("/test/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(not(containsString("NoSuchElementException"))));
    }

    @Test
    void handleAll_doesNotLeakExceptionClassName() throws Exception {
        mockMvc.perform(get("/test/server-error"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(not(containsString("RuntimeException"))));
    }

    @Test
    void handleValidation_doesNotLeakExceptionClassName() throws Exception {
        mockMvc.perform(get("/test/validation-error"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(not(containsString("BindException"))));
    }

    // -------------------------------------------------------------------------
    // CWE-R3: Stack trace fragment absence (FR-08, NFR-01)
    // -------------------------------------------------------------------------

    @Test
    void handleNotFound_doesNotLeakStackTrace() throws Exception {
        mockMvc.perform(get("/test/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(not(containsString("at com.jtspringproject"))));
    }

    @Test
    void handleAll_doesNotLeakStackTrace() throws Exception {
        mockMvc.perform(get("/test/server-error"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(not(containsString("at com.jtspringproject"))));
    }

    @Test
    void handleValidation_doesNotLeakStackTrace() throws Exception {
        mockMvc.perform(get("/test/validation-error"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(not(containsString("at com.jtspringproject"))));
    }

    // -------------------------------------------------------------------------
    // CWE-R4: Package path absence (FR-08, NFR-01)
    // -------------------------------------------------------------------------

    @Test
    void handleNotFound_doesNotLeakPackagePath() throws Exception {
        mockMvc.perform(get("/test/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(not(containsString("com.jtspringproject.JtSpringProject"))));
    }

    @Test
    void handleAll_doesNotLeakPackagePath() throws Exception {
        mockMvc.perform(get("/test/server-error"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(not(containsString("com.jtspringproject.JtSpringProject"))));
    }

    @Test
    void handleValidation_doesNotLeakPackagePath() throws Exception {
        mockMvc.perform(get("/test/validation-error"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(not(containsString("com.jtspringproject.JtSpringProject"))));
    }

    // -------------------------------------------------------------------------
    // FR-04: Conditional NoHandlerFoundException test (dec-nohandlerfound-gated)
    // -------------------------------------------------------------------------

    @Test
    void handleNoHandlerFound_returns404WhenEnabled() throws Exception {
        Method handler = null;
        try {
            handler = GlobalExceptionHandler.class.getDeclaredMethod(
                    "handleNoHandlerFound", org.springframework.web.servlet.NoHandlerFoundException.class,
                    javax.servlet.http.HttpServletRequest.class);
        } catch (NoSuchMethodException ignored) {
            // method not present
        }
        Assumptions.assumeTrue(handler != null,
                "Skipped: handleNoHandlerFound not present (nohandler-found-gate not yet applied)");

        mockMvc.perform(get("/test/unmapped-path-that-does-not-exist"))
                .andExpect(status().isNotFound())
                .andExpect(view().name("404"))
                .andExpect(content().string(not(containsString("com.jtspringproject.JtSpringProject"))))
                .andExpect(content().string(not(containsString("at com.jtspringproject"))))
                .andExpect(content().string(not(containsString("NoHandlerFoundException"))));
    }
}
