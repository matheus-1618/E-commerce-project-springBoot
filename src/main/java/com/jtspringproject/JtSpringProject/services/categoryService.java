package com.jtspringproject.JtSpringProject.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jtspringproject.JtSpringProject.models.Category;
import com.jtspringproject.JtSpringProject.repository.CategoryRepository;

@Service
@Transactional(readOnly = true)
public class categoryService {
	private final CategoryRepository categoryRepository;

	public categoryService(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}

	@Transactional
	public Category addCategory(String name) {
		Category category = new Category();
		category.setName(name);
		return this.categoryRepository.save(category);
	}

	public List<Category> getCategories() {
		return this.categoryRepository.findAll();
	}

	@Transactional
	public Boolean deleteCategory(int id) {
		if (this.categoryRepository.existsById(id)) {
			this.categoryRepository.deleteById(id);
			return true;
		}
		return false;
	}

	@Transactional
	public Category updateCategory(int id, String name) {
		Category category = this.categoryRepository.findById(id).orElse(null);
		if (category == null) {
			return null;
		}
		category.setName(name);
		return this.categoryRepository.save(category);
	}

	public Category getCategory(int id) {
		return this.categoryRepository.findById(id).orElse(null);
	}
}
