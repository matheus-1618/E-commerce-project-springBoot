package com.ecommerce.catalogservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * Centralized exception handler for catalog-service.
 *
 * <p>All HTTP error response production is the sole responsibility of this class;
 * no controller or service class may catch exceptions and produce error responses
 * directly (req-global-exception-handler).
 *
 * <p>Every handler method returns a uniform {@link ErrorResponse} JSON body
 * (req-uniform-error-body) with the five fields: timestamp, status, error,
 * message, path.
 *
 * <p>Mapping table:
 * <ul>
 *   <li>{@link ProductNotFoundException}, {@link CategoryNotFoundException} → 404 (req-404-mapping)</li>
 *   <li>{@link CatalogValidationException}, {@link MethodArgumentNotValidException} → 400 (req-400-mapping)</li>
 *   <li>{@link CatalogConflictException} → 409 (req-409-mapping)</li>
 *   <li>{@link Exception} catch-all → 500 (req-500-mapping)</li>
 * </ul>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // -------------------------------------------------------------------------
    // 404 — Not Found
    // -------------------------------------------------------------------------

    /**
     * Handles {@link ProductNotFoundException} (req-404-mapping).
     * Returns HTTP 404 with the uniform error envelope.
     */
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFound(
            ProductNotFoundException ex,
            HttpServletRequest request) {

        ErrorResponse body = ErrorResponse.of(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    /**
     * Handles {@link CategoryNotFoundException} (req-404-mapping).
     * Returns HTTP 404 with the uniform error envelope.
     */
    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCategoryNotFound(
            CategoryNotFoundException ex,
            HttpServletRequest request) {

        ErrorResponse body = ErrorResponse.of(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    // -------------------------------------------------------------------------
    // 400 — Bad Request
    // -------------------------------------------------------------------------

    /**
     * Handles {@link CatalogValidationException} — business-rule violations
     * that pass Bean Validation but fail domain logic (req-400-mapping).
     * Returns HTTP 400 with the uniform error envelope.
     */
    @ExceptionHandler(CatalogValidationException.class)
    public ResponseEntity<ErrorResponse> handleCatalogValidation(
            CatalogValidationException ex,
            HttpServletRequest request) {

        ErrorResponse body = ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Handles {@link MethodArgumentNotValidException} — Jakarta Bean Validation
     * failures on @RequestBody DTOs (req-400-mapping).
     * Returns HTTP 400; the message field aggregates all field-level constraint
     * violations for human-readable detail.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));

        ErrorResponse body = ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                message.isEmpty() ? "Validation failed" : message,
                request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // -------------------------------------------------------------------------
    // 409 — Conflict
    // -------------------------------------------------------------------------

    /**
     * Handles {@link CatalogConflictException} — duplicate-entity conflicts
     * (req-409-mapping).
     * Returns HTTP 409 with the uniform error envelope.
     */
    @ExceptionHandler(CatalogConflictException.class)
    public ResponseEntity<ErrorResponse> handleCatalogConflict(
            CatalogConflictException ex,
            HttpServletRequest request) {

        ErrorResponse body = ErrorResponse.of(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    // -------------------------------------------------------------------------
    // 500 — Internal Server Error (catch-all)
    // -------------------------------------------------------------------------

    /**
     * Catch-all handler for any unhandled {@link Exception} (req-500-mapping).
     * Returns HTTP 500; the raw exception message is used but stack trace is
     * never exposed in the response body.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        ErrorResponse body = ErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "An unexpected error occurred",
                request.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
