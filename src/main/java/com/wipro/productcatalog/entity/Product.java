package com.wipro.productcatalog.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Product Entity - Represents a product in the catalog
 *
 * This entity maps to the 'products' table in the database
 * and contains all product-related information including pricing,
 * inventory, and audit trail.
 */
@Entity
@Table(
    name = "products",
    indexes = {
        @Index(name = "idx_category_id", columnList = "category_id"),
        @Index(name = "idx_active", columnList = "active"),
        @Index(name = "idx_created_at", columnList = "created_at"),
        @Index(name = "idx_name", columnList = "name")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    /**
     * Product ID - Primary Key
     * Format: PROD-XXX (e.g., PROD-001)
     * Manually assigned, not auto-generated (business decision)
     */
    @Id
    @Column(name = "id", length = 50, nullable = false)
    private String id;

    /**
     * Product Name
     * Required field, max 100 characters
     */
    @Column(name = "name", length = 100, nullable = false)
    @NotBlank(message = "Product name cannot be blank")
    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    private String name;

    /**
     * Product Description
     * Optional field for detailed product information
     * Can store up to 1000 characters
     */
    @Column(name = "description", columnDefinition = "TEXT")
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    /**
     * Category Reference
     * Many-to-One relationship with Category entity
     * Foreign Key: category_id
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @NotNull(message = "Category cannot be null")
    private Category category;

    /**
     * Product Price
     * Decimal type for monetary values
     * Precision: 10 digits total, 2 decimal places (e.g., 99999999.99)
     * Constraint: Must be greater than 0
     */
    @Column(name = "price", precision = 10, scale = 2, nullable = false)
    @NotNull(message = "Price cannot be null")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @DecimalMax(value = "999999.99", message = "Price cannot exceed 999999.99")
    private BigDecimal price;

    /**
     * Stock Quantity
     * Integer representing available inventory
     * Constraint: Cannot be negative
     */
    @Column(name = "stock_quantity", nullable = false)
    @NotNull(message = "Stock quantity cannot be null")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    @Max(value = 1000000, message = "Stock quantity cannot exceed 1,000,000")
    private Integer stockQuantity;

    /**
     * Active Flag
     * Soft delete indicator
     * true = active (visible to customers)
     * false = inactive/deleted (hidden from customers)
     */
    @Column(name = "active", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean active = true;

    /**
     * Product Image URL
     * Optional field for storing product image location
     */
    @Column(name = "image_url", length = 500)
    private String imageUrl;

    /**
     * Creation Timestamp
     * Automatically set to current time when product is created
     * Immutable after creation
     */
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    /**
     * Last Update Timestamp
     * Automatically updated whenever the product is modified
     */
    @Column(name = "updated_at", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    /**
     * Created By
     * Username/ID of the user who created this product
     * Useful for audit trail
     */
    @Column(name = "created_by", length = 255)
    private String createdBy;

    /**
     * Updated By
     * Username/ID of the user who last modified this product
     * Useful for audit trail
     */
    @Column(name = "updated_by", length = 255)
    private String updatedBy;

    /**
     * Version Number
     * Used for Optimistic Locking
     * Prevents concurrent modification conflicts
     * Automatically incremented on every update by Hibernate
     */
    @Version
    @Column(name = "version", nullable = false)
    private Integer version;

    /**
     * Lifecycle Callback - Before Persist (Insert)
     * Automatically called before saving a new product to database
     * Sets createdAt and updatedAt to current time
     * Sets version to 1
     * Sets active to true
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.version = 1;
        this.active = true;
    }

    /**
     * Lifecycle Callback - Before Update
     * Automatically called before updating product in database
     * Updates the updatedAt timestamp to current time
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}
