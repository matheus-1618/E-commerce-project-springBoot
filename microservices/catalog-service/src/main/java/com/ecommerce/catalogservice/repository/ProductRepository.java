package com.ecommerce.catalogservice.repository;

import com.ecommerce.catalogservice.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Find all products belonging to a given category.
     */
    List<Product> findByCategoryId(Long categoryId);
}
