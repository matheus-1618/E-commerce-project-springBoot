package com.jtspringproject.JtSpringProject.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.jtspringproject.JtSpringProject.dao.productDao;
import com.jtspringproject.JtSpringProject.models.Product;

@Service
public class productService {
	private static final Logger logger = LoggerFactory.getLogger(productService.class);

	private final productDao productDao;

	public productService(productDao productDao) {
		this.productDao = productDao;
	}

	public List<Product> getProducts() {
		logger.debug("Fetching all products");
		return this.productDao.getProducts();
	}

	public Product addProduct(Product product) {
		logger.debug("Adding product name={}", product.getName());
		return this.productDao.addProduct(product);
	}

	public Product getProduct(int id) {
		logger.debug("Fetching product id={}", id);
		return this.productDao.getProduct(id);
	}

	public Product updateProduct(int id, Product product) {
		logger.debug("Updating product id={}", id);
		product.setId(id);
		return this.productDao.updateProduct(product);
	}

	public boolean deleteProduct(int id) {
		logger.debug("Deleting product id={}", id);
		return this.productDao.deleteProduct(id);
	}
}
