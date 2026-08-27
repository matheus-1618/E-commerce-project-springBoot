package com.jtspringproject.JtSpringProject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jtspringproject.JtSpringProject.models.Cart;
import com.jtspringproject.JtSpringProject.models.User;

public interface CartRepository extends JpaRepository<Cart, Integer> {
    List<Cart> findByCustomer(User customer);
}
