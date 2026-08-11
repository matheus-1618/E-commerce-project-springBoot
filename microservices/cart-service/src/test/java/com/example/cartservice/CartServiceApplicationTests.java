package com.example.cartservice;

import com.example.cartservice.model.CartItem;
import com.example.cartservice.repository.CartRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Walking-skeleton smoke tests for the cart-service-foundation unit.
 *
 * <p>These tests verify that:
 * <ol>
 *   <li>The Spring application context loads without errors</li>
 *   <li>The {@link CartRepository} bean is wired and functional</li>
 *   <li>The {@link CartItem} entity is correctly mapped and persistable</li>
 * </ol>
 *
 * <p>This is the gate-pass for U-01 (cart-service-foundation). All three tests
 * must be green before the rest-layer unit proceeds.
 */
@SpringBootTest
class CartServiceApplicationTests {

    @Autowired
    private CartRepository cartRepository;

    /**
     * Verifies that the Spring Boot application context starts and the
     * CartRepository bean is present and injected. This is the walking-skeleton
     * acceptance signal per unit-of-work U-01.
     */
    @Test
    void contextLoads() {
        assertThat(cartRepository).isNotNull();
    }

    /**
     * Verifies that a CartItem can be persisted and retrieved via CartRepository.
     * Confirms that:
     * - save() assigns a non-null id (IDENTITY strategy)
     * - findById() retrieves the exact entity by id
     * - all six fields are stored and returned correctly
     */
    @Test
    void cartItemCanBePersistedAndRetrieved() {
        // Arrange
        CartItem item = new CartItem(
                10L,                         // cartId
                42L,                         // productId
                "Widget Pro",                // productName
                3,                           // quantity
                new BigDecimal("19.99")      // price
        );

        // Act
        CartItem saved = cartRepository.save(item);
        Optional<CartItem> found = cartRepository.findById(saved.getId());

        // Assert — id was assigned by IDENTITY strategy
        assertThat(saved.getId()).isNotNull();

        // Assert — all fields round-trip correctly
        assertThat(found).isPresent();
        CartItem retrieved = found.get();
        assertThat(retrieved.getCartId()).isEqualTo(10L);
        assertThat(retrieved.getProductId()).isEqualTo(42L);
        assertThat(retrieved.getProductName()).isEqualTo("Widget Pro");
        assertThat(retrieved.getQuantity()).isEqualTo(3);
        assertThat(retrieved.getPrice()).isEqualByComparingTo(new BigDecimal("19.99"));
    }

    /**
     * Verifies that a CartItem can be deleted via CartRepository.delete().
     * After deletion, findById() returns Optional.empty() for the same id.
     */
    @Test
    void cartItemCanBeDeleted() {
        // Arrange
        CartItem item = new CartItem(
                20L,
                99L,
                "Gadget Basic",
                1,
                new BigDecimal("5.00")
        );
        CartItem saved = cartRepository.save(item);
        Long savedId = saved.getId();

        // Act
        cartRepository.delete(saved);
        Optional<CartItem> afterDelete = cartRepository.findById(savedId);

        // Assert — entity is no longer present
        assertThat(afterDelete).isEmpty();
    }
}
