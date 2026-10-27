package com.wipro.productcatalog.config;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RequestSpecificationBuilder - Build request specifications with common configurations
 * Handles base URL, timeout, content type, logging, etc.
 */
public class RequestSpecificationBuilder {
    private static final Logger logger = LoggerFactory.getLogger(RequestSpecificationBuilder.class);

    /**
     * Build request specification with default configuration
     */
    public static RequestSpecification buildRequestSpec() {
        return new io.restassured.builder.RequestSpecBuilder()
            .setBaseUri(EnvironmentConfig.getBaseUrl())
            .setBasePath(EnvironmentConfig.getBasePath())
            .setPort(EnvironmentConfig.getPort())
            .setContentType(ContentType.JSON)
            .setAccept(ContentType.JSON)
            .addFilter(new RequestLoggingFilter())
            .addFilter(new ResponseLoggingFilter())
            .setSocketTimeout((int) EnvironmentConfig.getTimeout())
            .build();
    }

    /**
     * Build request specification with authentication
     */
    public static RequestSpecification buildRequestSpecWithAuth() {
        RequestSpecBuilder builder = new io.restassured.builder.RequestSpecBuilder()
            .setBaseUri(EnvironmentConfig.getBaseUrl())
            .setBasePath(EnvironmentConfig.getBasePath())
            .setPort(EnvironmentConfig.getPort())
            .setContentType(ContentType.JSON)
            .setAccept(ContentType.JSON)
            .addFilter(new RequestLoggingFilter())
            .addFilter(new ResponseLoggingFilter())
            .setSocketTimeout((int) EnvironmentConfig.getTimeout());

        // Add authentication if configured
        String authType = EnvironmentConfig.getAuthType();
        if ("bearer".equalsIgnoreCase(authType)) {
            String apiKey = EnvironmentConfig.getApiKey();
            builder.addHeader("Authorization", "Bearer " + apiKey);
            logger.info("Added Bearer token authentication");
        } else if ("apikey".equalsIgnoreCase(authType)) {
            String apiKey = EnvironmentConfig.getApiKey();
            builder.addHeader("X-API-Key", apiKey);
            logger.info("Added API Key authentication");
        }

        return builder.build();
    }

    /**
     * Build request specification with custom headers
     */
    public static RequestSpecification buildRequestSpecWithHeaders(java.util.Map<String, String> headers) {
        RequestSpecBuilder builder = new io.restassured.builder.RequestSpecBuilder()
            .setBaseUri(EnvironmentConfig.getBaseUrl())
            .setBasePath(EnvironmentConfig.getBasePath())
            .setPort(EnvironmentConfig.getPort())
            .setContentType(ContentType.JSON)
            .setAccept(ContentType.JSON)
            .addFilter(new RequestLoggingFilter())
            .addFilter(new ResponseLoggingFilter())
            .setSocketTimeout((int) EnvironmentConfig.getTimeout());

        // Add custom headers
        headers.forEach(builder::addHeader);

        return builder.build();
    }

    /**
     * Build request specification without logging
     */
    public static RequestSpecification buildRequestSpecNoLogging() {
        return new io.restassured.builder.RequestSpecBuilder()
            .setBaseUri(EnvironmentConfig.getBaseUrl())
            .setBasePath(EnvironmentConfig.getBasePath())
            .setPort(EnvironmentConfig.getPort())
            .setContentType(ContentType.JSON)
            .setAccept(ContentType.JSON)
            .setSocketTimeout((int) EnvironmentConfig.getTimeout())
            .build();
    }
}
