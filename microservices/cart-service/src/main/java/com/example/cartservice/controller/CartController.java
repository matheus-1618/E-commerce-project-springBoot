package com.example.cartservice.controller;

import com.example.cartservice.dto.CartItemResponse;
import com.example.cartservice.dto.UpdateQuantityRequest;
import com.example.cartservice.model.CartItem;
import com.example.cartservice.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * REST controller for cart item mutations — Phase 2.1.
 *
 * <p>Exposes two HTTP endpoints under the {@code /api/cart/items} prefix:
 * <ul>
 *   <li>{@code DELETE /api/cart/items/{itemId}} — remove an item from the cart</li>
 *   <li>{@code PUT /api/cart/items/{itemId}/quantity} — update item quantity</li>
 * </ul>
 *
 * <p>Per {@code dec-three-layer-architecture}: this class is a pure orchestration
 * layer. It delegates all business logic to {@link CartService} and performs no
 * repository access. Error handling is centralized in {@code CartControllerAdvice}.
 *
 * <p>Per {@code dec-bean-validation-at-controller}: {@code @Valid} is applied to
 * the {@link UpdateQuantityRequest} request body so that Bean Validation fires
 * before the service method is invoked. If validation fails,
 * {@code CartControllerAdvice.handleValidation} returns 400 immediately.
 *
 * <p>Per {@code dec-constructor-injection}: wiring is via constructor — no
 * field or setter injection.
 *
 * <p>Stack: Spring Boot 2.6.4 / Java 11 / {@code javax.validation.*}
 */
@RestController
@RequestMapping("/api/cart/items")
public class CartController {

    private final CartService cartService;

    /**
     * Creates the controller with its required service dependency.
     *
     * @param cartService the cart domain service; must not be null
     */
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // -------------------------------------------------------------------------
    // DELETE /api/cart/items/{itemId}
    // -------------------------------------------------------------------------

    /**
     * Removes a cart item identified by its primary key.
     *
     * <p>On success, returns HTTP 204 No Content with no response body (REST convention
     * for a successful delete that yields nothing meaningful to return).
     *
     * <p>On failure:
     * <ul>
     *   <li>404 — item does not exist (raised by service, caught by CartControllerAdvice)</li>
     *   <li>400 — {@code itemId} is not a valid Long (Spring type conversion failure,
     *       caught by CartControllerAdvice.handleTypeMismatch)</li>
     * </ul>
     *
     * @param itemId the primary key of the cart item to delete; Spring converts the
     *               path variable String to Long; conversion failure → 400
     * @return {@code ResponseEntity<Void>} with status 204 on success
     */
    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> removeItem(@PathVariable Long itemId) {
        cartService.removeItem(itemId);
        return ResponseEntity.noContent().build();
    }

    // -------------------------------------------------------------------------
    // PUT /api/cart/items/{itemId}/quantity
    // -------------------------------------------------------------------------

    /**
     * Updates the quantity of a cart item identified by its primary key.
     *
     * <p>The request body must be JSON with Content-Type: application/json and a
     * {@code quantity} field that is a positive integer ({@code >= 1}).
     *
     * <p>On success, returns HTTP 200 OK with the updated {@link CartItemResponse}
     * as the response body. The response reflects the persisted state (quantity
     * updated, all other fields unchanged).
     *
     * <p>On failure:
     * <ul>
     *   <li>400 — validation failure ({@code @NotNull} or {@code @Min(1)} on DTO)
     *       or malformed {@code itemId}</li>
     *   <li>404 — item does not exist</li>
     *   <li>415 — missing or wrong Content-Type (Spring default behaviour)</li>
     * </ul>
     *
     * @param itemId  the primary key of the item to update
     * @param request the validated request body containing the new quantity
     * @return {@code ResponseEntity<CartItemResponse>} with status 200 and updated body
     */
    @PutMapping("/{itemId}/quantity")
    public ResponseEntity<CartItemResponse> updateQuantity(
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateQuantityRequest request) {
        CartItem updated = cartService.updateQuantity(itemId, request.getQuantity());
        CartItemResponse response = CartItemResponse.from(updated);
        return ResponseEntity.ok(response);
    }
}
