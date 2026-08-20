package com.jtspringproject.JtSpringProject.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.jtspringproject.JtSpringProject.models.Product;
import com.jtspringproject.JtSpringProject.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private productService service;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1);
        testProduct.setName("Test Product");
        testProduct.setPrice(100);
        testProduct.setQuantity(10);
    }

    @Test
    void getProducts_returnsList() {
        when(productRepository.findAll()).thenReturn(Arrays.asList(testProduct));

        List<Product> result = service.getProducts();

        assertEquals(1, result.size());
        assertEquals("Test Product", result.get(0).getName());
    }

    @Test
    void addProduct_savesAndReturns() {
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        Product result = service.addProduct(testProduct);

        assertNotNull(result);
        verify(productRepository).save(testProduct);
    }

    @Test
    void getProduct_returnsProductById() {
        when(productRepository.findById(1)).thenReturn(Optional.of(testProduct));

        Product result = service.getProduct(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
    }

    @Test
    void getProduct_returnsNullWhenNotFound() {
        when(productRepository.findById(99)).thenReturn(Optional.empty());

        assertNull(service.getProduct(99));
    }

    @Test
    void updateProduct_setsIdAndSaves() {
        Product updatedProduct = new Product();
        updatedProduct.setName("Updated");
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        Product result = service.updateProduct(5, updatedProduct);

        assertEquals(5, updatedProduct.getId());
        verify(productRepository).save(updatedProduct);
    }

    @Test
    void deleteProduct_returnsTrueWhenExists() {
        when(productRepository.existsById(1)).thenReturn(true);

        assertTrue(service.deleteProduct(1));
        verify(productRepository).deleteById(1);
    }

    @Test
    void deleteProduct_returnsFalseWhenNotExists() {
        when(productRepository.existsById(99)).thenReturn(false);

        assertFalse(service.deleteProduct(99));
        verify(productRepository, never()).deleteById(any());
    }
}
