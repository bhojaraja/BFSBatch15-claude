package com.wipro.productcatalog.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ErrorResponse DTO
 *
 * Standard error response object sent to client when exceptions occur
 * Provides consistent error format across all API endpoints
 *
 * Example Response:
 * {
 *   "timestamp": "2026-10-26T10:30:00",
 *   "status": 404,
 *   "message": "Product with ID 'PROD-999' not found",
 *   "error": "PRODUCT_NOT_FOUND",
 *   "details": [
 *     {
 *       "field": "id",
 *       "message": "Product not found"
 *     }
 *   ]
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    @Schema(description = "Timestamp when error occurred (ISO 8601)", example = "2026-10-26T10:30:00")
    private LocalDateTime timestamp;

    @Schema(description = "HTTP status code", example = "404")
    private Integer status;

    @Schema(description = "Human-readable error message", example = "Product with ID 'PROD-001' not found")
    private String message;

    @Schema(description = "Machine-readable error code", example = "PRODUCT_NOT_FOUND")
    private String error;

    @Schema(description = "Request path that caused the error", example = "/api/v1/products/PROD-001")
    private String path;

    @Schema(description = "Unique request ID for tracing", example = "req-abc-123-xyz")
    private String requestId;

    @Schema(description = "Field-level validation errors (for 400 responses)")
    private List<FieldError> details;

    /**
     * FieldError nested class for validation details
     * Provides field-level error information
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FieldError {

        @Schema(description = "Field name that caused validation error", example = "price")
        private String field;

        @Schema(description = "Error message for this field", example = "Price must be greater than 0")
        private String message;
    }

}
