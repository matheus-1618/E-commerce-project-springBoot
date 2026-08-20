package com.jtspringproject.JtSpringProject.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jtspringproject.JtSpringProject.models.Product;
import com.jtspringproject.JtSpringProject.repository.ProductRepository;

@Service
@Transactional(readOnly = true)
public class productService {
	private final ProductRepository productRepository;

	public productService(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	public List<Product> getProducts() {
		return this.productRepository.findAll();
	}

	@Transactional
	public Product addProduct(Product product) {
		return this.productRepository.save(product);
	}

	public Product getProduct(int id) {
		return this.productRepository.findById(id).orElse(null);
	}

	@Transactional
	public Product updateProduct(int id, Product product) {
		product.setId(id);
		return this.productRepository.save(product);
	}

	@Transactional
	public boolean deleteProduct(int id) {
		if (this.productRepository.existsById(id)) {
			this.productRepository.deleteById(id);
			return true;
		}
		return false;
	}
}
