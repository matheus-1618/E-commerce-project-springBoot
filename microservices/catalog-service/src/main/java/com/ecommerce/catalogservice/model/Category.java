package com.ecommerce.catalogservice.model;

import jakarta.persistence.*;

/**
 * JPA entity mapping to the {@code categories} table.
 * Mirrors the monolith schema: categoryid (PK), name.
 */
@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "categoryid")
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 255)
    private String name;

    protected Category() {
    }

    public Category(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
