package com.wipro.productcatalog.utils;

import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * AssertionUtils - Custom assertions for API testing
 * Provides reusable assertions for common API response validations
 */
public class AssertionUtils {
    private static final Logger logger = LoggerFactory.getLogger(AssertionUtils.class);

    /**
     * Assert response status code
     */
    public static void assertStatusCode(Response response, int expectedStatusCode) {
        int actualStatusCode = response.getStatusCode();
        org.testng.Assert.assertEquals(actualStatusCode, expectedStatusCode,
            "Status code mismatch. Expected: " + expectedStatusCode + ", Actual: " + actualStatusCode);
        logger.info("✓ Status code assertion passed: {}", expectedStatusCode);
    }

    /**
     * Assert response contains field
     */
    public static void assertResponseContainsField(Response response, String fieldPath) {
        try {
            Object value = response.jsonPath().get(fieldPath);
            org.testng.Assert.assertNotNull(value, "Field '" + fieldPath + "' is missing in response");
            logger.info("✓ Field '{}' exists in response with value: {}", fieldPath, value);
        } catch (Exception e) {
            org.testng.Assert.fail("Field '" + fieldPath + "' not found in response: " + e.getMessage());
        }
    }

    /**
     * Assert field value matches expected
     */
    public static void assertFieldValue(Response response, String fieldPath, Object expectedValue) {
        Object actualValue = response.jsonPath().get(fieldPath);
        org.testng.Assert.assertEquals(actualValue, expectedValue,
            "Field value mismatch. Field: " + fieldPath + ", Expected: " + expectedValue + ", Actual: " + actualValue);
        logger.info("✓ Field '{}' value assertion passed: {}", fieldPath, expectedValue);
    }

    /**
     * Assert field value is not null
     */
    public static void assertFieldNotNull(Response response, String fieldPath) {
        Object value = response.jsonPath().get(fieldPath);
        org.testng.Assert.assertNotNull(value, "Field '" + fieldPath + "' is null");
        logger.info("✓ Field '{}' is not null: {}", fieldPath, value);
    }

    /**
     * Assert field value is not empty string
     */
    public static void assertFieldNotEmpty(Response response, String fieldPath) {
        String value = response.jsonPath().get(fieldPath);
        org.testng.Assert.assertFalse(value == null || value.isEmpty(),
            "Field '" + fieldPath + "' is empty or null");
        logger.info("✓ Field '{}' is not empty: {}", fieldPath, value);
    }

    /**
     * Assert list contains number of elements
     */
    public static void assertListSize(Response response, String listPath, int expectedSize) {
        List<?> list = response.jsonPath().getList(listPath);
        org.testng.Assert.assertEquals(list.size(), expectedSize,
            "List size mismatch. Path: " + listPath + ", Expected: " + expectedSize + ", Actual: " + list.size());
        logger.info("✓ List '{}' size assertion passed: {}", listPath, expectedSize);
    }

    /**
     * Assert list is not empty
     */
    public static void assertListNotEmpty(Response response, String listPath) {
        List<?> list = response.jsonPath().getList(listPath);
        org.testng.Assert.assertFalse(list.isEmpty(), "List '" + listPath + "' is empty");
        logger.info("✓ List '{}' is not empty, size: {}", listPath, list.size());
    }

    /**
     * Assert response contains expected error message
     */
    public static void assertErrorMessage(Response response, String expectedMessage) {
        String actualMessage = response.jsonPath().get("message");
        org.testng.Assert.assertTrue(actualMessage.contains(expectedMessage),
            "Error message mismatch. Expected to contain: '" + expectedMessage + "', Actual: '" + actualMessage + "'");
        logger.info("✓ Error message assertion passed: {}", expectedMessage);
    }

    /**
     * Assert response body is not empty
     */
    public static void assertResponseNotEmpty(Response response) {
        String body = response.getBody().asString();
        org.testng.Assert.assertFalse(body.isEmpty(), "Response body is empty");
        logger.info("✓ Response body is not empty");
    }

    /**
     * Assert response header exists
     */
    public static void assertResponseHeaderExists(Response response, String headerName) {
        String headerValue = response.getHeader(headerName);
        org.testng.Assert.assertNotNull(headerValue, "Header '" + headerName + "' not found in response");
        logger.info("✓ Response header '{}' exists with value: {}", headerName, headerValue);
    }

    /**
     * Assert response header value
     */
    public static void assertResponseHeaderValue(Response response, String headerName, String expectedValue) {
        String actualValue = response.getHeader(headerName);
        org.testng.Assert.assertEquals(actualValue, expectedValue,
            "Header value mismatch. Header: " + headerName + ", Expected: " + expectedValue + ", Actual: " + actualValue);
        logger.info("✓ Response header '{}' value assertion passed: {}", headerName, expectedValue);
    }

    /**
     * Assert response time is less than threshold
     */
    public static void assertResponseTime(Response response, long maxTimeMs) {
        long responseTime = response.getTime();
        org.testng.Assert.assertTrue(responseTime <= maxTimeMs,
            "Response time exceeds threshold. Max: " + maxTimeMs + "ms, Actual: " + responseTime + "ms");
        logger.info("✓ Response time assertion passed: {} ms (max: {} ms)", responseTime, maxTimeMs);
    }

    /**
     * Generic field assertion using Hamcrest matcher
     */
    public static void assertField(Response response, String fieldPath, org.hamcrest.Matcher<?> matcher) {
        response.then().body(fieldPath, matcher);
        logger.info("✓ Field '{}' assertion passed with matcher", fieldPath);
    }
}
