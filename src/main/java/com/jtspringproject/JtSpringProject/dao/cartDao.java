package com.jtspringproject.JtSpringProject.dao;

import java.util.List;

import com.jtspringproject.JtSpringProject.models.Cart;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class cartDao {
    private static final Logger logger = LoggerFactory.getLogger(cartDao.class);

    private final SessionFactory sessionFactory;

    public cartDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Transactional
    public Cart addCart(Cart cart) {
        logger.debug("Adding cart id={}", cart.getId());
        this.sessionFactory.getCurrentSession().save(cart);
        return cart;
    }

    @Transactional
    public List<Cart> getCarts() {
        logger.debug("Retrieving all carts");
        return this.sessionFactory.getCurrentSession().createQuery("from CART", Cart.class).list();
    }

    @Transactional
    public void updateCart(Cart cart) {
        logger.debug("Updating cart id={}", cart.getId());
        this.sessionFactory.getCurrentSession().update(cart);
    }

    @Transactional
    public void deleteCart(Cart cart) {
        logger.debug("Deleting cart id={}", cart.getId());
        this.sessionFactory.getCurrentSession().delete(cart);
    }
}
