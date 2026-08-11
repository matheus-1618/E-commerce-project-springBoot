package com.ecommerce.catalogservice.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for {@link GlobalExceptionHandler}.
 *
 * <p>Strategy: a small stub controller ({@link StubController}) deliberately
 * throws each domain exception so MockMvc exercises the handler without
 * starting the full application context.
 *
 * <p>Each test asserts:
 * <ul>
 *   <li>The correct HTTP status code</li>
 *   <li>Content-Type is application/json (req-test-coverage)</li>
 *   <li>All five ErrorResponse fields are present</li>
 *   <li>The {@code error} field matches the expected HTTP reason phrase</li>
 * </ul>
 */
@WebMvcTest(controllers = GlobalExceptionHandlerTest.StubController.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    // -------------------------------------------------------------------------
    // 404 — ProductNotFoundException
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("ProductNotFoundException → 404 Not Found with ErrorResponse body")
    void productNotFound_returns404() throws Exception {
        mockMvc.perform(get("/test/product-not-found"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.path").value("/test/product-not-found"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    // -------------------------------------------------------------------------
    // 404 — CategoryNotFoundException
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("CategoryNotFoundException → 404 Not Found with ErrorResponse body")
    void categoryNotFound_returns404() throws Exception {
        mockMvc.perform(get("/test/category-not-found"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.path").value("/test/category-not-found"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    // -------------------------------------------------------------------------
    // 400 — CatalogValidationException
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("CatalogValidationException → 400 Bad Request with ErrorResponse body")
    void catalogValidation_returns400() throws Exception {
        mockMvc.perform(get("/test/catalog-validation"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.path").value("/test/catalog-validation"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    // -------------------------------------------------------------------------
    // 409 — CatalogConflictException
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("CatalogConflictException → 409 Conflict with ErrorResponse body")
    void catalogConflict_returns409() throws Exception {
        mockMvc.perform(get("/test/catalog-conflict"))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.path").value("/test/catalog-conflict"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    // -------------------------------------------------------------------------
    // 500 — catch-all
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Unhandled RuntimeException → 500 Internal Server Error with ErrorResponse body")
    void unhandledException_returns500() throws Exception {
        mockMvc.perform(get("/test/runtime-exception"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.message").value("An unexpected error occurred"))
                .andExpect(jsonPath("$.path").value("/test/runtime-exception"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    // -------------------------------------------------------------------------
    // Error response must not contain stack trace text
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Error response body must not contain stack trace text")
    void errorResponse_doesNotExposeStackTrace() throws Exception {
        String body = mockMvc.perform(get("/test/runtime-exception"))
                .andReturn().getResponse().getContentAsString();

        // Stack trace indicators must be absent from the response body
        org.assertj.core.api.Assertions.assertThat(body)
                .doesNotContain("at com.")
                .doesNotContain("java.lang.")
                .doesNotContain("StackTrace");
    }

    // -------------------------------------------------------------------------
    // Stub controller — throws on demand
    // -------------------------------------------------------------------------

    @RestController
    @RequestMapping("/test")
    static class StubController {

        @GetMapping("/product-not-found")
        void throwProductNotFound() {
            throw new ProductNotFoundException(99L);
        }

        @GetMapping("/category-not-found")
        void throwCategoryNotFound() {
            throw new CategoryNotFoundException(99L);
        }

        @GetMapping("/catalog-validation")
        void throwCatalogValidation() {
            throw new CatalogValidationException("Price cannot be negative");
        }

        @GetMapping("/catalog-conflict")
        void throwCatalogConflict() {
            throw new CatalogConflictException("Category 'Electronics' already exists");
        }

        @GetMapping("/runtime-exception")
        void throwRuntimeException() {
            throw new RuntimeException("Unexpected database connection failure");
        }
    }
}
