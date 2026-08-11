package com.example.cartservice.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Inbound DTO for the PUT /api/cart/items/{itemId}/quantity endpoint.
 *
 * <p>Carries the desired new quantity from the HTTP request body to the controller.
 * Bean Validation annotations enforce the business invariant (quantity >= 1) at the
 * API boundary, before the service layer is ever invoked.
 *
 * <p>Design notes:
 * <ul>
 *   <li>{@code quantity} is declared as boxed {@code Integer} (not primitive {@code int})
 *       so that a missing JSON field deserializes to {@code null} rather than {@code 0}.
 *       This allows {@code @NotNull} to fire and return 400 when the caller omits the
 *       field entirely. With a primitive {@code int}, the field would silently default
 *       to 0 and only {@code @Min(1)} would fire — an identical HTTP 400 outcome, but
 *       with a less accurate error message.</li>
 *   <li>Standard JavaBeans getters/setters are required for Jackson deserialization
 *       under Spring Boot 2.6.4 without additional ObjectMapper configuration.</li>
 * </ul>
 *
 * <p>Stack: Spring Boot 2.6.4 / Java 11 / {@code javax.validation.*}
 */
public class UpdateQuantityRequest {

    /**
     * The desired quantity for the cart item. Must be present and >= 1.
     *
     * <p>Boxed {@code Integer} to enable {@code @NotNull} detection of a missing JSON field.
     */
    @NotNull(message = "quantity is required")
    @Min(value = 1, message = "quantity must be at least 1")
    private Integer quantity;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /** No-arg constructor required by Jackson for deserialization. */
    public UpdateQuantityRequest() {
    }

    /**
     * Convenience constructor for tests.
     *
     * @param quantity the desired quantity (must be >= 1 to pass validation)
     */
    public UpdateQuantityRequest(Integer quantity) {
        this.quantity = quantity;
    }

    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
