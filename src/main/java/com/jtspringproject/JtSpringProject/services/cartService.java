package com.jtspringproject.JtSpringProject.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jtspringproject.JtSpringProject.models.Cart;
import com.jtspringproject.JtSpringProject.repository.CartRepository;

@Service
@Transactional(readOnly = true)
public class cartService {
    private final CartRepository cartRepository;

    @Autowired
    public cartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    @Transactional
    public Cart addCart(Cart cart) {
        return cartRepository.save(cart);
    }

    public List<Cart> getCarts() {
        return this.cartRepository.findAll();
    }

    @Transactional
    public void updateCart(Cart cart) {
        cartRepository.save(cart);
    }

    @Transactional
    public void deleteCart(Cart cart) {
        cartRepository.delete(cart);
    }
}
