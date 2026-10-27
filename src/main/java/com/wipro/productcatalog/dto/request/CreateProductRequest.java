package com.wipro.productcatalog.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * CreateProductRequest DTO
 *
 * Data Transfer Object for creating a new product
 * Accepts product information from the client (HTTP request body)
 * Validates all input data before passing to service layer
 *
 * Validation Flow:
 * 1. Client sends JSON request
 * 2. Spring deserializes JSON to this DTO
 * 3. @Valid trigger bean validation annotations
 * 4. If validation fails: MethodArgumentNotValidException thrown
 * 5. If validation passes: DTO passed to controller
 *
 * Example Request:
 * POST /api/v1/products
 * {
 *   "id": "PROD-001",
 *   "name": "Laptop Pro 15",
 *   "description": "High-performance laptop with Intel i7",
 *   "categoryId": "ELEC-001",
 *   "price": 999.99,
 *   "stockQuantity": 50
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProductRequest {

    /**
     * Product ID
     * Format: PROD-XXX (alphanumeric with hyphens)
     * User provides this ID (manually assigned)
     *
     * Validation Rules:
     * - Required: Must not be blank
     * - Format: Maximum 50 characters
     * - Pattern: Alphanumeric and hyphens only (optional)
     *
     * Annotations Used:
     * @NotBlank - Cannot be null, empty, or whitespace
     * @Size - Restricts string length
     * @Pattern - Validates against regex (optional but recommended)
     */
    @Schema(description = "Unique product identifier", example = "PROD-001", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Product ID is required")
    @Size(max = 50, message = "Product ID cannot exceed 50 characters")
    @Pattern(
        regexp = "^[A-Z0-9-]+$",
        message = "Product ID must contain only uppercase letters, numbers, and hyphens"
    )
    private String id;

    /**
     * Product Name
     * Descriptive name of the product
     *
     * Validation Rules:
     * - Required: Must not be blank
     * - Length: Between 1 and 100 characters
     *
     * Annotations Used:
     * @NotBlank - Cannot be null, empty, or whitespace
     * @Size - Restricts string length with min and max
     *
     * Examples:
     * ✓ "Laptop Pro 15"
     * ✓ "Wireless Mouse"
     * ✗ "" (empty - INVALID)
     * ✗ "   " (whitespace only - INVALID)
     * ✗ "A product with a very long name that exceeds 100 characters limit..." (INVALID)
     */
    @Schema(description = "Product name", example = "Laptop Pro 15", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Product name is required")
    @Size(
        min = 1,
        max = 100,
        message = "Product name must be between 1 and 100 characters"
    )
    private String name;

    /**
     * Product Description
     * Detailed information about the product
     *
     * Validation Rules:
     * - Optional: Can be null or blank
     * - Length: Maximum 1000 characters if provided
     *
     * Annotations Used:
     * @Size - Maximum length constraint (min not specified, so 0 is allowed)
     *
     * Examples:
     * ✓ null
     * ✓ "" (empty string)
     * ✓ "High-performance laptop with Intel i7 processor"
     * ✗ "Very long description exceeding 1000 characters..." (INVALID)
     */
    @Schema(description = "Product description", example = "High-performance laptop with Intel i7 processor", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Size(
        max = 1000,
        message = "Product description cannot exceed 1000 characters"
    )
    private String description;

    /**
     * Category ID
     * Reference to the product category
     *
     * Validation Rules:
     * - Required: Must not be blank
     * - Format: Maximum 50 characters
     *
     * Annotations Used:
     * @NotBlank - Cannot be null, empty, or whitespace
     * @Size - Maximum length constraint
     *
     * Note: This DTO only stores category ID
     * Service layer will validate that category exists in database
     * That is a cross-layer validation (DTO validates format, Service validates existence)
     *
     * Examples:
     * ✓ "ELEC-001"
     * ✓ "CLOTH-001"
     * ✗ "" (empty - INVALID)
     * ✗ null (null - INVALID)
     */
    @Schema(description = "Category identifier", example = "ELEC-001", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Category ID is required")
    @Size(max = 50, message = "Category ID cannot exceed 50 characters")
    private String categoryId;

    /**
     * Product Price
     * Monetary value of the product
     *
     * Validation Rules:
     * - Required: Must not be null
     * - Constraint: Must be greater than 0 (decimal: 0.01 minimum)
     * - Maximum: 999999.99 (supports 2 decimal places)
     *
     * Annotations Used:
     * @NotNull - Cannot be null (for wrapper types like BigDecimal)
     * @DecimalMin - Minimum decimal value (inclusive)
     * @DecimalMax - Maximum decimal value (inclusive)
     *
     * Why BigDecimal?
     * - Exact decimal representation (not float/double rounding errors)
     * - Suitable for monetary values
     * - Required for financial calculations
     *
     * Examples:
     * ✓ 999.99
     * ✓ 0.01
     * ✓ 149.50
     * ✗ null (INVALID)
     * ✗ 0 (INVALID - must be > 0)
     * ✗ -50.00 (INVALID - negative)
     * ✗ 1000000.00 (INVALID - exceeds max)
     */
    @Schema(description = "Product price", example = "999.99", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Product price is required")
    @DecimalMin(
        value = "0.01",
        message = "Product price must be greater than 0"
    )
    @DecimalMax(
        value = "999999.99",
        message = "Product price cannot exceed 999999.99"
    )
    private BigDecimal price;

    /**
     * Stock Quantity
     * Number of items available in inventory
     *
     * Validation Rules:
     * - Required: Must not be null
     * - Constraint: Must be 0 or more (non-negative)
     * - Maximum: 1,000,000 units
     *
     * Annotations Used:
     * @NotNull - Cannot be null (for wrapper types like Integer)
     * @Min - Minimum value (inclusive)
     * @Max - Maximum value (inclusive)
     *
     * Why allow 0?
     * - Product can be out of stock (stockQuantity = 0)
     * - User can create product before stock arrives
     * - Allows pre-order scenarios
     *
     * Examples:
     * ✓ 0 (out of stock - valid)
     * ✓ 50
     * ✓ 1000
     * ✗ null (INVALID)
     * ✗ -5 (INVALID - negative)
     * ✗ 1000001 (INVALID - exceeds max)
     */
    @Schema(description = "Product stock quantity", example = "50", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Stock quantity is required")
    @Min(
        value = 0,
        message = "Stock quantity cannot be negative"
    )
    @Max(
        value = 1000000,
        message = "Stock quantity cannot exceed 1,000,000"
    )
    private Integer stockQuantity;

}
