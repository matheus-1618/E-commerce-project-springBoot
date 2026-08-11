package com.ecommerce.catalogservice.exception;

/**
 * Thrown by {@link com.ecommerce.catalogservice.service.ProductService} when a
 * requested product ID does not exist in the catalog.
 * Maps to HTTP 404 Not Found via {@link GlobalExceptionHandler}.
 */
public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(Long id) {
        super("Product not found with id: " + id);
    }

    public ProductNotFoundException(String message) {
        super(message);
    }
}
