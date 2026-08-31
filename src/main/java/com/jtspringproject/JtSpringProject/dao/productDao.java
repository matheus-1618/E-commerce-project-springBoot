package com.jtspringproject.JtSpringProject.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.jtspringproject.JtSpringProject.models.Product;

@Repository
public class productDao {
	private static final Logger logger = LoggerFactory.getLogger(productDao.class);

	private final SessionFactory sessionFactory;

	public productDao(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Transactional
	public List<Product> getProducts() {
		logger.debug("Retrieving all products");
		return this.sessionFactory.getCurrentSession().createQuery("from PRODUCT", Product.class).list();
	}

	@Transactional
	public Product addProduct(Product product) {
		logger.debug("Saving new product name={}", product.getName());
		this.sessionFactory.getCurrentSession().save(product);
		return product;
	}

	@Transactional
	public Product getProduct(int id) {
		logger.debug("Fetching product id={}", id);
		return this.sessionFactory.getCurrentSession().get(Product.class, id);
	}

	@Transactional
	public Product updateProduct(Product product) {
		logger.debug("Updating product id={}", product.getId());
		this.sessionFactory.getCurrentSession().update(product);
		return product;
	}

	@Transactional
	public Boolean deleteProduct(int id) {
		logger.debug("Deleting product id={}", id);
		Session session = this.sessionFactory.getCurrentSession();
		Product product = session.get(Product.class, id);

		if (product != null) {
			session.delete(product);
			return true;
		}
		logger.debug("Product id={} not found for deletion", id);
		return false;
	}

}
