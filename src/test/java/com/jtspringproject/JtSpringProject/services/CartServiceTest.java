package com.jtspringproject.JtSpringProject.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.jtspringproject.JtSpringProject.models.Cart;
import com.jtspringproject.JtSpringProject.models.User;
import com.jtspringproject.JtSpringProject.repository.CartRepository;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private cartService service;

    private Cart testCart;

    @BeforeEach
    void setUp() {
        testCart = new Cart();
        testCart.setId(1);
        User user = new User();
        user.setId(1);
        user.setUsername("testuser");
        testCart.setCustomer(user);
    }

    @Test
    void addCart_savesAndReturns() {
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        Cart result = service.addCart(testCart);

        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(cartRepository).save(testCart);
    }

    @Test
    void getCarts_returnsList() {
        when(cartRepository.findAll()).thenReturn(Arrays.asList(testCart));

        List<Cart> result = service.getCarts();

        assertEquals(1, result.size());
    }

    @Test
    void updateCart_callsSave() {
        service.updateCart(testCart);

        verify(cartRepository).save(testCart);
    }

    @Test
    void deleteCart_callsDelete() {
        service.deleteCart(testCart);

        verify(cartRepository).delete(testCart);
    }
}
