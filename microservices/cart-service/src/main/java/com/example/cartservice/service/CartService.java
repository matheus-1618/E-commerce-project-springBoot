package com.example.cartservice.service;

import com.example.cartservice.model.CartItem;
import com.example.cartservice.repository.CartRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

/**
 * Domain service for cart mutations.
 *
 * <p>Single authoritative owner of business logic for the cart bounded context in
 * Phase 2.1. All interactions with {@link CartRepository} are routed through this
 * class — the controller and any other layer must never access the repository directly.
 *
 * <p>Transaction strategy: class-level {@code @Transactional} wraps every public method
 * in a transaction. Both {@code removeItem} and {@code updateQuantity} execute their
 * database reads and writes atomically. Spring rolls back on any unchecked exception.
 *
 * <p>Wiring: constructor injection is mandatory per {@code dec-constructor-injection}.
 * No field or setter injection is used.
 *
 * <p>Stack: Spring Boot 2.6.4 / Java 11 / {@code javax.persistence.*}
 */
@Service
@Transactional
public class CartService {

    private final CartRepository cartRepository;

    /**
     * Creates a CartService with the required repository dependency.
     *
     * @param cartRepository the JPA repository for CartItem entities; must not be null
     */
    public CartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /**
     * Removes a cart item by its primary key.
     *
     * <p>Algorithm:
     * <ol>
     *   <li>Check existence via {@link CartRepository#existsById(Object)}.</li>
     *   <li>If not found, throw {@link EntityNotFoundException} (maps to 404).</li>
     *   <li>Delete the row via {@link CartRepository#deleteById(Object)}.</li>
     * </ol>
     *
     * <p>Post-condition (success): the {@code cart_items} row with {@code id = itemId}
     * no longer exists in the database.
     *
     * @param itemId primary key of the item to remove; non-null (guaranteed by Spring
     *               path variable binding in the controller)
     * @throws EntityNotFoundException if no CartItem with the given id exists
     */
    public void removeItem(Long itemId) {
        if (!cartRepository.existsById(itemId)) {
            throw new EntityNotFoundException("CartItem not found: " + itemId);
        }
        cartRepository.deleteById(itemId);
    }

    /**
     * Updates the quantity of a cart item in-place.
     *
     * <p>Algorithm:
     * <ol>
     *   <li>Defensive precondition: if {@code quantity < 1}, throw
     *       {@link IllegalArgumentException}. This guard should never fire if the
     *       controller's {@code @Valid} annotation is wired correctly — it is a
     *       belt-and-suspenders safety net.</li>
     *   <li>Retrieve the item via {@link CartRepository#findById(Object)}; throw
     *       {@link EntityNotFoundException} if absent.</li>
     *   <li>Update the quantity field on the managed entity.</li>
     *   <li>Persist via {@link CartRepository#save(Object)} and return the saved
     *       entity.</li>
     * </ol>
     *
     * <p>Post-condition (success): the {@code cart_items} row with {@code id = itemId}
     * has {@code quantity = N} where N is the supplied value. The returned
     * {@link CartItem} reflects the persisted state.
     *
     * @param itemId   primary key of the item to update; non-null
     * @param quantity the new quantity; must be >= 1
     * @return the updated and persisted {@link CartItem}
     * @throws IllegalArgumentException if {@code quantity < 1} (defensive guard)
     * @throws EntityNotFoundException  if no CartItem with the given id exists
     */
    public CartItem updateQuantity(Long itemId, int quantity) {
        if (quantity < 1) {
            throw new IllegalArgumentException("quantity must be at least 1");
        }
        CartItem item = cartRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("CartItem not found: " + itemId));
        item.setQuantity(quantity);
        return cartRepository.save(item);
    }
}
