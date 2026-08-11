package com.ecommerce.catalogservice.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * JPA entity mapping to the {@code products} table.
 * Mirrors the monolith schema: id, name, image, categoryid FK, quantity, price, weight, description.
 */
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "image", length = 512)
    private String image;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryid", nullable = false)
    private Category category;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "weight", precision = 8, scale = 3)
    private BigDecimal weight;

    @Column(name = "description", length = 1000)
    private String description;

    protected Product() {
    }

    public Product(String name, String image, Category category, Integer quantity,
                   BigDecimal price, BigDecimal weight, String description) {
        this.name = name;
        this.image = image;
        this.category = category;
        this.quantity = quantity;
        this.price = price;
        this.weight = weight;
        this.description = description;
    }

    public Long getId() { return id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getImage() { return image; }

    public void setImage(String image) { this.image = image; }

    public Category getCategory() { return category; }

    public void setCategory(Category category) { this.category = category; }

    public Integer getQuantity() { return quantity; }

    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getPrice() { return price; }

    public void setPrice(BigDecimal price) { this.price = price; }

    public BigDecimal getWeight() { return weight; }

    public void setWeight(BigDecimal weight) { this.weight = weight; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }
}
