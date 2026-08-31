package com.jtspringproject.JtSpringProject.services;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.jtspringproject.JtSpringProject.dao.productDao;
import com.jtspringproject.JtSpringProject.models.PageResult;
import com.jtspringproject.JtSpringProject.models.PaginationRequest;
import com.jtspringproject.JtSpringProject.models.Product;

@Service
public class productService {
	private static final Set<String> PRODUCT_SORT_FIELDS = new LinkedHashSet<>(Arrays.asList("name", "price"));
	private static final String DEFAULT_PRODUCT_SORT = "name";

	private final productDao productDao;

	public productService(productDao productDao) {
		this.productDao = productDao;
	}

	public List<Product> getProducts() {
		return this.productDao.getProducts();
	}

	public PageResult<Product> getProducts(int page, int size, String sort) {
		PaginationRequest request = PaginationRequest.of(page, size, sort, PRODUCT_SORT_FIELDS, DEFAULT_PRODUCT_SORT);
		return this.productDao.getProducts(request);
	}

	public Product addProduct(Product product) {
		return this.productDao.addProduct(product);
	}

	public Product getProduct(int id) {
		return this.productDao.getProduct(id);
	}

	public Product updateProduct(int id, Product product) {
		product.setId(id);
		return this.productDao.updateProduct(product);
	}

	public boolean deleteProduct(int id) {
		return this.productDao.deleteProduct(id);
	}
}
