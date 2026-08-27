package com.jtspringproject.JtSpringProject.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.jtspringproject.JtSpringProject.models.Cart;
import com.jtspringproject.JtSpringProject.models.CartProduct;
import com.jtspringproject.JtSpringProject.models.CartProductId;
import com.jtspringproject.JtSpringProject.models.Category;
import com.jtspringproject.JtSpringProject.models.Product;
import com.jtspringproject.JtSpringProject.models.User;

@DataJpaTest
class CartProductRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CartProductRepository cartProductRepository;

    private Cart testCart;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setUsername("cartuser");
        user.setEmail("cart@test.com");
        user.setPassword("$2a$10$pass");
        user.setRole("ROLE_NORMAL");
        entityManager.persistAndFlush(user);

        Category category = new Category();
        category.setName("TestCategory");
        entityManager.persistAndFlush(category);

        testProduct = new Product();
        testProduct.setName("CartTestProduct");
        testProduct.setPrice(50);
        testProduct.setQuantity(5);
        testProduct.setWeight(1);
        testProduct.setCategory(category);
        entityManager.persistAndFlush(testProduct);

        testCart = new Cart();
        testCart.setCustomer(user);
        entityManager.persistAndFlush(testCart);

        CartProduct cartProduct = new CartProduct();
        cartProduct.setId(new CartProductId(testCart.getId(), testProduct.getId()));
        cartProduct.setCart(testCart);
        cartProduct.setProduct(testProduct);
        entityManager.persistAndFlush(cartProduct);
    }

    @Test
    void findProductsByCartId_returnsProducts() {
        List<Product> products = cartProductRepository.findProductsByCartId(testCart.getId());

        assertEquals(1, products.size());
        assertEquals("CartTestProduct", products.get(0).getName());
    }

    @Test
    void findProductsByCartId_returnsEmptyForUnknownCart() {
        List<Product> products = cartProductRepository.findProductsByCartId(99999);

        assertTrue(products.isEmpty());
    }

    @Test
    void findByCart_returnsCartProducts() {
        List<CartProduct> cartProducts = cartProductRepository.findByCart(testCart);

        assertEquals(1, cartProducts.size());
        assertEquals(testProduct.getId(), cartProducts.get(0).getProduct().getId());
    }
}
