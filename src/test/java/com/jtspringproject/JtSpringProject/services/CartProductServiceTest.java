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
import com.jtspringproject.JtSpringProject.models.CartProduct;
import com.jtspringproject.JtSpringProject.models.CartProductId;
import com.jtspringproject.JtSpringProject.models.Product;
import com.jtspringproject.JtSpringProject.repository.CartProductRepository;

@ExtendWith(MockitoExtension.class)
class CartProductServiceTest {

    @Mock
    private CartProductRepository cartProductRepository;

    @InjectMocks
    private CartProductService service;

    private CartProduct testCartProduct;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        Cart cart = new Cart();
        cart.setId(1);
        testProduct = new Product();
        testProduct.setId(1);
        testProduct.setName("Test Product");
        testCartProduct = new CartProduct(cart, testProduct);
    }

    @Test
    void getCartProducts_returnsList() {
        when(cartProductRepository.findAll()).thenReturn(Arrays.asList(testCartProduct));

        List<CartProduct> result = service.getCartProducts();

        assertEquals(1, result.size());
    }

    @Test
    void getProductsByCartId_returnsList() {
        when(cartProductRepository.findProductsByCartId(1)).thenReturn(Arrays.asList(testProduct));

        List<Product> result = service.getProductsByCartId(1);

        assertEquals(1, result.size());
        assertEquals("Test Product", result.get(0).getName());
    }

    @Test
    void addCartProduct_savesAndReturns() {
        when(cartProductRepository.save(any(CartProduct.class))).thenReturn(testCartProduct);

        CartProduct result = service.addCartProduct(testCartProduct);

        assertNotNull(result);
        verify(cartProductRepository).save(testCartProduct);
    }

    @Test
    void deleteCartProduct_callsDelete() {
        service.deleteCartProduct(testCartProduct);

        verify(cartProductRepository).delete(testCartProduct);
    }
}
