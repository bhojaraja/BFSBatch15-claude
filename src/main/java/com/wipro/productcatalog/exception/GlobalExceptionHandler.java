package com.wipro.productcatalog.exception;

import com.wipro.productcatalog.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * GlobalExceptionHandler
 *
 * Centralized exception handling for entire application
 * Catches exceptions from all controllers and returns consistent error responses
 *
 * @ControllerAdvice:
 * - Aspect-oriented approach for exception handling
 * - Handles exceptions thrown by any controller
 * - Returns standardized error responses
 * - No need for try-catch in controllers
 *
 * Exception Flow:
 * Controller → Service → Exception thrown
 *     ↓ (propagates up)
 * GlobalExceptionHandler catches
 *     ↓
 * @ExceptionHandler method invoked
 *     ↓
 * ErrorResponse returned (JSON)
 *
 * Handled Exceptions:
 * - ProductNotFoundException (404)
 * - DuplicateProductException (409)
 * - MethodArgumentNotValidException (400) - validation errors
 * - Exception (500) - generic fallback
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handle ProductNotFoundException (404 Not Found)
     *
     * Triggered when:
     * - Product not found by ID
     * - Product was deleted
     * - Category not found
     * - Resource doesn't exist
     *
     * HTTP Status: 404 Not Found
     *
     * @param ex - ProductNotFoundException thrown by service
     * @param request - Current HTTP request
     * @return - ErrorResponse with 404 status
     *
     * Example Response:
     * {
     *   "timestamp": "2026-10-26T10:30:00",
     *   "status": 404,
     *   "message": "Product with ID 'PROD-999' not found",
     *   "error": "PRODUCT_NOT_FOUND",
     *   "path": "/api/v1/products/PROD-999",
     *   "requestId": "req-abc-123"
     * }
     */
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFoundException(
            ProductNotFoundException ex,
            HttpServletRequest request) {

        log.warn("Product not found exception: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(404)
            .message(ex.getMessage())
            .error("PRODUCT_NOT_FOUND")
            .path(request.getRequestURI())
            .requestId(generateRequestId())
            .build();

        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(errorResponse);
    }

    /**
     * Handle DuplicateProductException (409 Conflict)
     *
     * Triggered when:
     * - Product ID already exists
     * - Product name already exists
     * - Duplicate constraint violation
     * - Resource conflict
     *
     * HTTP Status: 409 Conflict
     *
     * @param ex - DuplicateProductException thrown by service
     * @param request - Current HTTP request
     * @return - ErrorResponse with 409 status
     *
     * Example Response:
     * {
     *   "timestamp": "2026-10-26T10:30:00",
     *   "status": 409,
     *   "message": "Product with ID 'PROD-001' already exists",
     *   "error": "DUPLICATE_PRODUCT",
     *   "path": "/api/v1/products",
     *   "requestId": "req-xyz-789"
     * }
     */
    @ExceptionHandler(DuplicateProductException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateProductException(
            DuplicateProductException ex,
            HttpServletRequest request) {

        log.warn("Duplicate product exception: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(409)
            .message(ex.getMessage())
            .error("DUPLICATE_PRODUCT")
            .path(request.getRequestURI())
            .requestId(generateRequestId())
            .build();

        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(errorResponse);
    }

    /**
     * Handle MethodArgumentNotValidException (400 Bad Request)
     *
     * Triggered when:
     * - @Valid annotation validation fails
     * - Request body validation fails
     * - DTO validation errors
     * - Constraint violations
     *
     * Examples:
     * - @NotBlank violation: "Product name is required"
     * - @Size violation: "Name must be between 1 and 100 characters"
     * - @DecimalMin violation: "Price must be greater than 0"
     * - @Min violation: "Stock quantity cannot be negative"
     *
     * HTTP Status: 400 Bad Request
     *
     * @param ex - MethodArgumentNotValidException thrown by Spring validation
     * @param request - Current HTTP request
     * @return - ErrorResponse with 400 status and field errors
     *
     * Example Response:
     * {
     *   "timestamp": "2026-10-26T10:30:00",
     *   "status": 400,
     *   "message": "Validation failed",
     *   "error": "VALIDATION_ERROR",
     *   "path": "/api/v1/products",
     *   "requestId": "req-val-456",
     *   "details": [
     *     {
     *       "field": "price",
     *       "message": "Price must be greater than 0"
     *     },
     *     {
     *       "field": "name",
     *       "message": "Product name is required"
     *     },
     *     {
     *       "field": "stockQuantity",
     *       "message": "Stock quantity cannot be negative"
     *     }
     *   ]
     * }
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        log.warn("Validation failed for request: {}", request.getRequestURI());

        // Extract field errors from binding result
        List<ErrorResponse.FieldError> fieldErrors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> ErrorResponse.FieldError.builder()
                .field(error.getField())
                .message(error.getDefaultMessage())
                .build())
            .collect(Collectors.toList());

        ErrorResponse errorResponse = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(400)
            .message("Validation failed")
            .error("VALIDATION_ERROR")
            .path(request.getRequestURI())
            .requestId(generateRequestId())
            .details(fieldErrors)
            .build();

        log.debug("Validation errors: {}", fieldErrors);

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(errorResponse);
    }

    /**
     * Handle generic exceptions (500 Internal Server Error)
     *
     * Triggered when:
     * - Unexpected exceptions occur
     * - Database errors
     * - NullPointerException
     * - Any unhandled exception
     *
     * HTTP Status: 500 Internal Server Error
     *
     * @param ex - Any exception not explicitly handled
     * @param request - Current HTTP request
     * @return - ErrorResponse with 500 status
     *
     * Security Note:
     * - Don't expose stack traces to client
     * - Log full details on server side
     * - Return generic message to client
     *
     * Example Response:
     * {
     *   "timestamp": "2026-10-26T10:30:00",
     *   "status": 500,
     *   "message": "An error occurred processing your request",
     *   "error": "INTERNAL_ERROR",
     *   "path": "/api/v1/products",
     *   "requestId": "req-err-999"
     * }
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        log.error("Unexpected exception occurred", ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(500)
            .message("An error occurred processing your request")
            .error("INTERNAL_ERROR")
            .path(request.getRequestURI())
            .requestId(generateRequestId())
            .build();

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(errorResponse);
    }

    /**
     * Handle 404 Not Found (No handler found)
     *
     * Triggered when:
     * - Request path doesn't match any endpoint
     * - Invalid URL path
     *
     * HTTP Status: 404 Not Found
     *
     * @param ex - NoHandlerFoundException thrown by Spring
     * @param request - Current HTTP request
     * @return - ErrorResponse with 404 status
     *
     * Note: Requires spring.mvc.throw-exception-if-no-handler-found=true
     *       in application.properties
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFoundException(
            NoHandlerFoundException ex,
            HttpServletRequest request) {

        log.warn("No handler found for request: {}", request.getRequestURI());

        ErrorResponse errorResponse = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(404)
            .message("Endpoint not found")
            .error("ENDPOINT_NOT_FOUND")
            .path(request.getRequestURI())
            .requestId(generateRequestId())
            .build();

        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(errorResponse);
    }

    /**
     * Generate unique request ID for tracing
     *
     * Used for tracking requests through logs
     * Helpful for debugging and monitoring
     *
     * @return - Unique request ID
     *
     * Example: "req-550e8400-e29b-41d4-a716-446655440000"
     */
    private String generateRequestId() {
        return "req-" + UUID.randomUUID().toString();
    }

}
