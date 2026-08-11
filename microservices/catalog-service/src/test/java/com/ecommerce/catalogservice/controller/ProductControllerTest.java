package com.ecommerce.catalogservice.controller;

import com.ecommerce.catalogservice.dto.CreateProductRequest;
import com.ecommerce.catalogservice.dto.ProductDto;
import com.ecommerce.catalogservice.dto.UpdateProductRequest;
import com.ecommerce.catalogservice.exception.CategoryNotFoundException;
import com.ecommerce.catalogservice.exception.GlobalExceptionHandler;
import com.ecommerce.catalogservice.exception.ProductNotFoundException;
import com.ecommerce.catalogservice.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * MockMvc slice tests for {@link ProductController}.
 *
 * <p>Verifies that:
 * <ul>
 *   <li>Successful paths return correct 2xx status and response body (req-no-successful-response-change)</li>
 *   <li>Service exceptions are delegated to {@link GlobalExceptionHandler} and produce
 *       the uniform error envelope (req-404-mapping, req-400-mapping)</li>
 *   <li>Content-Type is application/json for both success and error responses (req-test-coverage)</li>
 * </ul>
 */
@WebMvcTest(ProductController.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("ProductController")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    private static final ProductDto LAPTOP_DTO = new ProductDto(
            1L, "Laptop Pro", "laptop.jpg",
            1L, "Electronics", 10,
            new BigDecimal("999.99"), new BigDecimal("2.500"),
            "High-performance laptop");

    // -------------------------------------------------------------------------
    // GET /api/products
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("GET /api/products → 200 with product list")
    void getAllProducts_returns200WithList() throws Exception {
        when(productService.findAll()).thenReturn(List.of(LAPTOP_DTO));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Laptop Pro"))
                .andExpect(jsonPath("$[0].categoryName").value("Electronics"));
    }

    // -------------------------------------------------------------------------
    // GET /api/products/{id}
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("GET /api/products/{id} → 200 for existing id")
    void getProductById_existingId_returns200() throws Exception {
        when(productService.findById(1L)).thenReturn(LAPTOP_DTO);

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Laptop Pro"));
    }

    @Test
    @DisplayName("GET /api/products/{id} → 404 for unknown id (req-404-mapping)")
    void getProductById_unknownId_returns404WithErrorResponse() throws Exception {
        when(productService.findById(99L)).thenThrow(new ProductNotFoundException(99L));

        mockMvc.perform(get("/api/products/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.path").value("/api/products/99"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    // -------------------------------------------------------------------------
    // POST /api/products
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("POST /api/products → 201 Created")
    void createProduct_validRequest_returns201() throws Exception {
        CreateProductRequest request = new CreateProductRequest(
                "Tablet", "tablet.jpg", 1L, 5,
                new BigDecimal("299.99"), new BigDecimal("0.600"), "Compact tablet");

        ProductDto saved = new ProductDto(
                2L, "Tablet", "tablet.jpg",
                1L, "Electronics", 5,
                new BigDecimal("299.99"), new BigDecimal("0.600"), "Compact tablet");

        when(productService.create(any())).thenReturn(saved);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("Tablet"));
    }

    @Test
    @DisplayName("POST /api/products → 400 for blank name (Bean Validation, req-400-mapping)")
    void createProduct_blankName_returns400() throws Exception {
        String invalidBody = """
                {
                  "name": "",
                  "categoryId": 1,
                  "quantity": 5,
                  "price": 9.99
                }
                """;

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    @Test
    @DisplayName("POST /api/products → 404 when category does not exist (req-404-mapping)")
    void createProduct_unknownCategory_returns404() throws Exception {
        CreateProductRequest request = new CreateProductRequest(
                "Tablet", null, 99L, 5,
                new BigDecimal("299.99"), null, null);

        when(productService.create(any()))
                .thenThrow(new CategoryNotFoundException(99L));

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    // -------------------------------------------------------------------------
    // PUT /api/products/{id}
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("PUT /api/products/{id} → 200 Updated")
    void updateProduct_validRequest_returns200() throws Exception {
        UpdateProductRequest request = new UpdateProductRequest(
                "Laptop Pro Max", "laptop-max.jpg", 1L, 8,
                new BigDecimal("1299.99"), new BigDecimal("2.200"), "Upgraded laptop");

        ProductDto updated = new ProductDto(
                1L, "Laptop Pro Max", "laptop-max.jpg",
                1L, "Electronics", 8,
                new BigDecimal("1299.99"), new BigDecimal("2.200"), "Upgraded laptop");

        when(productService.update(eq(1L), any())).thenReturn(updated);

        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Laptop Pro Max"));
    }

    @Test
    @DisplayName("PUT /api/products/{id} → 404 for unknown id (req-404-mapping)")
    void updateProduct_unknownId_returns404() throws Exception {
        UpdateProductRequest request = new UpdateProductRequest(
                "X", null, 1L, 1,
                new BigDecimal("1.00"), null, null);

        when(productService.update(eq(99L), any()))
                .thenThrow(new ProductNotFoundException(99L));

        mockMvc.perform(put("/api/products/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    // -------------------------------------------------------------------------
    // DELETE /api/products/{id}
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("DELETE /api/products/{id} → 204 No Content")
    void deleteProduct_existingId_returns204() throws Exception {
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/products/{id} → 404 for unknown id (req-404-mapping)")
    void deleteProduct_unknownId_returns404() throws Exception {
        doThrow(new ProductNotFoundException(99L))
                .when(productService).delete(99L);

        mockMvc.perform(delete("/api/products/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
