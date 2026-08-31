package com.jtspringproject.JtSpringProject.dao;

import java.util.List;

import javax.persistence.NoResultException;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.jtspringproject.JtSpringProject.models.PageResult;
import com.jtspringproject.JtSpringProject.models.PaginationRequest;
import com.jtspringproject.JtSpringProject.models.User;

@Repository
public class userDao {
	private final SessionFactory sessionFactory;

	public userDao(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Transactional
	public List<User> getAllUser() {
		Session session = this.sessionFactory.getCurrentSession();
		return session.createQuery("from CUSTOMER", User.class).list();
	}

	@Transactional
	public PageResult<User> getAllUser(PaginationRequest request) {
		Session session = this.sessionFactory.getCurrentSession();

		Query<Long> countQuery = session.createQuery("SELECT COUNT(*) FROM CUSTOMER", Long.class);
		long totalElements = countQuery.uniqueResult();

		String hql = "FROM CUSTOMER ORDER BY " + request.getSortField() + " " + request.getSortDirection() + ", id ASC";
		Query<User> dataQuery = session.createQuery(hql, User.class);
		dataQuery.setFirstResult(request.getOffset());
		dataQuery.setMaxResults(request.getSize());
		List<User> content = dataQuery.list();

		return new PageResult<>(content, request.getPage(), request.getSize(),
				totalElements, request.getSortField(), request.getSortDirection());
	}

	@Transactional
	public User saveUser(User user) {
		this.sessionFactory.getCurrentSession().saveOrUpdate(user);
		return user;
	}

	@Transactional
	public boolean userExists(String username) {
		Query<User> query = sessionFactory.getCurrentSession().createQuery("from CUSTOMER where username = :username",
				User.class);
		query.setParameter("username", username);
		return !query.getResultList().isEmpty();
	}

	@Transactional
	public User getUserByUsername(String username) {
		Query<User> query = sessionFactory.getCurrentSession().createQuery("from CUSTOMER where username = :username",
				User.class);
		query.setParameter("username", username);

		try {
			return query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	@Transactional
	public User getUserById(int id) {
		return this.sessionFactory.getCurrentSession().get(User.class, id);
	}
}