package com.wipro.productcatalog.controller;

import com.wipro.productcatalog.dto.request.CreateProductRequest;
import com.wipro.productcatalog.dto.request.UpdateProductRequest;
import com.wipro.productcatalog.dto.response.ErrorResponse;
import com.wipro.productcatalog.dto.response.ProductResponse;
import com.wipro.productcatalog.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ProductController
 *
 * REST API endpoints for product management
 * Base URL: /api/v1/products
 *
 * Responsibilities:
 * - Parse HTTP requests
 * - Extract request data
 * - Trigger validation (@Valid)
 * - Delegate to service layer
 * - Format HTTP responses
 * - Set appropriate status codes
 *
 * NOT Responsible for:
 * - Business logic (belongs to Service)
 * - Database queries (belongs to Repository)
 * - Data transformation (belongs to Service)
 *
 * Architecture:
 * Request → Controller → Service → Repository → Database
 *          ↑ Response flows back through layers ↑
 *
 * Exception Handling:
 * - Exceptions thrown in service layer
 * - GlobalExceptionHandler catches and formats
 * - Consistent error responses to client
 */
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Products", description = "Product management endpoints")
public class ProductController {

    // Dependency injection - Service layer
    private final ProductService productService;

    /**
     * POST /api/v1/products
     * Create a new product
     *
     * Feature: FR-4 (Add Product)
     * HTTP Method: POST
     * Status Code: 201 Created
     *
     * Request Body:
     * {
     *   "id": "PROD-001",
     *   "name": "Laptop Pro 15",
     *   "description": "High-performance laptop",
     *   "categoryId": "ELEC-001",
     *   "price": 999.99,
     *   "stockQuantity": 50
     * }
     *
     * Response: 201 Created
     * {
     *   "id": "PROD-001",
     *   "name": "Laptop Pro 15",
     *   "description": "High-performance laptop",
     *   "category": "Electronics",
     *   "price": 999.99,
     *   "stockQuantity": 50
     * }
     *
     * Validation (@Valid):
     * - @NotBlank: id, name, categoryId
     * - @Size: id (max 50), name (1-100), description (max 1000)
     * - @NotNull: price, stockQuantity
     * - @DecimalMin: price >= 0.01
     * - @Min: stockQuantity >= 0
     *
     * Exceptions:
     * - 400 Bad Request: Validation failed
     * - 409 Conflict: Duplicate ID/name
     * - 404 Not Found: Category not found
     *
     * @param request - CreateProductRequest DTO with product data
     * @return - ResponseEntity with created ProductResponse and 201 status
     */
    @PostMapping
    @Operation(summary = "Create a new product", description = "Add a new product to the catalog")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Product created successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductResponse.class))),
        @ApiResponse(responseCode = "400", description = "Validation failed",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "Duplicate product ID or name",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody CreateProductRequest request) {

        log.info("Creating product with ID: {}", request.getId());

        // Delegate to service (no business logic here)
        ProductResponse response = productService.addProduct(request);

        // Return 201 Created status
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response);
    }

    /**
     * GET /api/v1/products
     * Get all products with pagination and sorting
     *
     * Feature: FR-1 (View Products - List/Browse)
     * HTTP Method: GET
     * Status Code: 200 OK
     *
     * Query Parameters:
     * - page (default: 0) - Page number, 0-indexed
     * - size (default: 20) - Items per page
     * - sort (default: id,desc) - Sort by field and direction
     *
     * Examples:
     * GET /api/v1/products?page=0&size=20&sort=createdAt,desc
     * GET /api/v1/products?page=1&size=50
     * GET /api/v1/products?sort=price,asc
     *
     * Response: 200 OK
     * {
     *   "content": [
     *     {
     *       "id": "PROD-001",
     *       "name": "Laptop",
     *       "price": 999.99,
     *       "stockQuantity": 50
     *     },
     *     {
     *       "id": "PROD-002",
     *       "name": "Mouse",
     *       "price": 29.99,
     *       "stockQuantity": 200
     *     }
     *   ],
     *   "pageable": {
     *     "pageNumber": 0,
     *     "pageSize": 20,
     *     "totalElements": 150,
     *     "totalPages": 8
     *   }
     * }
     *
     * Exceptions: None (returns empty page if no products)
     *
     * @param pageable - Pagination and sorting info from query parameters
     * @return - ResponseEntity with Page of ProductResponse and 200 status
     */
    @GetMapping
    @Operation(summary = "Get all products", description = "Retrieve all products with pagination and sorting")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Products retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<ProductResponse>> getAllProducts(
            @Parameter(description = "Pagination and sorting parameters") Pageable pageable) {

        log.info("Fetching all products with pagination: page={}, size={}",
            pageable.getPageNumber(), pageable.getPageSize());

        // Delegate to service
        Page<ProductResponse> response = productService.getAllProducts(pageable);

        // Return 200 OK status
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(response);
    }

    /**
     * GET /api/v1/products/{id}
     * Get a specific product by ID
     *
     * Feature: FR-3 (Search Product by ID)
     * HTTP Method: GET
     * Status Code: 200 OK
     *
     * Path Parameter:
     * - id (required) - Product ID (e.g., "PROD-001")
     *
     * Examples:
     * GET /api/v1/products/PROD-001
     * GET /api/v1/products/PROD-999
     *
     * Response: 200 OK
     * {
     *   "id": "PROD-001",
     *   "name": "Laptop Pro 15",
     *   "description": "High-performance laptop",
     *   "category": "Electronics",
     *   "price": 999.99,
     *   "stockQuantity": 50
     * }
     *
     * Exceptions:
     * - 404 Not Found: Product with ID not found
     *
     * @param id - Product ID to retrieve
     * @return - ResponseEntity with ProductResponse and 200 status
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Retrieve a specific product by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductResponse.class))),
        @ApiResponse(responseCode = "404", description = "Product not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ProductResponse> getProductById(
            @Parameter(description = "Product ID") @PathVariable String id) {

        log.info("Fetching product by ID: {}", id);

        // Delegate to service (throws ProductNotFoundException if not found)
        ProductResponse response = productService.getProductById(id);

        // Return 200 OK status
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(response);
    }

    /**
     * PUT /api/v1/products/{id}
     * Update an existing product
     *
     * Feature: FR-5 (Update Product)
     * HTTP Method: PUT
     * Status Code: 200 OK
     *
     * Path Parameter:
     * - id (required) - Product ID to update (e.g., "PROD-001")
     *
     * Request Body:
     * {
     *   "name": "Updated Laptop Name",
     *   "description": "Updated description",
     *   "categoryId": "ELEC-001",
     *   "price": 1199.99,
     *   "stockQuantity": 45
     * }
     *
     * Note: All fields are optional (partial update)
     * Only provided fields will be updated
     *
     * Response: 200 OK
     * {
     *   "id": "PROD-001",
     *   "name": "Updated Laptop Name",
     *   "description": "Updated description",
     *   "category": "Electronics",
     *   "price": 1199.99,
     *   "stockQuantity": 45
     * }
     *
     * Validation (@Valid):
     * - @Size: name (1-100), description (max 1000)
     * - @DecimalMin: price >= 0.01 (if provided)
     * - @Min: stockQuantity >= 0 (if provided)
     *
     * Exceptions:
     * - 400 Bad Request: Validation failed
     * - 404 Not Found: Product with ID not found
     * - 409 Conflict: New name conflicts with another product
     *
     * @param id - Product ID to update
     * @param request - UpdateProductRequest DTO with updates
     * @return - ResponseEntity with updated ProductResponse and 200 status
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update product", description = "Update an existing product's information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product updated successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductResponse.class))),
        @ApiResponse(responseCode = "400", description = "Validation failed",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Product not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "Conflict: product name already exists",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ProductResponse> updateProduct(
            @Parameter(description = "Product ID") @PathVariable String id,
            @Valid @RequestBody UpdateProductRequest request) {

        log.info("Updating product with ID: {}", id);

        // Delegate to service
        ProductResponse response = productService.updateProduct(id, request);

        // Return 200 OK status
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(response);
    }

    /**
     * DELETE /api/v1/products/{id}
     * Delete a product from the catalog
     *
     * Feature: FR-6 (Delete Product)
     * HTTP Method: DELETE
     * Status Code: 204 No Content
     *
     * Path Parameter:
     * - id (required) - Product ID to delete (e.g., "PROD-001")
     *
     * Examples:
     * DELETE /api/v1/products/PROD-001
     *
     * Response: 204 No Content
     * (No response body)
     *
     * Implementation: Soft Delete
     * - Product marked as inactive (active = false)
     * - Product remains in database for audit trail
     * - Hidden from customer views
     * - Can be recovered by admin
     *
     * Exceptions:
     * - 404 Not Found: Product with ID not found
     *
     * @param id - Product ID to delete
     * @return - ResponseEntity with 204 No Content status
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product", description = "Remove a product from the catalog (soft delete)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Product not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "Product ID") @PathVariable String id) {

        log.info("Deleting product with ID: {}", id);

        // Delegate to service (throws ProductNotFoundException if not found)
        productService.deleteProduct(id);

        // Return 204 No Content (standard for successful DELETE)
        return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .build();
    }

    /**
     * GET /api/v1/products/category/{category}
     * Get all products in a specific category
     *
     * Feature: FR-2 (Search Products by Category)
     * HTTP Method: GET
     * Status Code: 200 OK
     *
     * Path Parameter:
     * - category (required) - Category ID (e.g., "ELEC-001")
     *
     * Examples:
     * GET /api/v1/products/category/ELEC-001
     * GET /api/v1/products/category/CLOTH-001
     *
     * Response: 200 OK
     * [
     *   {
     *     "id": "PROD-001",
     *     "name": "Laptop",
     *     "category": "Electronics",
     *     "price": 999.99,
     *     "stockQuantity": 50
     *   },
     *   {
     *     "id": "PROD-002",
     *     "name": "Mouse",
     *     "category": "Electronics",
     *     "price": 29.99,
     *     "stockQuantity": 200
     *   }
     * ]
     *
     * Exceptions:
     * - 404 Not Found: Category with ID not found
     *
     * @param categoryId - Category ID to filter by
     * @return - ResponseEntity with List of ProductResponse and 200 status
     */
    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get products by category", description = "Retrieve all products in a specific category")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Products retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductResponse.class))),
        @ApiResponse(responseCode = "404", description = "Category not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<ProductResponse>> getProductsByCategory(
            @Parameter(description = "Category ID") @PathVariable String categoryId) {

        log.info("Fetching products by category: {}", categoryId);

        // Delegate to service
        List<ProductResponse> response = productService.getProductsByCategory(categoryId);

        // Return 200 OK status
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(response);
    }

}
