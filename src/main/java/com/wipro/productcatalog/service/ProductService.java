package com.wipro.productcatalog.service;

import com.wipro.productcatalog.dto.request.CreateProductRequest;
import com.wipro.productcatalog.dto.request.UpdateProductRequest;
import com.wipro.productcatalog.dto.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * ProductService Interface
 *
 * Service layer contract for product-related business operations
 * Defines all business logic methods for product management
 *
 * Responsibilities:
 * - Business logic validation
 * - Data transformation (Entity ↔ DTO)
 * - Cross-layer operations
 * - Exception handling and conversion
 *
 * Implementation: ProductServiceImpl
 */
public interface ProductService {

    /**
     * Add a new product to the catalog
     *
     * Business Logic:
     * - Validate product data
     * - Check for duplicate ID
     * - Check for duplicate name (optional)
     * - Verify category exists
     * - Save to database
     * - Return created product
     *
     * @param request - Create product request with product details
     * @return - Created product response
     *
     * @throws DuplicateProductException - If product ID or name already exists
     * @throws ProductNotFoundException - If category not found
     * @throws ValidationException - If validation fails
     *
     * Use Case: FR-4 (Add Product)
     * Endpoint: POST /api/v1/products
     *
     * Example:
     * CreateProductRequest request = CreateProductRequest.builder()
     *     .id("PROD-001")
     *     .name("Laptop")
     *     .categoryId("ELEC-001")
     *     .price(new BigDecimal("999.99"))
     *     .stockQuantity(50)
     *     .build();
     *
     * ProductResponse response = service.addProduct(request);
     */
    ProductResponse addProduct(CreateProductRequest request);

    /**
     * Get all products with pagination and sorting
     *
     * Business Logic:
     * - Retrieve all active products
     * - Apply pagination (page, pageSize)
     * - Apply sorting (sortBy, sortOrder)
     * - Transform entities to DTOs
     * - Return paginated results
     *
     * @param pageable - Pagination and sorting information
     * @return - Page of products with pagination metadata
     *
     * Use Case: FR-1 (View Products - List/Browse)
     * Endpoint: GET /api/v1/products?page=1&pageSize=20&sort=createdAt,desc
     *
     * Example:
     * Pageable pageable = PageRequest.of(0, 20, Sort.by("createdAt").descending());
     * Page<ProductResponse> page = service.getAllProducts(pageable);
     * // Returns: 20 products per page, sorted by creation date
     */
    Page<ProductResponse> getAllProducts(Pageable pageable);

    /**
     * Get a specific product by ID
     *
     * Business Logic:
     * - Validate product ID format
     * - Search product in database
     * - Transform entity to DTO
     * - Return product details
     *
     * @param id - Product ID to retrieve
     * @return - Product details
     *
     * @throws ProductNotFoundException - If product not found or is deleted
     *
     * Use Case: FR-3 (Search Product by ID)
     * Endpoint: GET /api/v1/products/{id}
     *
     * Example:
     * ProductResponse response = service.getProductById("PROD-001");
     * // Returns: Full product details
     */
    ProductResponse getProductById(String id);

    /**
     * Update an existing product
     *
     * Business Logic:
     * - Validate product ID exists
     * - Validate update data
     * - Check for duplicate name (excluding current product)
     * - Check optimistic locking version
     * - Merge updates with existing product
     * - Save to database
     * - Return updated product
     *
     * @param id - Product ID to update
     * @param request - Update product request with new values
     * @return - Updated product response
     *
     * @throws ProductNotFoundException - If product not found
     * @throws DuplicateProductException - If new name conflicts with other products
     * @throws VersionConflictException - If version mismatch (concurrent update)
     * @throws ValidationException - If validation fails
     *
     * Use Case: FR-5 (Update Product)
     * Endpoint: PUT /api/v1/products/{id}
     *
     * Example:
     * UpdateProductRequest request = UpdateProductRequest.builder()
     *     .name("Laptop Pro Max")
     *     .price(new BigDecimal("1199.99"))
     *     .stockQuantity(45)
     *     .version(1)
     *     .build();
     *
     * ProductResponse response = service.updateProduct("PROD-001", request);
     * // Returns: Updated product with new version number
     */
    ProductResponse updateProduct(String id, UpdateProductRequest request);

    /**
     * Delete a product from the catalog
     *
     * Business Logic:
     * - Validate product ID exists
     * - Perform soft delete (mark as inactive)
     * - Preserve data for audit trail
     * - Don't actually remove from database
     *
     * @param id - Product ID to delete
     *
     * @throws ProductNotFoundException - If product not found
     * @throws DeletionException - If deletion fails
     *
     * Use Case: FR-6 (Delete Product)
     * Endpoint: DELETE /api/v1/products/{id}
     *
     * Note: Soft Delete
     * - Product remains in database but marked as active=false
     * - Hidden from customer views
     * - Visible in admin panel for recovery
     * - Data preserved for analytics/audit
     *
     * Example:
     * service.deleteProduct("PROD-001");
     * // Product is now hidden from customers but still in database
     */
    void deleteProduct(String id);

    /**
     * Get all products in a specific category
     *
     * Business Logic:
     * - Validate category ID exists
     * - Retrieve products in that category
     * - Only include active products (soft delete aware)
     * - Transform entities to DTOs
     * - Return matching products
     *
     * @param categoryId - Category ID to filter by
     * @return - List of products in the category
     *
     * @throws CategoryNotFoundException - If category not found
     *
     * Use Case: FR-2 (Search Products by Category)
     * Endpoint: GET /api/v1/products/category/{categoryId}
     *
     * Example:
     * List<ProductResponse> products = service.getProductsByCategory("ELEC-001");
     * // Returns: All active products in Electronics category
     * // ["Laptop", "Mouse", "USB Cable", ...]
     */
    List<ProductResponse> getProductsByCategory(String categoryId);

}
