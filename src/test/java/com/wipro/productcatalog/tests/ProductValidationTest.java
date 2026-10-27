package com.wipro.productcatalog.tests;

import com.wipro.productcatalog.base.BaseTest;
import com.wipro.productcatalog.config.ResponseSpecificationBuilder;
import com.wipro.productcatalog.dto.request.CreateProductRequest;
import com.wipro.productcatalog.utils.AssertionUtils;
import com.wipro.productcatalog.utils.TestDataBuilder;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.annotations.Test;

import java.math.BigDecimal;

/**
 * ProductValidationTest - API validation tests
 * Tests validation rules and error handling
 */
@Feature("Product Validation")
public class ProductValidationTest extends BaseTest {

    // ==================== VALIDATION TESTS ====================

    @Test
    @Story("Product Validation")
    @Description("Test creating product with invalid price (zero)")
    public void testCreateProductWithZeroPriceFails() {
        logger.info("Test: Creating product with invalid price (zero)");

        CreateProductRequest request = CreateProductRequest.builder()
            .id("PROD-ZERO-PRICE-" + System.currentTimeMillis())
            .name("Zero Price Product")
            .categoryId("ELEC-001")
            .price(BigDecimal.ZERO)
            .stockQuantity(50)
            .build();

        Response response = RestAssured
            .given()
            .spec(requestSpec)
            .body(request)
            .when()
            .post("/products")
            .then()
            .spec(ResponseSpecificationBuilder.buildResponseSpec400())
            .extract()
            .response();

        AssertionUtils.assertStatusCode(response, 400);
        AssertionUtils.assertResponseContainsField(response, "message");
        logger.info("✓ Zero price validation error returned");
    }

    @Test
    @Story("Product Validation")
    @Description("Test creating product with negative stock")
    public void testCreateProductWithNegativeStockFails() {
        logger.info("Test: Creating product with negative stock");

        CreateProductRequest request = CreateProductRequest.builder()
            .id("PROD-NEG-STOCK-" + System.currentTimeMillis())
            .name("Negative Stock Product")
            .categoryId("ELEC-001")
            .price(BigDecimal.valueOf(100.00))
            .stockQuantity(-5)
            .build();

        Response response = RestAssured
            .given()
            .spec(requestSpec)
            .body(request)
            .when()
            .post("/products")
            .then()
            .spec(ResponseSpecificationBuilder.buildResponseSpec400())
            .extract()
            .response();

        AssertionUtils.assertStatusCode(response, 400);
        logger.info("✓ Negative stock validation error returned");
    }

    @Test
    @Story("Product Validation")
    @Description("Test creating product with blank name")
    public void testCreateProductWithBlankNameFails() {
        logger.info("Test: Creating product with blank name");

        CreateProductRequest request = CreateProductRequest.builder()
            .id("PROD-BLANK-NAME-" + System.currentTimeMillis())
            .name("")
            .categoryId("ELEC-001")
            .price(BigDecimal.valueOf(100.00))
            .stockQuantity(50)
            .build();

        Response response = RestAssured
            .given()
            .spec(requestSpec)
            .body(request)
            .when()
            .post("/products")
            .then()
            .spec(ResponseSpecificationBuilder.buildResponseSpec400())
            .extract()
            .response();

        AssertionUtils.assertStatusCode(response, 400);
        logger.info("✓ Blank name validation error returned");
    }

    @Test
    @Story("Product Validation")
    @Description("Test creating product with invalid category")
    public void testCreateProductWithInvalidCategoryFails() {
        logger.info("Test: Creating product with invalid category");

        CreateProductRequest request = CreateProductRequest.builder()
            .id("PROD-INVALID-CAT-" + System.currentTimeMillis())
            .name("Invalid Category Product")
            .categoryId(TestDataBuilder.getInvalidCategoryId())
            .price(BigDecimal.valueOf(100.00))
            .stockQuantity(50)
            .build();

        Response response = RestAssured
            .given()
            .spec(requestSpec)
            .body(request)
            .when()
            .post("/products")
            .then()
            .spec(ResponseSpecificationBuilder.buildResponseSpec404())
            .extract()
            .response();

        AssertionUtils.assertStatusCode(response, 404);
        logger.info("✓ Invalid category validation error returned");
    }

    @Test
    @Story("Product Validation")
    @Description("Test creating product with missing required fields")
    public void testCreateProductWithMissingFieldsFails() {
        logger.info("Test: Creating product with missing required fields");

        String invalidJson = "{\"name\": \"Incomplete Product\"}"; // Missing required fields

        Response response = RestAssured
            .given()
            .spec(requestSpec)
            .body(invalidJson)
            .when()
            .post("/products")
            .then()
            .spec(ResponseSpecificationBuilder.buildResponseSpec400())
            .extract()
            .response();

        AssertionUtils.assertStatusCode(response, 400);
        logger.info("✓ Missing fields validation error returned");
    }

