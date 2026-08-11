package com.ecommerce.catalogservice.controller;

import com.ecommerce.catalogservice.dto.CategoryDto;
import com.ecommerce.catalogservice.dto.CreateCategoryRequest;
import com.ecommerce.catalogservice.dto.UpdateCategoryRequest;
import com.ecommerce.catalogservice.exception.CatalogConflictException;
import com.ecommerce.catalogservice.exception.CategoryNotFoundException;
import com.ecommerce.catalogservice.exception.GlobalExceptionHandler;
import com.ecommerce.catalogservice.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * MockMvc slice tests for {@link CategoryController}.
 *
 * <p>Verifies that:
 * <ul>
 *   <li>Successful paths return correct 2xx status and response body (req-no-successful-response-change)</li>
 *   <li>Service exceptions are delegated to {@link GlobalExceptionHandler} and produce
 *       the uniform error envelope (req-404-mapping, req-409-mapping, req-400-mapping)</li>
 *   <li>Content-Type is application/json for both success and error responses (req-test-coverage)</li>
 * </ul>
 */
@WebMvcTest(CategoryController.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("CategoryController")
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    // -------------------------------------------------------------------------
    // GET /api/categories
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("GET /api/categories → 200 with category list")
    void getAllCategories_returns200WithList() throws Exception {
        when(categoryService.findAll()).thenReturn(
                List.of(new CategoryDto(1L, "Electronics")));

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Electronics"));
    }

    // -------------------------------------------------------------------------
    // GET /api/categories/{id}
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("GET /api/categories/{id} → 200 for existing id")
    void getCategoryById_existingId_returns200() throws Exception {
        when(categoryService.findById(1L)).thenReturn(new CategoryDto(1L, "Electronics"));

        mockMvc.perform(get("/api/categories/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Electronics"));
    }

    @Test
    @DisplayName("GET /api/categories/{id} → 404 for unknown id (req-404-mapping)")
    void getCategoryById_unknownId_returns404WithErrorResponse() throws Exception {
        when(categoryService.findById(99L)).thenThrow(new CategoryNotFoundException(99L));

        mockMvc.perform(get("/api/categories/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.path").value("/api/categories/99"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    // -------------------------------------------------------------------------
    // POST /api/categories
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("POST /api/categories → 201 Created")
    void createCategory_validRequest_returns201() throws Exception {
        CreateCategoryRequest request = new CreateCategoryRequest("Books");
        CategoryDto saved = new CategoryDto(2L, "Books");

        when(categoryService.create(any())).thenReturn(saved);

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("Books"));
    }

    @Test
    @DisplayName("POST /api/categories → 400 for blank name (Bean Validation, req-400-mapping)")
    void createCategory_blankName_returns400() throws Exception {
        String invalidBody = """
                {"name": ""}
                """;

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    @Test
    @DisplayName("POST /api/categories → 409 Conflict for duplicate name (req-409-mapping)")
    void createCategory_duplicateName_returns409() throws Exception {
        CreateCategoryRequest request = new CreateCategoryRequest("Electronics");

        when(categoryService.create(any()))
                .thenThrow(new CatalogConflictException("Category 'Electronics' already exists"));

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"));
    }

    // -------------------------------------------------------------------------
    // PUT /api/categories/{id}
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("PUT /api/categories/{id} → 200 Updated")
    void updateCategory_validRequest_returns200() throws Exception {
        UpdateCategoryRequest request = new UpdateCategoryRequest("Electronics Updated");
        CategoryDto updated = new CategoryDto(1L, "Electronics Updated");

        when(categoryService.update(eq(1L), any())).thenReturn(updated);

        mockMvc.perform(put("/api/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Electronics Updated"));
    }

    @Test
    @DisplayName("PUT /api/categories/{id} → 404 for unknown id (req-404-mapping)")
    void updateCategory_unknownId_returns404() throws Exception {
        UpdateCategoryRequest request = new UpdateCategoryRequest("Anything");

        when(categoryService.update(eq(99L), any()))
                .thenThrow(new CategoryNotFoundException(99L));

        mockMvc.perform(put("/api/categories/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    // -------------------------------------------------------------------------
    // DELETE /api/categories/{id}
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("DELETE /api/categories/{id} → 204 No Content")
    void deleteCategory_existingId_returns204() throws Exception {
        mockMvc.perform(delete("/api/categories/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/categories/{id} → 404 for unknown id (req-404-mapping)")
    void deleteCategory_unknownId_returns404() throws Exception {
        doThrow(new CategoryNotFoundException(99L))
                .when(categoryService).delete(99L);

        mockMvc.perform(delete("/api/categories/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
