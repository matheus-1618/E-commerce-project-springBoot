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

import com.jtspringproject.JtSpringProject.models.Category;
import com.jtspringproject.JtSpringProject.repository.CategoryRepository;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private categoryService service;

    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setId(1);
        testCategory.setName("Electronics");
    }

    @Test
    void addCategory_createsAndSaves() {
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        Category result = service.addCategory("Electronics");

        assertNotNull(result);
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void getCategories_returnsList() {
        when(categoryRepository.findAll()).thenReturn(Arrays.asList(testCategory));

        List<Category> result = service.getCategories();

        assertEquals(1, result.size());
        assertEquals("Electronics", result.get(0).getName());
    }

    @Test
    void deleteCategory_returnsTrueWhenExists() {
        when(categoryRepository.existsById(1)).thenReturn(true);

        assertTrue(service.deleteCategory(1));
        verify(categoryRepository).deleteById(1);
    }

    @Test
    void deleteCategory_returnsFalseWhenNotExists() {
        when(categoryRepository.existsById(99)).thenReturn(false);

        assertFalse(service.deleteCategory(99));
        verify(categoryRepository, never()).deleteById(any());
    }

    @Test
    void updateCategory_updatesName() {
        when(categoryRepository.findById(1)).thenReturn(Optional.of(testCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        Category result = service.updateCategory(1, "Updated Name");

        assertNotNull(result);
        verify(categoryRepository).save(testCategory);
    }

    @Test
    void updateCategory_returnsNullWhenNotFound() {
        when(categoryRepository.findById(99)).thenReturn(Optional.empty());

        assertNull(service.updateCategory(99, "Name"));
    }

    @Test
    void getCategory_returnsCategory() {
        when(categoryRepository.findById(1)).thenReturn(Optional.of(testCategory));

        Category result = service.getCategory(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
    }

    @Test
    void getCategory_returnsNullWhenNotFound() {
        when(categoryRepository.findById(99)).thenReturn(Optional.empty());

        assertNull(service.getCategory(99));
    }
}
