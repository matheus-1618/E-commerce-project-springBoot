package com.ecommerce.catalogservice.exception;

/**
 * Thrown by {@link com.ecommerce.catalogservice.service.CategoryService} when a
 * requested category ID does not exist in the catalog.
 * Maps to HTTP 404 Not Found via {@link GlobalExceptionHandler}.
 */
public class CategoryNotFoundException extends RuntimeException {

    public CategoryNotFoundException(Long id) {
        super("Category not found with id: " + id);
    }

    public CategoryNotFoundException(String message) {
        super(message);
    }
}
