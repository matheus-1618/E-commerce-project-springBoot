package com.example.cartservice.controller;

import com.example.cartservice.dto.UpdateQuantityRequest;
import com.example.cartservice.model.CartItem;
import com.example.cartservice.service.CartService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for {@link CartController} using the Spring MVC test slice.
 *
 * <p>{@code @WebMvcTest} wires only the MVC layer (controller, advice, filters) —
 * no full application context, no database. {@code CartService} is a {@code @MockBean}
 * that allows Mockito to control its return values and thrown exceptions.
 *
 * <p>Coverage targets (per req-unit-test-coverage-gate):
 * <ul>
 *   <li>DELETE 204 happy path</li>
 *   <li>DELETE 404 (item not found)</li>
 *   <li>DELETE 400 (malformed itemId path variable)</li>
 *   <li>PUT 200 with correct response body</li>
 *   <li>PUT 400 (validation — quantity < 1)</li>
 *   <li>PUT 400 (validation — quantity null / missing)</li>
 *   <li>PUT 404 (item not found)</li>
 * </ul>
 */
@WebMvcTest(CartController.class)
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CartService cartService;

    // -------------------------------------------------------------------------
    // DELETE /api/cart/items/{itemId} — happy path
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("DELETE /api/cart/items/{itemId}: existing item returns 204 No Content")
    void deleteItem_existingItem_returns204() throws Exception {
        // Arrange
        doNothing().when(cartService).removeItem(1L);

        // Act + Assert
        mockMvc.perform(delete("/api/cart/items/1"))
                .andExpect(status().isNoContent());
    }

    // -------------------------------------------------------------------------
    // DELETE — item not found
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("DELETE /api/cart/items/{itemId}: missing item returns 404 with RFC 7807 body")
    void deleteItem_missingItem_returns404() throws Exception {
        // Arrange
        doThrow(new EntityNotFoundException("CartItem not found: 99"))
                .when(cartService).removeItem(99L);

        // Act + Assert
        mockMvc.perform(delete("/api/cart/items/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.detail").value("CartItem not found: 99"));
    }

    // -------------------------------------------------------------------------
    // DELETE — malformed itemId (400)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("DELETE /api/cart/items/{itemId}: non-numeric itemId returns 400")
    void deleteItem_malformedItemId_returns400() throws Exception {
        // Act + Assert — "abc" cannot be converted to Long
        mockMvc.perform(delete("/api/cart/items/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Bad Request"));
    }

    // -------------------------------------------------------------------------
    // PUT /api/cart/items/{itemId}/quantity — happy path
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("PUT /api/cart/items/{itemId}/quantity: valid request returns 200 with body")
    void updateQuantity_validRequest_returns200WithBody() throws Exception {
        // Arrange
        CartItem saved = new CartItem(1L, 42L, "Widget A", 3, new BigDecimal("9.99"));
        when(cartService.updateQuantity(eq(1L), eq(3))).thenReturn(saved);

        UpdateQuantityRequest request = new UpdateQuantityRequest(3);
        String body = objectMapper.writeValueAsString(request);

        // Act + Assert
        mockMvc.perform(put("/api/cart/items/1/quantity")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.quantity").value(3))
                .andExpect(jsonPath("$.productName").value("Widget A"))
                .andExpect(jsonPath("$.cartId").value(1));
    }

    // -------------------------------------------------------------------------
    // PUT — validation failure: quantity < 1
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("PUT /api/cart/items/{itemId}/quantity: quantity=0 returns 400")
    void updateQuantity_zeroQuantity_returns400() throws Exception {
        // Arrange
        UpdateQuantityRequest request = new UpdateQuantityRequest(0);
        String body = objectMapper.writeValueAsString(request);

        // Act + Assert
        mockMvc.perform(put("/api/cart/items/1/quantity")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("quantity: quantity must be at least 1"));
    }

    // -------------------------------------------------------------------------
    // PUT — validation failure: quantity missing (null in JSON)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("PUT /api/cart/items/{itemId}/quantity: missing quantity field returns 400")
    void updateQuantity_missingQuantity_returns400() throws Exception {
        // Arrange — empty object body: {} — quantity field is absent
        String body = "{}";

        // Act + Assert
        mockMvc.perform(put("/api/cart/items/1/quantity")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Bad Request"));
    }

    // -------------------------------------------------------------------------
    // PUT — item not found
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("PUT /api/cart/items/{itemId}/quantity: missing item returns 404")
    void updateQuantity_missingItem_returns404() throws Exception {
        // Arrange
        when(cartService.updateQuantity(eq(77L), any(Integer.class)))
                .thenThrow(new EntityNotFoundException("CartItem not found: 77"));

        UpdateQuantityRequest request = new UpdateQuantityRequest(2);
        String body = objectMapper.writeValueAsString(request);

        // Act + Assert
        mockMvc.perform(put("/api/cart/items/77/quantity")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.detail").value("CartItem not found: 77"));
    }

    // -------------------------------------------------------------------------
    // PUT — malformed itemId
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("PUT /api/cart/items/{itemId}/quantity: non-numeric itemId returns 400")
    void updateQuantity_malformedItemId_returns400() throws Exception {
        // Arrange
        UpdateQuantityRequest request = new UpdateQuantityRequest(2);
        String body = objectMapper.writeValueAsString(request);

        // Act + Assert
        mockMvc.perform(put("/api/cart/items/xyz/quantity")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Bad Request"));
    }

    // -------------------------------------------------------------------------
    // PUT — unhandled exception → 500 (covers CartControllerAdvice.handleGeneral)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("PUT /api/cart/items/{itemId}/quantity: unexpected service error returns 500 with opaque detail")
    void updateQuantity_unexpectedError_returns500() throws Exception {
        // Arrange — simulate an unhandled RuntimeException from CartService
        when(cartService.updateQuantity(eq(1L), eq(2)))
                .thenThrow(new RuntimeException("Unexpected DB error"));

        UpdateQuantityRequest request = new UpdateQuantityRequest(2);
        String body = objectMapper.writeValueAsString(request);

        // Act + Assert — 500 with opaque detail (no internal message leaked per SR-DP-01)
        mockMvc.perform(put("/api/cart/items/1/quantity")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.title").value("Internal Server Error"))
                .andExpect(jsonPath("$.detail").value("An unexpected error occurred. Please try again later."));
    }
}
