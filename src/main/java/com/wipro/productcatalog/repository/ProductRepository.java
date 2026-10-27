package com.wipro.productcatalog.repository;

import com.wipro.productcatalog.entity.Category;
import com.wipro.productcatalog.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * ProductRepository
 *
 * Data access layer for Product entity
 * Extends Spring Data JPA's JpaRepository for CRUD operations
 *
 * Generic Type: JpaRepository<Product, String>
 * - Product: Entity class
 * - String: Primary key type (id is String)
 *
 * Provided CRUD Methods (inherited from JpaRepository):
 * - save(Product) → Insert or update product
 * - findById(String) → Get product by ID
 * - findAll() → Get all products
 * - delete(Product) → Delete product
 * - deleteById(String) → Delete product by ID
 * - count() → Count total products
 * - existsById(String) → Check if product exists
 *
 * Custom Query Methods (defined below):
 * - findByCategoryId(String) → Find products by category
 * - existsByName(String) → Check if product name exists
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    /**
     * Find all products by category ID
     *
     * Method Name Query: Spring Data automatically generates SQL from method name
     * Naming Convention: findBy<FieldName>
     *
     * Generated SQL:
     * SELECT * FROM products WHERE category_id = ?
     *
     * @param categoryId - The category ID to filter by
     * @return List of products in the specified category
     *
     * Examples:
     * List<Product> electronics = repository.findByCategoryId("ELEC-001");
     * // Returns all products with category_id = "ELEC-001"
     *
     * Use Case: FR-2 (Search Products by Category)
     * When user filters products by category, this method retrieves matching products
     *
     * Note: Returns List (never null, empty list if no matches)
     */
    List<Product> findByCategoryId(String categoryId);

    /**
     * Find all active products by category ID
     *
     * Custom JPQL query with WHERE clause
     * Filters only active products (active = true)
     *
     * Generated SQL:
     * SELECT * FROM products WHERE category_id = ? AND active = true
     *
     * @param categoryId - The category ID to filter by
     * @return List of active products in the specified category
     *
     * Examples:
     * List<Product> activeElectronics =
     *     repository.findActiveByCategoryId("ELEC-001");
     * // Returns active products with category_id = "ELEC-001"
     *
     * Use Case: Customer-facing product list should only show active products
     * Deleted products (active=false) are hidden from customers
     *
     * Note: Soft delete implementation - products not deleted from DB,
     *       just marked as inactive
     */
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId AND p.active = true")
    List<Product> findActiveByCategoryId(@Param("categoryId") String categoryId);

    /**
     * Check if product with given name exists
     *
     * Method Name Query: Spring Data generates from method name
     * Naming Convention: existsBy<FieldName>
     *
     * Generated SQL:
     * SELECT COUNT(*) FROM products WHERE name = ?
     *
     * @param name - The product name to check
     * @return true if product with name exists, false otherwise
     *
     * Examples:
     * boolean exists = repository.existsByName("Laptop Pro 15");
     * if (exists) {
     *     // Product name already exists
     *     throw new DuplicateResourceException("Product name already exists");
     * }
     *
     * Use Case: Validation during product creation
     * Prevent creating products with duplicate names
     *
     * Note: Returns boolean (never null)
     * Returns true if ANY product with name exists, false if none exist
     */
    boolean existsByName(String name);

    /**
     * Check if product with given name exists (excluding a specific product)
     *
     * Used during product update to allow same name, but prevent duplicates
     * with other products
     *
     * Custom JPQL query with condition
     * Generated SQL:
     * SELECT COUNT(*) FROM products WHERE name = ? AND id != ?
     *
     * @param name - The product name to check
     * @param productId - The product ID to exclude (current product)
     * @return true if another product with name exists, false otherwise
     *
     * Examples:
     * boolean duplicateExists =
     *     repository.existsByNameAndIdNot("Laptop Pro", "PROD-001");
     * // Checks if any OTHER product has name "Laptop Pro"
     *
     * Use Case: Validation during product update
     * Allow updating product to same name, but prevent name duplication
     *
     * Scenario:
     * Product 1: id="PROD-001", name="Laptop Pro"
     * Product 2: id="PROD-002", name="Desktop"
     *
     * existsByNameAndIdNot("Laptop Pro", "PROD-001") → false (same product, allowed)
     * existsByNameAndIdNot("Desktop", "PROD-001") → true (conflicts with PROD-002)
     */
    boolean existsByNameAndIdNot(String name, String productId);

    /**
     * Find all products by category (including inactive)
     *
     * Custom JPQL query without active filter
     * Used in admin panel to show all products regardless of status
     *
     * Generated SQL:
     * SELECT * FROM products WHERE category_id = ?
     *
     * @param category - The Category entity to filter by
     * @return List of all products in the category (active and inactive)
     *
     * Examples:
     * Category electronics = categoryRepository.findById("ELEC-001").get();
     * List<Product> allElectronics = repository.findByCategory(electronics);
     * // Returns ALL products with this category (including deleted)
     *
     * Use Case: Admin panel inventory management
     * Admins need to see all products including deleted ones for recovery
     *
     * Note: Accepts Category entity object instead of String ID
     * More type-safe, allows JPA to handle relationship
     */
    List<Product> findByCategory(Category category);

    /**
     * Count products by category
     *
     * Method Name Query: Spring Data generates from method name
     * Naming Convention: countBy<FieldName>
     *
     * Generated SQL:
     * SELECT COUNT(*) FROM products WHERE category_id = ?
     *
     * @param categoryId - The category ID to count by
     * @return Total number of products in the category
     *
     * Examples:
     * long electronicsCount = repository.countByCategoryId("ELEC-001");
     * // Returns total products with category_id = "ELEC-001"
     *
     * Use Case: Display product count per category
     * Show users how many products in each category filter
     *
     * Note: Returns long (never null)
     */
    long countByCategoryId(String categoryId);

    /**
     * Check if product exists by ID and is active
     *
     * Custom JPQL query
     * Useful for retrieving products that are visible to customers
     *
     * Generated SQL:
     * SELECT COUNT(*) FROM products WHERE id = ? AND active = true
     *
     * @param id - The product ID
     * @return true if active product exists, false otherwise
     *
     * Examples:
     * boolean isActive = repository.existsByIdAndActiveTrue("PROD-001");
     * if (isActive) {
     *     // Show to customer
     * } else {
     *     // Product not found or is deleted
     *     throw new ResourceNotFoundException(...);
     * }
     *
     * Use Case: Customer product retrieval
     * Ensure deleted products cannot be accessed by customers
     */
    boolean existsByIdAndActiveTrue(String id);

}
