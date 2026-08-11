package com.ecommerce.catalogservice.controller;

import com.ecommerce.catalogservice.dto.CategoryDto;
import com.ecommerce.catalogservice.dto.CreateCategoryRequest;
import com.ecommerce.catalogservice.dto.UpdateCategoryRequest;
import com.ecommerce.catalogservice.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for category operations.
 *
 * <p>This controller delegates all business logic to {@link CategoryService} and
 * does NOT catch exceptions or produce error responses directly — all error
 * handling is the responsibility of {@link com.ecommerce.catalogservice.exception.GlobalExceptionHandler}
 * (req-global-exception-handler).
 */
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /** GET /api/categories — list all categories. */
    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        return ResponseEntity.ok(categoryService.findAll());
    }

    /** GET /api/categories/{id} — get one category (404 if not found). */
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.findById(id));
    }

    /** POST /api/categories — create a new category (201 on success). */
    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(
            @Valid @RequestBody CreateCategoryRequest request) {
        CategoryDto created = categoryService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /** PUT /api/categories/{id} — update an existing category. */
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDto> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCategoryRequest request) {
        return ResponseEntity.ok(categoryService.update(id, request));
    }

    /** DELETE /api/categories/{id} — delete a category (204 on success). */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
