package com.jtspringproject.JtSpringProject.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.jtspringproject.JtSpringProject.models.Product;
import com.jtspringproject.JtSpringProject.models.User;
import com.jtspringproject.JtSpringProject.services.productService;
import com.jtspringproject.JtSpringProject.services.userService;

/**
 * Verifies that exceptions thrown by service beans propagate through
 * UserController to GlobalExceptionHandler without being swallowed.
 *
 * <p>Per contract-exception-propagation: service throws → controller propagates
 * → handler catches → safe HTTP response. CWE-209 sentinel assertions confirm
 * no exception detail leaks into the response body.</p>
 */
@WebMvcTest(
    controllers = {UserController.class, GlobalExceptionHandler.class},
    excludeAutoConfiguration = {
        HibernateJpaAutoConfiguration.class,
        SecurityAutoConfiguration.class
    }
)
class UserControllerExceptionPropagationTest {

    private static final String SENTINEL_MESSAGE = "SENTINEL_USER_PROPAGATION_55443";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private userService userService;

    @MockBean
    private productService productService;

    // -------------------------------------------------------------------------
    // Exception propagation — RuntimeException → HTTP 500 (BR-06)
    // -------------------------------------------------------------------------

    @Test
    void getUserProducts_whenServiceThrowsRuntimeException_returns500AndErrorView() throws Exception {
        given(productService.getProducts()).willThrow(new RuntimeException(SENTINEL_MESSAGE));

        mockMvc.perform(get("/user/products"))
                .andExpect(status().isInternalServerError())
                .andExpect(view().name("error"));
    }

    @Test
    void getUserProducts_whenServiceThrowsRuntimeException_doesNotLeakExceptionMessage() throws Exception {
        given(productService.getProducts()).willThrow(new RuntimeException(SENTINEL_MESSAGE));

        mockMvc.perform(get("/user/products"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(not(containsString(SENTINEL_MESSAGE))));
    }

    @Test
    void registerNewUser_whenAddUserThrowsIllegalStateException_returns500AndErrorView() throws Exception {
        given(userService.checkUserExists("newuser")).willReturn(false);
        given(userService.addUser(any(User.class)))
                .willThrow(new IllegalStateException(SENTINEL_MESSAGE));

        mockMvc.perform(post("/newuserregister")
                        .param("username", "newuser")
                        .param("email", "new@example.com")
                        .param("password", "pass123")
                        .param("address", "123 Main St"))
                .andExpect(status().isInternalServerError())
                .andExpect(view().name("error"));
    }

    @Test
    void registerNewUser_whenAddUserThrowsIllegalStateException_doesNotLeakExceptionMessage() throws Exception {
        given(userService.checkUserExists("newuser")).willReturn(false);
        given(userService.addUser(any(User.class)))
                .willThrow(new IllegalStateException(SENTINEL_MESSAGE));

        mockMvc.perform(post("/newuserregister")
                        .param("username", "newuser")
                        .param("email", "new@example.com")
                        .param("password", "pass123")
                        .param("address", "123 Main St"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(not(containsString(SENTINEL_MESSAGE))));
    }

    // -------------------------------------------------------------------------
    // Happy-path regression guard (BR-05)
    // -------------------------------------------------------------------------

    @Test
    void getUserProducts_whenServiceReturnsList_returns200AndUproductView() throws Exception {
        Product product = new Product();
        product.setId(1);
        product.setName("Test Product");
        given(productService.getProducts()).willReturn(List.of(product));

        mockMvc.perform(get("/user/products"))
                .andExpect(status().isOk())
                .andExpect(view().name("uproduct"))
                .andExpect(model().attributeExists("products"));
    }

    @Test
    void registerNewUser_whenUserIsNew_returnsUserLoginView() throws Exception {
        given(userService.checkUserExists("newuser")).willReturn(false);
        User savedUser = new User();
        savedUser.setUsername("newuser");
        given(userService.addUser(any(User.class))).willReturn(savedUser);

        mockMvc.perform(post("/newuserregister")
                        .param("username", "newuser")
                        .param("email", "new@example.com")
                        .param("password", "pass123")
                        .param("address", "123 Main St"))
                .andExpect(status().isOk())
                .andExpect(view().name("userLogin"));
    }
}
