package com.ecommerce.catalogservice.service;

import com.ecommerce.catalogservice.dto.CreateProductRequest;
import com.ecommerce.catalogservice.dto.ProductDto;
import com.ecommerce.catalogservice.dto.UpdateProductRequest;
import com.ecommerce.catalogservice.exception.CategoryNotFoundException;
import com.ecommerce.catalogservice.exception.ProductNotFoundException;
import com.ecommerce.catalogservice.model.Category;
import com.ecommerce.catalogservice.model.Product;
import com.ecommerce.catalogservice.repository.CategoryRepository;
import com.ecommerce.catalogservice.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link ProductService}.
 * Uses Mockito to stub the repositories; no Spring context is loaded.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    private Category electronics;
    private Product laptop;

    @BeforeEach
    void setUp() throws Exception {
        electronics = new Category("Electronics");
        setField(electronics, "id", 1L);

        laptop = new Product(
                "Laptop Pro",
                "laptop.jpg",
                electronics,
                10,
                new BigDecimal("999.99"),
                new BigDecimal("2.500"),
                "High-performance laptop");
        setField(laptop, "id", 1L);
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        var field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    // -------------------------------------------------------------------------
    // findAll
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("findAll returns list of ProductDto")
    void findAll_returnsAllProducts() {
        when(productRepository.findAll()).thenReturn(List.of(laptop));

        List<ProductDto> result = productService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Laptop Pro");
        assertThat(result.get(0).categoryName()).isEqualTo("Electronics");
    }

    // -------------------------------------------------------------------------
    // findById
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("findById returns ProductDto for existing id")
    void findById_existingId_returnsProductDto() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(laptop));

        ProductDto result = productService.findById(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Laptop Pro");
        assertThat(result.price()).isEqualByComparingTo("999.99");
    }

    @Test
    @DisplayName("findById throws ProductNotFoundException for unknown id")
    void findById_unknownId_throwsProductNotFoundException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.findById(99L))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("99");
    }

    // -------------------------------------------------------------------------
    // create
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("create saves and returns new ProductDto")
    void create_validRequest_savesProductAndReturnsDto() {
        CreateProductRequest request = new CreateProductRequest(
                "Tablet", "tablet.jpg", 1L, 5,
                new BigDecimal("299.99"), new BigDecimal("0.600"), "Compact tablet");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(electronics));
        when(productRepository.save(any(Product.class))).thenReturn(laptop);

        ProductDto result = productService.create(request);

        assertThat(result).isNotNull();
        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("create throws CategoryNotFoundException for unknown category")
    void create_unknownCategory_throwsCategoryNotFoundException() {
        CreateProductRequest request = new CreateProductRequest(
                "Tablet", null, 99L, 5,
                new BigDecimal("299.99"), null, null);

        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.create(request))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessageContaining("99");

        verify(productRepository, never()).save(any());
    }

    // -------------------------------------------------------------------------
    // update
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("update modifies product and returns updated ProductDto")
    void update_existingId_updatesAndReturnsDto() {
        UpdateProductRequest request = new UpdateProductRequest(
                "Laptop Pro Max", "laptop-max.jpg", 1L, 8,
                new BigDecimal("1299.99"), new BigDecimal("2.200"), "Upgraded laptop");

        when(productRepository.findById(1L)).thenReturn(Optional.of(laptop));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(electronics));
        when(productRepository.save(laptop)).thenReturn(laptop);

        ProductDto result = productService.update(1L, request);

        assertThat(result).isNotNull();
        verify(productRepository).save(laptop);
    }

    @Test
    @DisplayName("update throws ProductNotFoundException for unknown product")
    void update_unknownProductId_throwsProductNotFoundException() {
        UpdateProductRequest request = new UpdateProductRequest(
                "X", null, 1L, 1,
                new BigDecimal("1.00"), null, null);

        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.update(99L, request))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("update throws CategoryNotFoundException when new category does not exist")
    void update_unknownCategoryId_throwsCategoryNotFoundException() {
        UpdateProductRequest request = new UpdateProductRequest(
                "Laptop", null, 99L, 1,
                new BigDecimal("1.00"), null, null);

        when(productRepository.findById(1L)).thenReturn(Optional.of(laptop));
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.update(1L, request))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessageContaining("99");
    }

    // -------------------------------------------------------------------------
    // delete
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("delete removes existing product")
    void delete_existingId_deletesProduct() {
        when(productRepository.existsById(1L)).thenReturn(true);

        productService.delete(1L);

        verify(productRepository).deleteById(1L);
    }

    @Test
    @DisplayName("delete throws ProductNotFoundException for unknown id")
    void delete_unknownId_throwsProductNotFoundException() {
        when(productRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> productService.delete(99L))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("99");

        verify(productRepository, never()).deleteById(any());
    }
}
