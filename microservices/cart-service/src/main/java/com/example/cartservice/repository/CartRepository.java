package com.example.cartservice.repository;

import com.example.cartservice.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for {@link CartItem} entities.
 *
 * <p>Extends {@link JpaRepository} to provide the full CRUD surface. No custom
 * query methods are required in Phase 2.1 — the standard inherited operations
 * ({@code findById}, {@code delete}, {@code save}) are sufficient for the two
 * REST mutations (remove item, update quantity).
 *
 * <p>Spring Data JPA generates the implementation proxy at application startup via
 * {@code @EnableJpaRepositories} auto-configuration. No {@code @Repository} annotation
 * is needed on this interface; the {@code JpaRepository} extension is sufficient for
 * component scanning.
 *
 * <p>Operations used by CartService (Phase 2.1):
 * <ul>
 *   <li>{@code findById(Long id)} — existence check before delete or update;
 *       returns {@code Optional<CartItem>}</li>
 *   <li>{@code delete(CartItem entity)} — hard DELETE for removeItem</li>
 *   <li>{@code save(CartItem entity)} — UPDATE for updateQuantity</li>
 * </ul>
 *
 * <p>All calls go through this interface. Raw JDBC is forbidden in this module
 * (BR-F-08 per business-rules artifact).
 */
public interface CartRepository extends JpaRepository<CartItem, Long> {
    // No custom queries required in Phase 2.1.
    // JpaRepository<CartItem, Long> provides: findById, delete, save, and the full CRUD surface.
}
