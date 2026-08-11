package com.example.cartservice.dto;

import com.example.cartservice.model.CartItem;

import java.math.BigDecimal;

/**
 * Outbound DTO for cart item state returned by the REST API.
 *
 * <p>Carries the cart item fields from the service layer to the HTTP response body.
 * Decouples the API contract from the {@link CartItem} JPA entity so that:
 * <ul>
 *   <li>Hibernate proxies and lazy-load internals are never accidentally serialized.</li>
 *   <li>Future entity schema evolution (renaming fields, adding JPA annotations) does
 *       not break the HTTP response contract observed by clients.</li>
 * </ul>
 *
 * <p>Per {@code dec-cart-item-response-dto}: this class is the stable API surface.
 * The entity is an implementation detail.
 *
 * <p>Stack: Spring Boot 2.6.4 / Java 11
 */
public class CartItemResponse {

    private Long id;
    private Long cartId;
    private Long productId;
    private String productName;
    private int quantity;
    private BigDecimal price;

    // -------------------------------------------------------------------------
    // Factory Method
    // -------------------------------------------------------------------------

    /**
     * One-way transformation from a {@link CartItem} entity to this response DTO.
     *
     * <p>Called by {@code CartController} after each successful mutation to build
     * the JSON response body. Never holds a back-reference to the entity.
     *
     * @param item the persisted (or saved) CartItem; must not be null
     * @return a new CartItemResponse populated from the entity's current state
     */
    public static CartItemResponse from(CartItem item) {
        CartItemResponse resp = new CartItemResponse();
        resp.setId(item.getId());
        resp.setCartId(item.getCartId());
        resp.setProductId(item.getProductId());
        resp.setProductName(item.getProductName());
        resp.setQuantity(item.getQuantity());
        resp.setPrice(item.getPrice());
        return resp;
    }

    // -------------------------------------------------------------------------
    // No-arg constructor (Jackson serialization)
    // -------------------------------------------------------------------------

    public CartItemResponse() {
    }

    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCartId() {
        return cartId;
    }

    public void setCartId(Long cartId) {
        this.cartId = cartId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
