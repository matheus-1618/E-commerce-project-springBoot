package com.example.cartservice.exception;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.persistence.EntityNotFoundException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Global exception handler — RFC 7807-aligned error responses for all cart endpoints.
 *
 * <p>Spring Boot 2.6.4 does not provide {@code org.springframework.http.ProblemDetail}
 * (introduced in Spring Boot 3.0). This class hand-crafts the RFC 7807 JSON structure
 * using {@link LinkedHashMap} to guarantee consistent field insertion order:
 * {@code type → title → status → detail}.
 *
 * <p>Per {@code dec-single-controller-advice}: exactly ONE {@code @RestControllerAdvice}
 * exists in the cart-service. All exception-to-HTTP mapping is centralized here.
 *
 * <p>Handler dispatch table:
 * <table>
 *   <tr><th>Exception</th><th>HTTP Status</th><th>detail source</th></tr>
 *   <tr><td>EntityNotFoundException</td><td>404</td><td>ex.getMessage()</td></tr>
 *   <tr><td>MethodArgumentNotValidException</td><td>400</td><td>First field error</td></tr>
 *   <tr><td>MethodArgumentTypeMismatchException</td><td>400</td><td>Parameter name + type</td></tr>
 *   <tr><td>Any other Exception</td><td>500</td><td>Opaque "Internal server error"</td></tr>
 * </table>
 *
 * <p>Security note (SR-DP-01): the catch-all 500 handler intentionally returns an opaque
 * message. The real exception is NOT included in the response body to prevent information
 * leakage — internal stack traces and database error details stay server-side.
 *
 * <p>Stack: Spring Boot 2.6.4 / Java 11 / {@code javax.persistence.*}
 */
@RestControllerAdvice
public class CartControllerAdvice {

    // -------------------------------------------------------------------------
    // 404 — Entity not found
    // -------------------------------------------------------------------------

    /**
     * Maps {@link EntityNotFoundException} to HTTP 404.
     *
     * <p>Thrown by {@code CartService.removeItem} and {@code CartService.updateQuantity}
     * when the requested cart item does not exist in the database.
     *
     * @param ex the exception containing the resource-specific detail message
     * @return RFC 7807 JSON body with status 404
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(EntityNotFoundException ex) {
        Map<String, Object> body = buildBody("Not Found", 404, ex.getMessage());
        return ResponseEntity.status(404)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    // -------------------------------------------------------------------------
    // 400 — Bean Validation failure
    // -------------------------------------------------------------------------

    /**
     * Maps {@link MethodArgumentNotValidException} to HTTP 400.
     *
     * <p>Fired by Spring's {@code @Valid} processing when the
     * {@code UpdateQuantityRequest} DTO fails {@code @NotNull} or {@code @Min(1)}
     * validation. Extracts the first field error to build a specific detail message.
     *
     * @param ex the validation exception; contains one or more field errors
     * @return RFC 7807 JSON body with status 400 and a field-level detail string
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
            MethodArgumentNotValidException ex) {
        String detail = "Validation failed";
        if (ex.getBindingResult().getFieldError() != null) {
            org.springframework.validation.FieldError fe =
                    ex.getBindingResult().getFieldError();
            detail = fe.getField() + ": " + fe.getDefaultMessage();
        }
        Map<String, Object> body = buildBody("Bad Request", 400, detail);
        return ResponseEntity.status(400)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    // -------------------------------------------------------------------------
    // 400 — Path variable type mismatch (e.g. "abc" for a Long {itemId})
    // -------------------------------------------------------------------------

    /**
     * Maps {@link MethodArgumentTypeMismatchException} to HTTP 400.
     *
     * <p>Fired when Spring cannot convert a path variable (e.g. {@code {itemId}})
     * to the required Java type ({@code Long}). Without this dedicated handler the
     * exception would fall through to the catch-all and incorrectly return 500.
     *
     * @param ex the type-mismatch exception
     * @return RFC 7807 JSON body with status 400
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex) {
        String requiredType = ex.getRequiredType() != null
                ? ex.getRequiredType().getSimpleName()
                : "unknown";
        String detail = ex.getName() + " must be of type " + requiredType;
        Map<String, Object> body = buildBody("Bad Request", 400, detail);
        return ResponseEntity.status(400)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    // -------------------------------------------------------------------------
    // 500 — Catch-all for unexpected errors
    // -------------------------------------------------------------------------

    /**
     * Catch-all handler for any unhandled {@link Exception}.
     *
     * <p>Returns an opaque 500 response that does NOT include the real exception
     * message, preventing internal implementation details from leaking to clients
     * (SR-DP-01 per security-design artifact).
     *
     * @param ex the unhandled exception (logged server-side, not surfaced to client)
     * @return RFC 7807 JSON body with status 500 and a generic detail string
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
        Map<String, Object> body = buildBody(
                "Internal Server Error",
                500,
                "An unexpected error occurred. Please try again later.");
        return ResponseEntity.status(500)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    // -------------------------------------------------------------------------
    // Shared builder
    // -------------------------------------------------------------------------

    /**
     * Constructs the RFC 7807-aligned response body.
     *
     * <p>{@link LinkedHashMap} is used to preserve insertion order so that the
     * serialized JSON consistently reads: {@code type, title, status, detail}.
     *
     * @param title  HTTP reason phrase (e.g. "Not Found", "Bad Request")
     * @param status HTTP status code integer
     * @param detail specific error description for the client
     * @return an ordered map ready for Jackson serialization
     */
    private Map<String, Object> buildBody(String title, int status, String detail) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("type", "about:blank");
        body.put("title", title);
        body.put("status", status);
        body.put("detail", detail);
        return body;
    }
}
