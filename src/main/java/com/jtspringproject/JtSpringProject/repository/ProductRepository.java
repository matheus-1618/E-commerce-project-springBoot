package com.jtspringproject.JtSpringProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jtspringproject.JtSpringProject.models.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {
}
