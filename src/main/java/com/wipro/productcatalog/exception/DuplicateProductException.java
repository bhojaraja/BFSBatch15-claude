package com.wipro.productcatalog.exception;

/**
 * DuplicateProductException
 *
 * Exception thrown when attempting to create a product with duplicate identifier or name
 * HTTP Status: 409 Conflict
 *
 * Extends RuntimeException (Unchecked Exception)
 * - No need to declare in method signature
 * - No need to catch or handle explicitly
 * - Propagates to Global Exception Handler
 *
 * When Thrown:
 * - User tries to create product with existing ID (FR-4)
 * - User tries to create product with existing name
 * - Duplicate constraint violation from database
 * - Two concurrent create requests with same ID
 *
 * Example 1 - Duplicate ID:
 * POST /api/v1/products
 * {
 *   "id": "PROD-001",  // Already exists
 *   "name": "Laptop",
 *   "price": 999.99
 * }
 * Response: 409 Conflict
 * {
 *   "statusCode": 409,
 *   "error": "DUPLICATE_PRODUCT",
 *   "message": "Product with ID 'PROD-001' already exists"
 * }
 *
 * Example 2 - Duplicate Name:
 * POST /api/v1/products
 * {
 *   "id": "PROD-999",
 *   "name": "Laptop",  // Already exists with different ID
 *   "price": 999.99
 * }
 * Response: 409 Conflict
 * {
 *   "statusCode": 409,
 *   "error": "DUPLICATE_PRODUCT",
 *   "message": "Product name 'Laptop' already exists"
 * }
 */
public class DuplicateProductException extends RuntimeException {

    private static final String ERROR_CODE = "DUPLICATE_PRODUCT";
    private static final int HTTP_STATUS = 409;

    /**
     * Constructor with message
     *
     * @param message - Descriptive error message
     *
     * Examples:
     * throw new DuplicateProductException("Product with ID 'PROD-001' already exists");
     * throw new DuplicateProductException("Product name 'Laptop' already exists");
     */
    public DuplicateProductException(String message) {
        super(message);
    }

    /**
     * Constructor with message and cause
     *
     * @param message - Descriptive error message
     * @param cause - Root cause exception
     *
     * Example:
     * catch (DataIntegrityViolationException ex) {
     *     throw new DuplicateProductException("Product ID already exists", ex);
     * }
     */
    public DuplicateProductException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Get the error code for this exception
     *
     * @return Error code for API response
     */
    public String getErrorCode() {
        return ERROR_CODE;
    }

    /**
     * Get the HTTP status code for this exception
     *
     * @return HTTP status code (409)
     */
    public int getHttpStatus() {
        return HTTP_STATUS;
    }

}
