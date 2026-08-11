package com.ecommerce.catalogservice.dto;

import java.math.BigDecimal;

/**
 * Response DTO for Product. Read-only view returned by controller endpoints.
 */
public record ProductDto(
        Long id,
        String name,
        String image,
        Long categoryId,
        String categoryName,
        Integer quantity,
        BigDecimal price,
        BigDecimal weight,
        String description
) {
}
