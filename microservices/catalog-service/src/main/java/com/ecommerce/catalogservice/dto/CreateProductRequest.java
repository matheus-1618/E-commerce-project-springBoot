package com.ecommerce.catalogservice.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * Request body for POST /api/products.
 */
public record CreateProductRequest(
        @NotBlank(message = "Product name must not be blank")
        @Size(max = 255, message = "Product name must not exceed 255 characters")
        String name,

        @Size(max = 512, message = "Image path must not exceed 512 characters")
        String image,

        @NotNull(message = "Category ID must not be null")
        @Positive(message = "Category ID must be a positive number")
        Long categoryId,

        @NotNull(message = "Quantity must not be null")
        @Min(value = 0, message = "Quantity must be zero or positive")
        Integer quantity,

        @NotNull(message = "Price must not be null")
        @DecimalMin(value = "0.01", message = "Price must be greater than zero")
        BigDecimal price,

        @DecimalMin(value = "0.001", inclusive = false, message = "Weight must be positive when provided")
        BigDecimal weight,

        @Size(max = 1000, message = "Description must not exceed 1000 characters")
        String description
) {
}
