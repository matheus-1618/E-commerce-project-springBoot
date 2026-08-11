package com.ecommerce.catalogservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request body for PUT /api/categories/{id}.
 */
public record UpdateCategoryRequest(
        @NotBlank(message = "Category name must not be blank")
        @Size(max = 255, message = "Category name must not exceed 255 characters")
        String name
) {
}
