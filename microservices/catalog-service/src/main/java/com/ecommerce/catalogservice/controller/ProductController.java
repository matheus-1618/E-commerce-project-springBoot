package com.ecommerce.catalogservice.controller;

import com.ecommerce.catalogservice.dto.CreateProductRequest;
import com.ecommerce.catalogservice.dto.ProductDto;
import com.ecommerce.catalogservice.dto.UpdateProductRequest;
import com.ecommerce.catalogservice.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for product operations.
 *
 * <p>This controller delegates all business logic to {@link ProductService} and
 * does NOT catch exceptions or produce error responses directly — all error
 * handling is the responsibility of {@link com.ecommerce.catalogservice.exception.GlobalExceptionHandler}
 * (req-global-exception-handler).
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /** GET /api/products — list all products. */
    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        return ResponseEntity.ok(productService.findAll());
    }

    /** GET /api/products/{id} — get one product (404 if not found). */
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    /** POST /api/products — create a new product (201 on success). */
    @PostMapping
    public ResponseEntity<ProductDto> createProduct(
            @Valid @RequestBody CreateProductRequest request) {
        ProductDto created = productService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /** PUT /api/products/{id} — update an existing product. */
    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request) {
        return ResponseEntity.ok(productService.update(id, request));
    }

    /** DELETE /api/products/{id} — delete a product (204 on success). */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
