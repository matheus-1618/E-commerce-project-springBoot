package com.jtspringproject.JtSpringProject.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.jtspringproject.JtSpringProject.dao.categoryDao;
import com.jtspringproject.JtSpringProject.models.Category;

@Service
public class categoryService {
	private static final Logger logger = LoggerFactory.getLogger(categoryService.class);

	private final categoryDao categoryDao;

	public categoryService(categoryDao categoryDao) {
		this.categoryDao = categoryDao;
	}

	public Category addCategory(String name) {
		logger.debug("Adding category name={}", name);
		return this.categoryDao.addCategory(name);
	}

	public List<Category> getCategories() {
		logger.debug("Fetching all categories");
		return this.categoryDao.getCategories();
	}

	public Boolean deleteCategory(int id) {
		logger.debug("Deleting category id={}", id);
		return this.categoryDao.deleteCategory(id);
	}

	public Category updateCategory(int id, String name) {
		logger.debug("Updating category id={} name={}", id, name);
		return this.categoryDao.updateCategory(id, name);
	}

	public Category getCategory(int id) {
		logger.debug("Fetching category id={}", id);
		return this.categoryDao.getCategory(id);
	}
}
