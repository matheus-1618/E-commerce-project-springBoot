package com.ecommerce.catalogservice.exception;

/**
 * Thrown by service classes when a create or update operation would create a
 * duplicate entity (e.g., duplicate category name).
 * Maps to HTTP 409 Conflict via {@link GlobalExceptionHandler}.
 */
public class CatalogConflictException extends RuntimeException {

    public CatalogConflictException(String message) {
        super(message);
    }
}
