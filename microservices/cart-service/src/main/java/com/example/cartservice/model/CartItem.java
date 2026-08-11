package com.example.cartservice.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * Aggregate root entity for the cart item bounded context.
 *
 * <p>Maps to the {@code cart_items} table managed exclusively by this microservice.
 * The monolith ({@code JtProject}) has zero access to this table or entity.
 *
 * <p>Design notes:
 * <ul>
 *   <li>{@code productName} and {@code price} are denormalized snapshots captured at
 *       add-to-cart time — they are not fetched live from catalog-service (Phase 2.1
 *       simplification; catalog integration is deferred).</li>
 *   <li>{@code productId} is a value reference only — no JPA FK constraint to an
 *       external catalog table.</li>
 *   <li>Quantity validation is enforced at the controller layer via Bean Validation
 *       ({@code @Min(1)} on the DTO), and defensively in the service layer.
 *       The entity itself carries only database-level NOT NULL constraints.</li>
 *   <li>All JPA annotations use {@code javax.persistence.*} — this module targets
 *       Spring Boot 2.6.4 / Java 11. Never use {@code jakarta.*}.</li>
 * </ul>
 *
 * <p>Invariants (BR-F-01 through BR-F-05 per business-rules artifact):
 * <ul>
 *   <li>cartId must be non-null</li>
 *   <li>productId and productName must be non-null</li>
 *   <li>quantity must be >= 1 (enforced by service/controller layers)</li>
 *   <li>price must be non-null and non-negative</li>
 *   <li>id is system-assigned; no setId() accessor exists</li>
 * </ul>
 */
@Entity
@Table(name = "cart_items")
public class CartItem {

    /** System-assigned primary key. IDENTITY strategy — auto-increment. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Logical cart session / user basket identifier.
     * In Phase 2.1, this is an opaque Long with no auth/session validation.
     */
    @Column(nullable = false)
    private Long cartId;

    /** FK-by-value to the catalog domain. No JPA FK constraint in Phase 2.1. */
    @Column(nullable = false)
    private Long productId;

    /** Denormalized product name snapshot captured at add-to-cart time. */
    @Column(nullable = false)
    private String productName;

    /**
     * Number of this product in the cart. Must be >= 1.
     * Enforced by @Min(1) on UpdateQuantityRequest DTO (controller layer)
     * and defensively asserted in CartService.
     */
    @Column(nullable = false)
    private int quantity;

    /** Unit price snapshot captured at add-to-cart time. Must be non-null and non-negative. */
    @Column(nullable = false)
    private BigDecimal price;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * JPA no-arg constructor — required by the JPA provider (Hibernate) for entity
     * instantiation during database reads. Package-protected to prevent accidental
     * use from application code; use the convenience constructor instead.
     *
     * <p>Must remain side-effect-free per JPA specification.
     */
    protected CartItem() {
        // Hibernate-only
    }

    /**
     * Convenience constructor for application code, service layer, and unit tests.
     *
     * <p>The {@code id} field is intentionally absent — it is null until
     * {@code CartRepository.save(cartItem)} returns the persisted entity.
     *
     * @param cartId      logical cart session identifier (must be non-null)
     * @param productId   catalog product reference (must be non-null)
     * @param productName product name snapshot (must be non-null and non-empty)
     * @param quantity    item count (must be >= 1)
     * @param price       unit price snapshot (must be non-null and non-negative)
     */
    public CartItem(Long cartId, Long productId, String productName,
                    int quantity, BigDecimal price) {
        this.cartId = cartId;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
    }

    // -------------------------------------------------------------------------
    // Accessors — standard JavaBeans getters/setters (no setId: PK is generated)
    // -------------------------------------------------------------------------

    public Long getId() {
        return id;
    }

    // No setId() — primary key is system-generated (BR-F-05)

    public Long getCartId() {
        return cartId;
    }

    public void setCartId(Long cartId) {
        this.cartId = cartId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
