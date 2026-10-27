package com.wipro.productcatalog.utils;

import com.wipro.productcatalog.dto.request.CreateProductRequest;
import com.wipro.productcatalog.dto.request.UpdateProductRequest;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * TestDataBuilder - Build test data for API tests
 * Provides builders for creating test objects with valid/invalid data
 */
public class TestDataBuilder {

    /**
     * Build valid CreateProductRequest
     */
    public static CreateProductRequest buildValidCreateProductRequest() {
        return CreateProductRequest.builder()
            .id("PROD-" + System.currentTimeMillis())
            .name("Test Product " + System.currentTimeMillis())
            .description("This is a test product")
            .categoryId("ELEC-001")
            .price(BigDecimal.valueOf(999.99))
            .stockQuantity(50)
            .build();
    }

    /**
     * Build CreateProductRequest with custom values
     */
    public static CreateProductRequest buildCreateProductRequest(String id, String name, String categoryId,
                                                                   BigDecimal price, Integer stockQuantity) {
        return CreateProductRequest.builder()
            .id(id)
            .name(name)
            .description("Test product for API testing")
            .categoryId(categoryId)
            .price(price)
            .stockQuantity(stockQuantity)
            .build();
    }

    /**
     * Build CreateProductRequest with minimum required fields
     */
    public static CreateProductRequest buildMinimalCreateProductRequest() {
        return CreateProductRequest.builder()
            .id("PROD-MIN-" + System.currentTimeMillis())
            .name("Minimal Product")
            .categoryId("ELEC-001")
            .price(BigDecimal.valueOf(10.00))
            .stockQuantity(0)
            .build();
    }

    /**
     * Build CreateProductRequest with invalid price (zero)
     */
    public static CreateProductRequest buildInvalidPriceRequest() {
        return CreateProductRequest.builder()
            .id("PROD-INVALID-PRICE")
            .name("Invalid Price Product")
            .categoryId("ELEC-001")
            .price(BigDecimal.ZERO)
            .stockQuantity(50)
            .build();
    }

    /**
     * Build CreateProductRequest with invalid stock (negative)
     */
    public static CreateProductRequest buildInvalidStockRequest() {
        return CreateProductRequest.builder()
            .id("PROD-INVALID-STOCK")
            .name("Invalid Stock Product")
            .categoryId("ELEC-001")
            .price(BigDecimal.valueOf(100.00))
            .stockQuantity(-5)
            .build();
    }

    /**
     * Build CreateProductRequest with blank name
     */
    public static CreateProductRequest buildBlankNameRequest() {
        return CreateProductRequest.builder()
            .id("PROD-BLANK-NAME")
            .name("")
            .categoryId("ELEC-001")
            .price(BigDecimal.valueOf(100.00))
            .stockQuantity(50)
            .build();
    }

    /**
     * Build UpdateProductRequest
     */
    public static UpdateProductRequest buildValidUpdateProductRequest() {
        return UpdateProductRequest.builder()
            .name("Updated Product " + System.currentTimeMillis())
            .description("Updated description")
            .price(BigDecimal.valueOf(1299.99))
            .stockQuantity(75)
            .build();
    }

    /**
     * Build UpdateProductRequest with custom values
     */
    public static UpdateProductRequest buildUpdateProductRequest(String name, String description,
                                                                   BigDecimal price, Integer stockQuantity) {
        return UpdateProductRequest.builder()
            .name(name)
            .description(description)
            .price(price)
            .stockQuantity(stockQuantity)
            .build();
    }

    /**
     * Build UpdateProductRequest with only name change
     */
    public static UpdateProductRequest buildPartialUpdateRequest(String newName) {
        return UpdateProductRequest.builder()
            .name(newName)
            .build();
    }

    /**
     * Build UpdateProductRequest with invalid price
     */
    public static UpdateProductRequest buildInvalidUpdateRequest() {
        return UpdateProductRequest.builder()
            .price(BigDecimal.ZERO)
            .build();
    }

    /**
     * Build request body as HashMap
     */
    public static Map<String, Object> buildRequestBodyMap(String id, String name, String categoryId,
                                                            Double price, Integer stockQuantity) {
        Map<String, Object> body = new HashMap<>();
        body.put("id", id);
        body.put("name", name);
        body.put("categoryId", categoryId);
        body.put("price", price);
        body.put("stockQuantity", stockQuantity);
        return body;
    }

    /**
     * Build query parameters map for pagination
     */
    public static Map<String, Object> buildPaginationParams(int page, int size) {
        Map<String, Object> params = new HashMap<>();
        params.put("page", page);
        params.put("size", size);
        return params;
    }

    /**
     * Build query parameters map with sorting
     */
    public static Map<String, Object> buildPaginationWithSortParams(int page, int size, String sortBy, String direction) {
        Map<String, Object> params = new HashMap<>();
        params.put("page", page);
        params.put("size", size);
        params.put("sort", sortBy + "," + direction);
        return params;
    }

    /**
     * Get valid category IDs
     */
    public static String[] getValidCategoryIds() {
        return new String[]{"ELEC-001", "CLOTH-001", "HOME-001", "BOOK-001", "SPORT-001", "HEALTH-001"};
    }

    /**
     * Get invalid category ID
     */
    public static String getInvalidCategoryId() {
        return "INVALID-CAT-999";
    }

    /**
     * Get existing product IDs for testing
     */
    public static String[] getExistingProductIds() {
        return new String[]{"PROD-001", "PROD-002", "PROD-003"};
    }

    /**
     * Get non-existing product ID
     */
    public static String getNonExistingProductId() {
        return "PROD-999-NONEXISTENT";
    }
}
