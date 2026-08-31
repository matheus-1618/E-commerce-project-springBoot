package com.jtspringproject.JtSpringProject.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.jtspringproject.JtSpringProject.models.PageResult;
import com.jtspringproject.JtSpringProject.models.PaginationRequest;
import com.jtspringproject.JtSpringProject.models.Product;

@Repository
public class productDao {
	private final SessionFactory sessionFactory;

	public productDao(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Transactional
	public List<Product> getProducts() {
		return this.sessionFactory.getCurrentSession().createQuery("from PRODUCT", Product.class).list();
	}

	@Transactional
	public PageResult<Product> getProducts(PaginationRequest request) {
		Session session = this.sessionFactory.getCurrentSession();

		Query<Long> countQuery = session.createQuery("SELECT COUNT(*) FROM PRODUCT", Long.class);
		long totalElements = countQuery.uniqueResult();

		String hql = "FROM PRODUCT ORDER BY " + request.getSortField() + " " + request.getSortDirection() + ", id ASC";
		Query<Product> dataQuery = session.createQuery(hql, Product.class);
		dataQuery.setFirstResult(request.getOffset());
		dataQuery.setMaxResults(request.getSize());
		List<Product> content = dataQuery.list();

		return new PageResult<>(content, request.getPage(), request.getSize(),
				totalElements, request.getSortField(), request.getSortDirection());
	}

	@Transactional
	public Product addProduct(Product product) {
		this.sessionFactory.getCurrentSession().save(product);
		return product;
	}

	@Transactional
	public Product getProduct(int id) {
		return this.sessionFactory.getCurrentSession().get(Product.class, id);
	}

	@Transactional
	public Product updateProduct(Product product) {
		this.sessionFactory.getCurrentSession().update(product);
		return product;
	}

	@Transactional
	public Boolean deleteProduct(int id) {
		Session session = this.sessionFactory.getCurrentSession();
		Product product = session.get(Product.class, id);

		if (product != null) {
			session.delete(product);
			return true;
		}
		return false;
	}

}
