package com.ecommerce.catalogservice.exception;

/**
 * Thrown by service classes when input passes Jakarta Bean Validation but fails
 * a business rule (e.g., negative stock after adjustment, price below cost).
 * Maps to HTTP 400 Bad Request via {@link GlobalExceptionHandler}.
 */
public class CatalogValidationException extends RuntimeException {

    public CatalogValidationException(String message) {
        super(message);
    }
}
