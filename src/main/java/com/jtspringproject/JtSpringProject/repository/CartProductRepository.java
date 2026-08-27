package com.jtspringproject.JtSpringProject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jtspringproject.JtSpringProject.models.Cart;
import com.jtspringproject.JtSpringProject.models.CartProduct;
import com.jtspringproject.JtSpringProject.models.CartProductId;
import com.jtspringproject.JtSpringProject.models.Product;

public interface CartProductRepository extends JpaRepository<CartProduct, CartProductId> {
    List<CartProduct> findByCart(Cart cart);

    @Query("SELECT cp.product FROM CartProduct cp WHERE cp.cart.id = :cartId")
    List<Product> findProductsByCartId(@Param("cartId") Integer cartId);
}
