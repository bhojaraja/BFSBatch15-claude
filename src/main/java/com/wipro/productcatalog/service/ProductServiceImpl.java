package com.wipro.productcatalog.service;

import com.wipro.productcatalog.dto.request.CreateProductRequest;
import com.wipro.productcatalog.dto.request.UpdateProductRequest;
import com.wipro.productcatalog.dto.response.ProductResponse;
import com.wipro.productcatalog.entity.Category;
import com.wipro.productcatalog.entity.Product;
import com.wipro.productcatalog.exception.DuplicateProductException;
import com.wipro.productcatalog.exception.ProductNotFoundException;
import com.wipro.productcatalog.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ProductServiceImpl
 *
 * Implementation of ProductService interface
 * Handles all business logic for product management
 *
 * Architecture:
 * - Dependency Injection: Constructor injection with @RequiredArgsConstructor
 * - DTO Pattern: Request → Entity → Response transformations
 * - Transaction Management: @Transactional for data consistency
 * - Exception Handling: Custom exceptions for business errors
 * - Logging: Info/warn/error for audit trail
 *
 * Business Rules:
 * 1. Product name is mandatory (validated in DTO)
 * 2. Price must be > 0 (validated in DTO)
 * 3. Stock must be >= 0 (validated in DTO)
 * 4. Product ID must be unique
 * 5. Product name should be unique (business decision)
 * 6. Category must exist
 * 7. Soft delete: mark as inactive, don't remove from DB
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    // Dependencies injected via constructor
    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    /**
     * Add a new product to the catalog
     *
     * Business Logic:
     * 1. Check product ID uniqueness
     * 2. Check product name uniqueness
     * 3. Verify category exists
     * 4. Create product entity
     * 5. Save to database
     * 6. Transform and return response
     *
     * @param request - Create product request with product details
     * @return - Created product response
     * @throws DuplicateProductException - If ID or name already exists
     * @throws ProductNotFoundException - If category not found
     */
    @Override
    @Transactional
    public ProductResponse addProduct(CreateProductRequest request) {
        log.info("Creating new product with ID: {}", request.getId());

        // Business Rule: Check for duplicate ID
        if (productRepository.existsById(request.getId())) {
            log.warn("Attempt to create product with duplicate ID: {}", request.getId());
            throw new DuplicateProductException(
                "Product with ID '" + request.getId() + "' already exists"
            );
        }

        // Business Rule: Check for duplicate name
        if (productRepository.existsByName(request.getName())) {
            log.warn("Attempt to create product with duplicate name: {}", request.getName());
            throw new DuplicateProductException(
                "Product name '" + request.getName() + "' already exists"
            );
        }

        // Business Rule: Verify category exists
        Category category = categoryService.getCategoryById(request.getCategoryId());
        if (category == null) {
            log.error("Category not found: {}", request.getCategoryId());
            throw new ProductNotFoundException(
                "Category with ID '" + request.getCategoryId() + "' not found"
            );
        }

        // Create product entity from request
        Product product = Product.builder()
            .id(request.getId())
            .name(request.getName())
            .description(request.getDescription())
            .category(category)
            .price(request.getPrice())
            .stockQuantity(request.getStockQuantity())
            .build();

        // Save to database (@PrePersist sets createdAt, updatedAt, version)
        Product savedProduct = productRepository.save(product);
        log.info("Product created successfully: {}", savedProduct.getId());

        // Transform to DTO and return
        return toResponse(savedProduct);
    }

    /**
     * Get all products with pagination and sorting
     *
     * Business Logic:
     * 1. Query all active products (active = true)
     * 2. Apply pagination (page number, page size)
     * 3. Apply sorting (sort by field and direction)
     * 4. Transform each product to DTO
     * 5. Return paginated response
     *
     * @param pageable - Pagination and sorting information
     * @return - Page of products with pagination metadata
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        log.info("Fetching all products with pagination: page={}, size={}",
            pageable.getPageNumber(), pageable.getPageSize());

        // Query products (Pageable handles pagination and sorting)
        Page<Product> products = productRepository.findAll(pageable);

        // Transform entities to DTOs
        Page<ProductResponse> response = products.map(this::toResponse);
        log.info("Retrieved {} products", response.getContent().size());

        return response;
    }

    /**
     * Get a specific product by ID
     *
     * Business Logic:
     * 1. Query product by ID
     * 2. Throw exception if not found
     * 3. Transform to DTO
     * 4. Return response
     *
     * @param id - Product ID to retrieve
     * @return - Product details
     * @throws ProductNotFoundException - If product not found or is deleted
     */
    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(String id) {
        log.info("Fetching product by ID: {}", id);

        // Query product by ID
        Product product = productRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("Product not found with ID: {}", id);
                return new ProductNotFoundException(
                    "Product with ID '" + id + "' not found"
                );
            });

        log.info("Product found: {}", id);
        return toResponse(product);
    }

    /**
     * Update an existing product
     *
     * Business Logic:
     * 1. Find product by ID (throw if not found)
     * 2. Check new name for duplicates (excluding current product)
     * 3. Check optimistic locking version
     * 4. Merge updates into entity
     * 5. Save to database (@PreUpdate increments version)
     * 6. Transform to DTO
     * 7. Return response
     *
     * @param id - Product ID to update
     * @param request - Update product request with new values
     * @return - Updated product response
     * @throws ProductNotFoundException - If product not found
     * @throws DuplicateProductException - If new name conflicts with other products
     */
    @Override
    @Transactional
    public ProductResponse updateProduct(String id, UpdateProductRequest request) {
        log.info("Updating product with ID: {}", id);

        // Business Rule: Find product or throw exception
        Product product = productRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("Product not found for update: {}", id);
                return new ProductNotFoundException(
                    "Product with ID '" + id + "' not found"
                );
            });

        // Business Rule: Check new name for duplicates (excluding current product)
        if (request.getName() != null && !request.getName().equals(product.getName())) {
            if (productRepository.existsByNameAndIdNot(request.getName(), id)) {
                log.warn("Attempt to update product with duplicate name: {}", request.getName());
                throw new DuplicateProductException(
                    "Product name '" + request.getName() + "' already exists"
                );
            }
        }

        // Merge updates into existing product
        if (request.getName() != null) {
            product.setName(request.getName());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }
        if (request.getStockQuantity() != null) {
            product.setStockQuantity(request.getStockQuantity());
        }
        if (request.getCategoryId() != null) {
            Category category = categoryService.getCategoryById(request.getCategoryId());
            if (category == null) {
                log.error("Category not found for update: {}", request.getCategoryId());
                throw new ProductNotFoundException(
                    "Category with ID '" + request.getCategoryId() + "' not found"
                );
            }
            product.setCategory(category);
        }

        // Save to database (@PreUpdate sets updatedAt, increments version)
        Product updatedProduct = productRepository.save(product);
        log.info("Product updated successfully: {} (new version: {})",
            updatedProduct.getId(), updatedProduct.getVersion());

        return toResponse(updatedProduct);
    }

    /**
     * Delete a product from the catalog
     *
     * Business Logic:
     * 1. Find product by ID (throw if not found)
     * 2. Soft delete: Set active = false
     * 3. Save to database
     * 4. Note: Data remains in DB for audit trail
     *
     * @param id - Product ID to delete
     * @throws ProductNotFoundException - If product not found
     */
    @Override
    @Transactional
    public void deleteProduct(String id) {
        log.info("Deleting product with ID: {}", id);

        // Business Rule: Find product or throw exception
        Product product = productRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("Product not found for deletion: {}", id);
                return new ProductNotFoundException(
                    "Product with ID '" + id + "' not found"
                );
            });

        // Soft delete: Mark as inactive
        product.setActive(false);

        // Save to database (@PreUpdate sets updatedAt)
        productRepository.save(product);
        log.info("Product deleted successfully (soft delete): {}", id);
    }

    /**
     * Get all products in a specific category
     *
     * Business Logic:
     * 1. Verify category exists
     * 2. Query products by category
     * 3. Only active products shown (soft delete aware)
     * 4. Transform to DTOs
     * 5. Return list
     *
     * @param categoryId - Category ID to filter by
     * @return - List of products in the category
     * @throws ProductNotFoundException - If category not found
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByCategory(String categoryId) {
        log.info("Fetching products for category: {}", categoryId);

        // Business Rule: Verify category exists
        Category category = categoryService.getCategoryById(categoryId);
        if (category == null) {
            log.error("Category not found: {}", categoryId);
            throw new ProductNotFoundException(
                "Category with ID '" + categoryId + "' not found"
            );
        }

        // Query active products in category
        List<Product> products = productRepository.findActiveByCategoryId(categoryId);
        log.info("Retrieved {} products for category: {}", products.size(), categoryId);

        // Transform to DTOs
        return products.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    /**
     * Transform Product Entity to ProductResponse DTO
     *
     * Mapping:
     * - id → id
     * - name → name
     * - description → description
     * - category.name → category (extract category name)
     * - price → price
     * - stockQuantity → stockQuantity
     *
     * Note: Not included in response (internal fields):
     * - active, createdAt, updatedAt, createdBy, updatedBy, version
     *
     * @param product - Product entity to transform
     * @return - ProductResponse DTO
     */
    private ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
            .id(product.getId())
            .name(product.getName())
            .description(product.getDescription())
            .category(product.getCategory() != null ? product.getCategory().getName() : null)
            .price(product.getPrice())
            .stockQuantity(product.getStockQuantity())
            .build();
    }

}
