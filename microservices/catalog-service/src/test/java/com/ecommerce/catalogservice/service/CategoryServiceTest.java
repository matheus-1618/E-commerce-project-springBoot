package com.ecommerce.catalogservice.service;

import com.ecommerce.catalogservice.dto.CategoryDto;
import com.ecommerce.catalogservice.dto.CreateCategoryRequest;
import com.ecommerce.catalogservice.dto.UpdateCategoryRequest;
import com.ecommerce.catalogservice.exception.CatalogConflictException;
import com.ecommerce.catalogservice.exception.CategoryNotFoundException;
import com.ecommerce.catalogservice.model.Category;
import com.ecommerce.catalogservice.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link CategoryService}.
 * Uses Mockito to stub the repository; no Spring context is loaded.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryService")
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category electronics;

    @BeforeEach
    void setUp() {
        electronics = new Category("Electronics");
        // Simulate the JPA-assigned id
        try {
            var idField = Category.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(electronics, 1L);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // -------------------------------------------------------------------------
    // findAll
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("findAll returns list of CategoryDto")
    void findAll_returnsAllCategories() {
        when(categoryRepository.findAll()).thenReturn(List.of(electronics));

        List<CategoryDto> result = categoryService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Electronics");
    }

    // -------------------------------------------------------------------------
    // findById
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("findById returns CategoryDto for existing id")
    void findById_existingId_returnsCategoryDto() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(electronics));

        CategoryDto result = categoryService.findById(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Electronics");
    }

    @Test
    @DisplayName("findById throws CategoryNotFoundException for unknown id")
    void findById_unknownId_throwsCategoryNotFoundException() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.findById(99L))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessageContaining("99");
    }

    // -------------------------------------------------------------------------
    // create
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("create saves and returns new CategoryDto")
    void create_newName_savesCategoryAndReturnsDto() {
        CreateCategoryRequest request = new CreateCategoryRequest("Books");
        Category savedCategory = new Category("Books");
        try {
            var idField = Category.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(savedCategory, 2L);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        when(categoryRepository.findByName("Books")).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

        CategoryDto result = categoryService.create(request);

        assertThat(result.id()).isEqualTo(2L);
        assertThat(result.name()).isEqualTo("Books");
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    @DisplayName("create throws CatalogConflictException for duplicate name")
    void create_duplicateName_throwsCatalogConflictException() {
        CreateCategoryRequest request = new CreateCategoryRequest("Electronics");

        when(categoryRepository.findByName("Electronics")).thenReturn(Optional.of(electronics));

        assertThatThrownBy(() -> categoryService.create(request))
                .isInstanceOf(CatalogConflictException.class)
                .hasMessageContaining("Electronics");

        verify(categoryRepository, never()).save(any());
    }

    // -------------------------------------------------------------------------
    // update
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("update modifies name and returns updated CategoryDto")
    void update_existingId_updatesAndReturnsDto() {
        UpdateCategoryRequest request = new UpdateCategoryRequest("Electronics Revised");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(electronics));
        when(categoryRepository.findByName("Electronics Revised")).thenReturn(Optional.empty());
        when(categoryRepository.save(electronics)).thenReturn(electronics);

        CategoryDto result = categoryService.update(1L, request);

        assertThat(result.name()).isEqualTo("Electronics Revised");
    }

    @Test
    @DisplayName("update throws CategoryNotFoundException for unknown id")
    void update_unknownId_throwsCategoryNotFoundException() {
        UpdateCategoryRequest request = new UpdateCategoryRequest("Anything");

        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.update(99L, request))
                .isInstanceOf(CategoryNotFoundException.class);
    }

    @Test
    @DisplayName("update throws CatalogConflictException when name is taken by another category")
    void update_nameTakenByOther_throwsCatalogConflictException() {
        Category other = new Category("Clothing");
        try {
            var idField = Category.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(other, 2L);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        UpdateCategoryRequest request = new UpdateCategoryRequest("Clothing");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(electronics));
        when(categoryRepository.findByName("Clothing")).thenReturn(Optional.of(other));

        assertThatThrownBy(() -> categoryService.update(1L, request))
                .isInstanceOf(CatalogConflictException.class)
                .hasMessageContaining("Clothing");
    }

    // -------------------------------------------------------------------------
    // delete
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("delete removes existing category")
    void delete_existingId_deletesCategory() {
        when(categoryRepository.existsById(1L)).thenReturn(true);

        categoryService.delete(1L);

        verify(categoryRepository).deleteById(1L);
    }

    @Test
    @DisplayName("delete throws CategoryNotFoundException for unknown id")
    void delete_unknownId_throwsCategoryNotFoundException() {
        when(categoryRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> categoryService.delete(99L))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessageContaining("99");

        verify(categoryRepository, never()).deleteById(any());
    }
}
