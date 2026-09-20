package com.jtspringproject.JtSpringProject.models;

import javax.persistence.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@Table(name = "CART_PRODUCT")
public class CartProduct {
    private static final Logger logger = LoggerFactory.getLogger(CartProduct.class);

    @EmbeddedId
    private CartProductId id;

    @ManyToOne
    @MapsId("cartId")
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private Product product;

    public CartProduct() {}

    public CartProduct(Cart cart, Product product) {
        this.cart = cart;
        this.product = product;
        this.id = new CartProductId(cart.getId(), product.getId());
    }

    public CartProductId getId() {
        return id;
    }

    public void setId(CartProductId id) {
        this.id = id;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
