package com.example.cartservice.service;

import com.example.cartservice.model.CartItem;
import com.example.cartservice.repository.CartRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link CartService}.
 *
 * <p>Uses Mockito to mock {@link CartRepository} — no Spring context, no database.
 * Tests run in pure JVM scope for maximum isolation and speed.
 *
 * <p>Coverage targets (per req-unit-test-coverage-gate):
 * <ul>
 *   <li>{@code removeItem}: happy path, not-found path</li>
 *   <li>{@code updateQuantity}: happy path, not-found path, quantity < 1 guard</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private CartService cartService;

    // -------------------------------------------------------------------------
    // removeItem — happy path
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("removeItem: existing item is deleted successfully")
    void removeItem_existingItem_deletesSuccessfully() {
        // Arrange
        Long itemId = 1L;
        when(cartRepository.existsById(itemId)).thenReturn(true);

        // Act — no exception should be thrown
        cartService.removeItem(itemId);

        // Assert — repository methods called in correct order
        verify(cartRepository).existsById(itemId);
        verify(cartRepository).deleteById(itemId);
    }

    // -------------------------------------------------------------------------
    // removeItem — not found
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("removeItem: missing item throws EntityNotFoundException")
    void removeItem_missingItem_throwsEntityNotFoundException() {
        // Arrange
        Long itemId = 99L;
        when(cartRepository.existsById(itemId)).thenReturn(false);

        // Act + Assert
        assertThatThrownBy(() -> cartService.removeItem(itemId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("CartItem not found: 99");

        // Assert — deleteById must NOT be called when item does not exist
        verify(cartRepository, never()).deleteById(any());
    }

    // -------------------------------------------------------------------------
    // updateQuantity — happy path
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("updateQuantity: valid quantity updates item and returns saved entity")
    void updateQuantity_validQuantity_updatesAndReturnsSaved() {
        // Arrange
        Long itemId = 2L;
        int newQuantity = 5;
        CartItem existing = new CartItem(10L, 42L, "Widget A", 2, new BigDecimal("9.99"));
        CartItem saved = new CartItem(10L, 42L, "Widget A", newQuantity, new BigDecimal("9.99"));

        when(cartRepository.findById(itemId)).thenReturn(Optional.of(existing));
        when(cartRepository.save(existing)).thenReturn(saved);

        // Act
        CartItem result = cartService.updateQuantity(itemId, newQuantity);

        // Assert — quantity was mutated on entity before save
        assertThat(existing.getQuantity()).isEqualTo(newQuantity);
        verify(cartRepository).save(existing);

        // Assert — returned entity has new quantity
        assertThat(result.getQuantity()).isEqualTo(newQuantity);
    }

    // -------------------------------------------------------------------------
    // updateQuantity — not found
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("updateQuantity: missing item throws EntityNotFoundException")
    void updateQuantity_missingItem_throwsEntityNotFoundException() {
        // Arrange
        Long itemId = 77L;
        when(cartRepository.findById(itemId)).thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> cartService.updateQuantity(itemId, 3))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("CartItem not found: 77");

        // Assert — save is never called when item does not exist
        verify(cartRepository, never()).save(any());
    }

    // -------------------------------------------------------------------------
    // updateQuantity — defensive quantity guard
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("updateQuantity: quantity=0 triggers defensive IllegalArgumentException")
    void updateQuantity_zeroQuantity_throwsIllegalArgumentException() {
        // Act + Assert — defensive guard fires before repository access
        assertThatThrownBy(() -> cartService.updateQuantity(1L, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("quantity must be at least 1");

        // Assert — no repository call is made
        verify(cartRepository, never()).findById(any());
    }

    @Test
    @DisplayName("updateQuantity: negative quantity triggers defensive IllegalArgumentException")
    void updateQuantity_negativeQuantity_throwsIllegalArgumentException() {
        // Act + Assert
        assertThatThrownBy(() -> cartService.updateQuantity(1L, -5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("quantity must be at least 1");

        verify(cartRepository, never()).findById(any());
    }

    // -------------------------------------------------------------------------
    // removeItem — verifies existsById is called with the correct id
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("removeItem: existsById is called with the exact itemId provided")
    void removeItem_callsExistsByIdWithCorrectId() {
        // Arrange
        Long itemId = 42L;
        when(cartRepository.existsById(itemId)).thenReturn(true);

        // Act
        cartService.removeItem(itemId);

        // Assert — correct id passed to repository
        verify(cartRepository).existsById(eq(42L));
        verify(cartRepository).deleteById(eq(42L));
    }

    // -------------------------------------------------------------------------
    // updateQuantity — quantity=1 (boundary: minimum valid value)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("updateQuantity: quantity=1 (minimum valid) succeeds")
    void updateQuantity_minimumQuantityOne_succeeds() {
        // Arrange
        Long itemId = 3L;
        CartItem existing = new CartItem(1L, 7L, "Gadget", 5, new BigDecimal("14.50"));
        when(cartRepository.findById(itemId)).thenReturn(Optional.of(existing));
        when(cartRepository.save(existing)).thenReturn(existing);

        // Act — should NOT throw
        CartItem result = cartService.updateQuantity(itemId, 1);

        // Assert
        assertThat(result.getQuantity()).isEqualTo(1);
        verify(cartRepository).save(existing);
    }
}