    // ==================== RESPONSE FORMAT TESTS ====================

    @Test
    @Story("Response Format")
    @Description("Test successful product creation returns proper response structure")
    public void testCreateProductResponseStructure() {
        logger.info("Test: Verifying response structure for product creation");

        CreateProductRequest request = TestDataBuilder.buildValidCreateProductRequest();

        Response response = RestAssured
            .given()
            .spec(requestSpec)
            .body(request)
            .when()
            .post("/products")
            .then()
            .spec(ResponseSpecificationBuilder.buildResponseSpec201())
            .extract()
            .response();

        // Verify response structure
        AssertionUtils.assertResponseContainsField(response, "id");
        AssertionUtils.assertResponseContainsField(response, "name");
        AssertionUtils.assertResponseContainsField(response, "description");
        AssertionUtils.assertResponseContainsField(response, "category");
        AssertionUtils.assertResponseContainsField(response, "price");
        AssertionUtils.assertResponseContainsField(response, "stockQuantity");

        logger.info("✓ Response structure is valid");
    }

    @Test
    @Story("Response Format")
    @Description("Test error response contains required fields")
    public void testErrorResponseStructure() {
        logger.info("Test: Verifying error response structure");

        String nonExistentId = TestDataBuilder.getNonExistingProductId();

        Response response = RestAssured
            .given()
            .spec(requestSpec)
            .pathParam("id", nonExistentId)
            .when()
            .get("/products/{id}")
            .then()
            .spec(ResponseSpecificationBuilder.buildResponseSpec404())
            .extract()
            .response();

        // Verify error response structure
        AssertionUtils.assertResponseContainsField(response, "status");
        AssertionUtils.assertResponseContainsField(response, "message");
        AssertionUtils.assertResponseContainsField(response, "error");

        logger.info("✓ Error response structure is valid");
    }

    // ==================== RESPONSE TIME TESTS ====================

    @Test
    @Story("Performance")
    @Description("Test API response time is within acceptable limits")
    public void testGetProductResponseTimeAcceptable() {
        logger.info("Test: Verifying response time for get product");

        String existingProductId = TestDataBuilder.getExistingProductIds()[0];

        Response response = RestAssured
            .given()
            .spec(requestSpec)
            .pathParam("id", existingProductId)
            .when()
            .get("/products/{id}")
            .then()
            .spec(ResponseSpecificationBuilder.buildResponseSpec200())
            .extract()
            .response();

        // Verify response time is less than 5 seconds
        AssertionUtils.assertResponseTime(response, 5000);
        logger.info("✓ Response time is acceptable: {} ms", response.getTime());
    }

    @Test
    @Story("Performance")
    @Description("Test API list response time is within acceptable limits")
    public void testGetAllProductsResponseTimeAcceptable() {
        logger.info("Test: Verifying response time for get all products");

        Response response = RestAssured
            .given()
            .spec(requestSpec)
            .queryParam("page", 0)
            .queryParam("size", 10)
            .when()
            .get("/products")
            .then()
            .spec(ResponseSpecificationBuilder.buildResponseSpec200())
            .extract()
            .response();

        // Verify response time is less than 10 seconds
        AssertionUtils.assertResponseTime(response, 10000);
        logger.info("✓ List response time is acceptable: {} ms", response.getTime());
    }

    // ==================== DUPLICATE NAME VALIDATION ====================

    @Test
    @Story("Product Validation")
    @Description("Test creating product with duplicate name fails")
    public void testCreateProductWithDuplicateNameFails() {
        logger.info("Test: Creating product with duplicate name");

        String uniqueName = "Unique Name " + System.currentTimeMillis();

        // Create first product
        CreateProductRequest request1 = CreateProductRequest.builder()
            .id("PROD-DUP-NAME-1-" + System.currentTimeMillis())
            .name(uniqueName)
            .categoryId("ELEC-001")
            .price(BigDecimal.valueOf(100.00))
            .stockQuantity(50)
            .build();

        RestAssured
            .given()
            .spec(requestSpec)
            .body(request1)
            .when()
            .post("/products");

        // Try to create second product with same name
        CreateProductRequest request2 = CreateProductRequest.builder()
            .id("PROD-DUP-NAME-2-" + System.currentTimeMillis())
            .name(uniqueName)
            .categoryId("ELEC-001")
            .price(BigDecimal.valueOf(200.00))
            .stockQuantity(30)
            .build();

        Response response = RestAssured
            .given()
            .spec(requestSpec)
            .body(request2)
            .when()
            .post("/products")
            .then()
            .spec(ResponseSpecificationBuilder.buildResponseSpec409())
            .extract()
            .response();

        AssertionUtils.assertStatusCode(response, 409);
        logger.info("✓ Duplicate name validation error returned");
    }
}
