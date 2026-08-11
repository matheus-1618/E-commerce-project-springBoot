package com.ecommerce.catalogservice.dto;

/**
 * Response DTO for Category. Read-only view returned by controller endpoints.
 */
public record CategoryDto(Long id, String name) {
}
