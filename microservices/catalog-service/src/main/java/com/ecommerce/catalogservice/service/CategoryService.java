package com.ecommerce.catalogservice.service;

import com.ecommerce.catalogservice.dto.CategoryDto;
import com.ecommerce.catalogservice.dto.CreateCategoryRequest;
import com.ecommerce.catalogservice.dto.UpdateCategoryRequest;
import com.ecommerce.catalogservice.exception.CatalogConflictException;
import com.ecommerce.catalogservice.exception.CategoryNotFoundException;
import com.ecommerce.catalogservice.model.Category;
import com.ecommerce.catalogservice.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Business logic for category operations.
 *
 * <p>This class communicates failures via typed domain exceptions:
 * <ul>
 *   <li>{@link CategoryNotFoundException} — category ID not found (→ 404)</li>
 *   <li>{@link CatalogConflictException} — duplicate category name (→ 409)</li>
 * </ul>
 *
 * <p>No exception is swallowed. Successful-response contracts (2xx shapes) are
 * unchanged by error-handling intent (req-no-successful-response-change).
 */
@Service
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /** Returns all categories. */
    public List<CategoryDto> findAll() {
        return categoryRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    /** Returns one category by id or throws {@link CategoryNotFoundException}. */
    public CategoryDto findById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        return toDto(category);
    }

    /** Creates a category; throws {@link CatalogConflictException} on duplicate name. */
    @Transactional
    public CategoryDto create(CreateCategoryRequest request) {
        if (categoryRepository.findByName(request.name()).isPresent()) {
            throw new CatalogConflictException(
                    "Category with name '" + request.name() + "' already exists");
        }
        Category saved = categoryRepository.save(new Category(request.name()));
        return toDto(saved);
    }

    /** Updates a category; throws {@link CategoryNotFoundException} or {@link CatalogConflictException}. */
    @Transactional
    public CategoryDto update(Long id, UpdateCategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        // Conflict check: another category already owns this name
        categoryRepository.findByName(request.name())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new CatalogConflictException(
                            "Category with name '" + request.name() + "' already exists");
                });

        category.setName(request.name());
        return toDto(categoryRepository.save(category));
    }

    /** Deletes a category; throws {@link CategoryNotFoundException} if id does not exist. */
    @Transactional
    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new CategoryNotFoundException(id);
        }
        categoryRepository.deleteById(id);
    }

    // -------------------------------------------------------------------------
    // Mapping
    // -------------------------------------------------------------------------

    private CategoryDto toDto(Category category) {
        return new CategoryDto(category.getId(), category.getName());
    }
}
