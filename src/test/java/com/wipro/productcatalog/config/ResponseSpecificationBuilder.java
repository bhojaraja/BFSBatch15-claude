package com.wipro.productcatalog.config;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.ResponseSpecification;

/**
 * ResponseSpecificationBuilder - Build response specifications for common validations
 * Handles status codes, content type, and common response validations
 */
public class ResponseSpecificationBuilder {

    /**
     * Response spec for successful 200 OK response
     */
    public static ResponseSpecification buildResponseSpec200() {
        return new ResponseSpecBuilder()
            .expectStatusCode(200)
            .expectContentType(ContentType.JSON)
            .build();
    }

    /**
     * Response spec for successful 201 Created response
     */
    public static ResponseSpecification buildResponseSpec201() {
        return new ResponseSpecBuilder()
            .expectStatusCode(201)
            .expectContentType(ContentType.JSON)
            .build();
    }

    /**
     * Response spec for successful 204 No Content response
     */
    public static ResponseSpecification buildResponseSpec204() {
        return new ResponseSpecBuilder()
            .expectStatusCode(204)
            .build();
    }

    /**
     * Response spec for bad request 400 response
     */
    public static ResponseSpecification buildResponseSpec400() {
        return new ResponseSpecBuilder()
            .expectStatusCode(400)
            .expectContentType(ContentType.JSON)
            .build();
    }

    /**
     * Response spec for not found 404 response
     */
    public static ResponseSpecification buildResponseSpec404() {
        return new ResponseSpecBuilder()
            .expectStatusCode(404)
            .expectContentType(ContentType.JSON)
            .build();
    }

    /**
     * Response spec for conflict 409 response
     */
    public static ResponseSpecification buildResponseSpec409() {
        return new ResponseSpecBuilder()
            .expectStatusCode(409)
            .expectContentType(ContentType.JSON)
            .build();
    }

    /**
     * Response spec for server error 500 response
     */
    public static ResponseSpecification buildResponseSpec500() {
        return new ResponseSpecBuilder()
            .expectStatusCode(500)
            .expectContentType(ContentType.JSON)
            .build();
    }

    /**
     * Custom response spec for any status code
     */
    public static ResponseSpecification buildResponseSpec(int statusCode) {
        return new ResponseSpecBuilder()
            .expectStatusCode(statusCode)
            .expectContentType(ContentType.JSON)
            .build();
    }

    /**
     * Custom response spec for any status code without content type validation
     */
    public static ResponseSpecification buildResponseSpecNoContentType(int statusCode) {
        return new ResponseSpecBuilder()
            .expectStatusCode(statusCode)
            .build();
    }
}
