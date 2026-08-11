package com.ecommerce.catalogservice.repository;

import com.ecommerce.catalogservice.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Find category by exact name (case-sensitive).
     * Used for duplicate-name detection before create/update.
     */
    Optional<Category> findByName(String name);
}
