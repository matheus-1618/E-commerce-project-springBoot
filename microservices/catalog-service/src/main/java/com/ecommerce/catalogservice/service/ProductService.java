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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Business logic for product operations.
 *
 * <p>This class communicates failures via typed domain exceptions:
 * <ul>
 *   <li>{@link ProductNotFoundException} — product ID not found (→ 404)</li>
 *   <li>{@link CategoryNotFoundException} — referenced category ID not found (→ 404)</li>
 * </ul>
 *
 * <p>No exception is swallowed. Successful-response contracts (2xx shapes) are
 * unchanged by error-handling intent (req-no-successful-response-change).
 */
@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository,
                          CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    /** Returns all products. */
    public List<ProductDto> findAll() {
        return productRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    /** Returns one product by id or throws {@link ProductNotFoundException}. */
    public ProductDto findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return toDto(product);
    }

    /**
     * Creates a product.
     * Throws {@link CategoryNotFoundException} if the referenced category does not exist.
     */
    @Transactional
    public ProductDto create(CreateProductRequest request) {
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new CategoryNotFoundException(request.categoryId()));

        Product product = new Product(
                request.name(),
                request.image(),
                category,
                request.quantity(),
                request.price(),
                request.weight(),
                request.description());

        return toDto(productRepository.save(product));
    }

    /**
     * Updates a product.
     * Throws {@link ProductNotFoundException} or {@link CategoryNotFoundException}.
     */
    @Transactional
    public ProductDto update(Long id, UpdateProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new CategoryNotFoundException(request.categoryId()));

        product.setName(request.name());
        product.setImage(request.image());
        product.setCategory(category);
        product.setQuantity(request.quantity());
        product.setPrice(request.price());
        product.setWeight(request.weight());
        product.setDescription(request.description());

        return toDto(productRepository.save(product));
    }

    /** Deletes a product; throws {@link ProductNotFoundException} if id does not exist. */
    @Transactional
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException(id);
        }
        productRepository.deleteById(id);
    }

    // -------------------------------------------------------------------------
    // Mapping
    // -------------------------------------------------------------------------

    private ProductDto toDto(Product product) {
        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getImage(),
                product.getCategory().getId(),
                product.getCategory().getName(),
                product.getQuantity(),
                product.getPrice(),
                product.getWeight(),
                product.getDescription());
    }
}
