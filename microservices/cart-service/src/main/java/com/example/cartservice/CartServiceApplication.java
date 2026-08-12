package com.example.cartservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Cart microservice entry point — Phase 2.1.
 *
 * <p>Starts a standalone Spring Boot application on port 8081 (configured in
 * application.properties). On startup, Hibernate auto-creates the {@code cart_items}
 * table in the H2 in-memory database and drops it on shutdown (create-drop strategy).
 *
 * <p>This class carries no business logic. All domain logic lives in
 * {@link com.example.cartservice.service.CartService}.
 */
@SpringBootApplication
public class CartServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CartServiceApplication.class, args);
    }
}
