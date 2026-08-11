package com.ecommerce.catalogservice.exception;

import java.time.LocalDateTime;

/**
 * Uniform JSON error envelope returned for every non-2xx response from
 * catalog-service. Serialized by Jackson; the five fields map directly to the
 * contract mandated by req-uniform-error-body.
 *
 * <pre>
 * {
 *   "timestamp": "2026-08-11T19:00:00",
 *   "status": 404,
 *   "error": "Not Found",
 *   "message": "Product not found with id: 99",
 *   "path": "/api/products/99"
 * }
 * </pre>
 */
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path
) {

    /**
     * Convenience factory — sets timestamp to now.
     */
    public static ErrorResponse of(int status, String error, String message, String path) {
        return new ErrorResponse(LocalDateTime.now(), status, error, message, path);
    }
}
