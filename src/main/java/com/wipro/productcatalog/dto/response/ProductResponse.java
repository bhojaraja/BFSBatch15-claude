package com.wipro.productcatalog.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * ProductResponse DTO
 *
 * Data Transfer Object for sending product information to the client
 * Used in API responses (HTTP response body)
 * No validation annotations (responses are generated internally)
 *
 * Example Response:
 * {
 *   "id": "PROD-001",
 *   "name": "Laptop Pro 15",
 *   "description": "High-performance laptop with Intel i7",
 *   "category": "Electronics",
 *   "price": 999.99,
 *   "stockQuantity": 50
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {

    @Schema(description = "Product ID", example = "PROD-001")
    private String id;

    @Schema(description = "Product name", example = "Laptop Pro 15")
    private String name;

    @Schema(description = "Product description", example = "High-performance laptop with Intel i7 processor")
    private String description;

    @Schema(description = "Category name", example = "Electronics")
    private String category;

    @Schema(description = "Product price", example = "999.99")
    private BigDecimal price;

    @Schema(description = "Available stock quantity", example = "50")
    private Integer stockQuantity;

}
