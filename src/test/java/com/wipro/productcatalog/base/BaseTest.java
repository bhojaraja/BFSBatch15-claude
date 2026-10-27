package com.wipro.productcatalog.base;

import com.wipro.productcatalog.config.RequestSpecificationBuilder;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import io.qameta.allure.Allure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.lang.reflect.Method;

/**
 * BaseTest - Base class for all API test classes
 * Provides common setup, teardown, and utility methods
 */
public class BaseTest {
    protected static final Logger logger = LoggerFactory.getLogger(BaseTest.class);
    protected RequestSpecification requestSpec;

    /**
     * Setup method - runs before each test
     * Initializes request specification and sets up logging
     */
    @BeforeMethod(alwaysRun = true)
    public void setUp(Method method) {
        logger.info("========== Starting Test: {} ==========", method.getName());

        // Initialize request specification
        requestSpec = RequestSpecificationBuilder.buildRequestSpec();
        RestAssured.requestSpecification = requestSpec;

        // Add test name to Allure report
        Allure.feature("API Testing");
        Allure.story(method.getName());

        logger.info("Request specification initialized");
    }

    /**
     * Teardown method - runs after each test
     * Logs test result and cleanup
     */
    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String status = result.isSuccess() ? "PASSED" : "FAILED";

        logger.info("========== Test Result: {} - {} ==========", testName, status);

        if (!result.isSuccess()) {
            logger.error("Test failed with exception: {}", result.getThrowable());
            Allure.addAttachment("Error", result.getThrowable().getMessage());
        }

        // Clear request specification
        RestAssured.requestSpecification = null;
    }

    /**
     * Get request specification with authentication
     */
    protected RequestSpecification getAuthenticatedRequestSpec() {
        return RequestSpecificationBuilder.buildRequestSpecWithAuth();
    }

    /**
     * Add request body to request specification
     */
    protected RequestSpecification withRequestBody(Object body) {
        logger.debug("Request body: {}", body);
        return requestSpec.body(body);
    }

    /**
     * Add query parameter to request
     */
    protected RequestSpecification withQueryParam(String paramName, Object paramValue) {
        logger.debug("Query parameter - {}: {}", paramName, paramValue);
        return requestSpec.queryParam(paramName, paramValue);
    }

    /**
     * Add path parameter to request
     */
    protected RequestSpecification withPathParam(String paramName, Object paramValue) {
        logger.debug("Path parameter - {}: {}", paramName, paramValue);
        return requestSpec.pathParam(paramName, paramValue);
    }

    /**
     * Add header to request
     */
    protected RequestSpecification withHeader(String headerName, String headerValue) {
        logger.debug("Header - {}: {}", headerName, headerValue);
        return requestSpec.header(headerName, headerValue);
    }

    /**
     * Log request details
     */
    protected void logRequestDetails(String method, String endpoint) {
        logger.info("Request Method: {}", method);
        logger.info("Request Endpoint: {}", endpoint);
    }

    /**
     * Log response details
     */
    protected void logResponseDetails(int statusCode, long responseTime) {
        logger.info("Response Status Code: {}", statusCode);
        logger.info("Response Time: {} ms", responseTime);
    }
}
