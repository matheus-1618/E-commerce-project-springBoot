package com.jtspringproject.JtSpringProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jtspringproject.JtSpringProject.models.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
}
