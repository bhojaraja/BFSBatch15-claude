package com.wipro.productcatalog.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Category Entity
 *
 * Represents a product category in the system
 * Used for organizing products
 */
@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @Column(name = "id", length = 50)
    private String id;

    @Column(name = "name", length = 100, nullable = false, unique = true)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "active", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean active = true;

}
