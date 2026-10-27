package com.wipro.productcatalog.exception;

/**
 * ProductNotFoundException
 *
 * Exception thrown when a requested product is not found in the database
 * HTTP Status: 404 Not Found
 *
 * Extends RuntimeException (Unchecked Exception)
 * - No need to declare in method signature
 * - No need to catch or handle explicitly
 * - Propagates to Global Exception Handler
 *
 * When Thrown:
 * - User searches for non-existent product by ID (FR-3)
 * - User tries to update non-existent product
 * - User tries to delete non-existent product
 * - Product was deleted while user was viewing it
 *
 * Example:
 * GET /api/v1/products/INVALID-ID
 * Response: 404 Not Found
 * {
 *   "statusCode": 404,
 *   "error": "PRODUCT_NOT_FOUND",
 *   "message": "Product with ID 'INVALID-ID' not found"
 * }
 */
public class ProductNotFoundException extends RuntimeException {

    private static final String ERROR_CODE = "PRODUCT_NOT_FOUND";
    private static final int HTTP_STATUS = 404;

    /**
     * Constructor with message
     *
     * @param message - Descriptive error message
     *
     * Example:
     * throw new ProductNotFoundException("Product with ID 'PROD-001' not found");
     */
    public ProductNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructor with message and cause
     *
     * @param message - Descriptive error message
     * @param cause - Root cause exception
     *
     * Example:
     * catch (DataAccessException ex) {
     *     throw new ProductNotFoundException("Product not found", ex);
     * }
     */
    public ProductNotFoundException(String message, Throwable cause) {
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
     * @return HTTP status code (404)
     */
    public int getHttpStatus() {
        return HTTP_STATUS;
    }

}
