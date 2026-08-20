package com.jtspringproject.JtSpringProject.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jtspringproject.JtSpringProject.models.CartProduct;
import com.jtspringproject.JtSpringProject.models.Product;
import com.jtspringproject.JtSpringProject.repository.CartProductRepository;

@Service
@Transactional(readOnly = true)
public class CartProductService {
    private final CartProductRepository cartProductRepository;

    @Autowired
    public CartProductService(CartProductRepository cartProductRepository) {
        this.cartProductRepository = cartProductRepository;
    }

    public List<CartProduct> getCartProducts() {
        return this.cartProductRepository.findAll();
    }

    public List<Product> getProductsByCartId(Integer cartId) {
        return this.cartProductRepository.findProductsByCartId(cartId);
    }

    @Transactional
    public CartProduct addCartProduct(CartProduct cartProduct) {
        return this.cartProductRepository.save(cartProduct);
    }

    @Transactional
    public void deleteCartProduct(CartProduct cartProduct) {
        this.cartProductRepository.delete(cartProduct);
    }
}
