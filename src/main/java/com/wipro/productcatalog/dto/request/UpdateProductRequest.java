package com.wipro.productcatalog.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * UpdateProductRequest DTO
 *
 * Data Transfer Object for updating an existing product
 * All fields are optional (partial update)
 * Only provided fields will be updated
 *
 * Example Request:
 * PUT /api/v1/products/PROD-001
 * {
 *   "name": "Updated Laptop Name",
 *   "price": 1199.99,
 *   "stockQuantity": 45
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProductRequest {

    @Schema(description = "Product name", example = "Updated Laptop Name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    private String name;

    @Schema(description = "Product description", example = "Updated description", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @Schema(description = "Category identifier", example = "ELEC-001", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Size(max = 50, message = "Category ID cannot exceed 50 characters")
    private String categoryId;

    @Schema(description = "Product price", example = "1199.99", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @DecimalMax(value = "999999.99", message = "Price cannot exceed 999999.99")
    private BigDecimal price;

    @Schema(description = "Product stock quantity", example = "45", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Min(value = 0, message = "Stock quantity cannot be negative")
    @Max(value = 1000000, message = "Stock quantity cannot exceed 1,000,000")
    private Integer stockQuantity;

}
