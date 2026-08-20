package com.jtspringproject.JtSpringProject.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.jtspringproject.JtSpringProject.models.Category;
import com.jtspringproject.JtSpringProject.services.categoryService;
import com.jtspringproject.JtSpringProject.services.productService;
import com.jtspringproject.JtSpringProject.services.userService;

/**
 * Verifies that exceptions thrown by service beans propagate through
 * AdminController to GlobalExceptionHandler without being swallowed.
 *
 * <p>Per contract-exception-propagation: service throws → controller propagates
 * → handler catches → safe HTTP response. CWE-209 sentinel assertions confirm
 * no exception detail leaks into the response body.</p>
 */
@WebMvcTest(
    controllers = {AdminController.class, GlobalExceptionHandler.class},
    excludeAutoConfiguration = {
        HibernateJpaAutoConfiguration.class,
        SecurityAutoConfiguration.class
    }
)
class AdminControllerExceptionPropagationTest {

    private static final String SENTINEL_MESSAGE = "SENTINEL_ADMIN_PROPAGATION_99887";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private userService userService;

    @MockBean
    private categoryService categoryService;

    @MockBean
    private productService productService;

    // -------------------------------------------------------------------------
    // Exception propagation — RuntimeException → HTTP 500 (BR-06)
    // -------------------------------------------------------------------------

    @Test
    void getProducts_whenServiceThrowsRuntimeException_returns500AndErrorView() throws Exception {
        given(productService.getProducts()).willThrow(new RuntimeException(SENTINEL_MESSAGE));

        mockMvc.perform(get("/admin/products"))
                .andExpect(status().isInternalServerError())
                .andExpect(view().name("error"));
    }

    @Test
    void getProducts_whenServiceThrowsRuntimeException_doesNotLeakExceptionMessage() throws Exception {
        given(productService.getProducts()).willThrow(new RuntimeException(SENTINEL_MESSAGE));

        mockMvc.perform(get("/admin/products"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(not(containsString(SENTINEL_MESSAGE))));
    }

    @Test
    void getCategories_whenServiceThrowsRuntimeException_returns500AndErrorView() throws Exception {
        given(categoryService.getCategories()).willThrow(new RuntimeException(SENTINEL_MESSAGE));

        mockMvc.perform(get("/admin/categories"))
                .andExpect(status().isInternalServerError())
                .andExpect(view().name("error"));
    }

    @Test
    void getCategories_whenServiceThrowsRuntimeException_doesNotLeakExceptionMessage() throws Exception {
        given(categoryService.getCategories()).willThrow(new RuntimeException(SENTINEL_MESSAGE));

        mockMvc.perform(get("/admin/categories"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(not(containsString(SENTINEL_MESSAGE))));
    }

    // -------------------------------------------------------------------------
    // Exception propagation — NoSuchElementException → HTTP 404
    // -------------------------------------------------------------------------

    @Test
    void getUpdateProductPage_whenProductNotFound_returns404And404View() throws Exception {
        given(productService.getProduct(anyInt()))
                .willThrow(new NoSuchElementException(SENTINEL_MESSAGE));

        mockMvc.perform(get("/admin/products/update/999"))
                .andExpect(status().isNotFound())
                .andExpect(view().name("404"));
    }

    @Test
    void getUpdateProductPage_whenProductNotFound_doesNotLeakExceptionMessage() throws Exception {
        given(productService.getProduct(anyInt()))
                .willThrow(new NoSuchElementException(SENTINEL_MESSAGE));

        mockMvc.perform(get("/admin/products/update/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(not(containsString(SENTINEL_MESSAGE))));
    }

    // -------------------------------------------------------------------------
    // Happy-path regression guard (BR-05)
    // -------------------------------------------------------------------------

    @Test
    void getProducts_whenServiceReturnsEmptyList_returns200AndProductsView() throws Exception {
        given(productService.getProducts()).willReturn(Collections.emptyList());

        mockMvc.perform(get("/admin/products"))
                .andExpect(status().isOk())
                .andExpect(view().name("products"))
                .andExpect(model().attributeExists("msg"));
    }

    @Test
    void getCategories_whenServiceReturnsList_returns200AndCategoriesView() throws Exception {
        Category category = new Category();
        category.setId(1);
        category.setName("Electronics");
        given(categoryService.getCategories()).willReturn(List.of(category));

        mockMvc.perform(get("/admin/categories"))
                .andExpect(status().isOk())
                .andExpect(view().name("categories"))
                .andExpect(model().attributeExists("categories"));
    }
}
