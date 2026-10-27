package com.wipro.productcatalog.tests;

import com.wipro.productcatalog.base.BaseTest;
import com.wipro.productcatalog.config.ResponseSpecificationBuilder;
import com.wipro.productcatalog.dto.request.CreateProductRequest;
import com.wipro.productcatalog.dto.request.UpdateProductRequest;
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
 * ProductAPITest - Comprehensive API tests for Product endpoints
 * Tests CRUD operations for products
 */
@Feature("Product Management API")
public class ProductAPITest extends BaseTest {

    private static String createdProductId;

    // ==================== CREATE PRODUCT TESTS ====================

    @Test(priority = 1)
    @Story("Create Product")
    @Description("Test creating a new product with valid data")
    public void testCreateProductSuccess() {
        logger.info("Test: Creating product with valid data");

        CreateProductRequest request = TestDataBuilder.buildValidCreateProductRequest();
        createdProductId = request.getId();

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

        AssertionUtils.assertStatusCode(response, 201);
        AssertionUtils.assertResponseContainsField(response, "id");
        AssertionUtils.assertFieldValue(response, "id", createdProductId);
        AssertionUtils.assertFieldValue(response, "name", request.getName());
        AssertionUtils.assertFieldValue(response, "price", request.getPrice().doubleValue());
        AssertionUtils.assertResponseTime(response, 5000);

        logger.info("✓ Product created successfully with ID: {}", createdProductId);
    }

    @Test(priority = 2)
    @Story("Create Product")
    @Description("Test creating product with duplicate ID fails")
    public void testCreateProductDuplicateIdFails() {
        logger.info("Test: Creating product with duplicate ID");

        CreateProductRequest request = TestDataBuilder.buildCreateProductRequest(
            "PROD-DUP-" + System.currentTimeMillis(),
            "Product 1",
            "ELEC-001",
            BigDecimal.valueOf(100.00),
            50
        );

        // First creation
        RestAssured
            .given()
            .spec(requestSpec)
            .body(request)
            .when()
            .post("/products");

        // Second creation with same ID should fail
        Response response = RestAssured
            .given()
            .spec(requestSpec)
            .body(request)
            .when()
            .post("/products")
            .then()
            .spec(ResponseSpecificationBuilder.buildResponseSpec409())
            .extract()
            .response();

        AssertionUtils.assertStatusCode(response, 409);
        AssertionUtils.assertResponseContainsField(response, "message");
        logger.info("✓ Duplicate product creation rejected with 409 Conflict");
    }

    // ==================== GET PRODUCT TESTS ====================

    @Test(priority = 3, dependsOnMethods = {"testCreateProductSuccess"})
    @Story("Get Product")
    @Description("Test retrieving a product by ID")
    public void testGetProductByIdSuccess() {
        logger.info("Test: Retrieving product by ID: {}", createdProductId);

        Response response = RestAssured
            .given()
            .spec(requestSpec)
            .pathParam("id", createdProductId)
            .when()
            .get("/products/{id}")
            .then()
            .spec(ResponseSpecificationBuilder.buildResponseSpec200())
            .extract()
            .response();

        AssertionUtils.assertStatusCode(response, 200);
        AssertionUtils.assertFieldValue(response, "id", createdProductId);
        AssertionUtils.assertFieldNotNull(response, "name");
        AssertionUtils.assertFieldNotNull(response, "price");

        logger.info("✓ Product retrieved successfully");
    }

    @Test(priority = 4)
    @Story("Get Product")
    @Description("Test retrieving non-existent product returns 404")
    public void testGetNonExistentProductFails() {
        logger.info("Test: Retrieving non-existent product");

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

        AssertionUtils.assertStatusCode(response, 404);
        AssertionUtils.assertResponseContainsField(response, "message");
        logger.info("✓ Non-existent product returns 404");
    }

    @Test(priority = 5)
    @Story("Get Products")
    @Description("Test retrieving all products with pagination")
    public void testGetAllProductsWithPagination() {
        logger.info("Test: Retrieving all products with pagination");

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

        AssertionUtils.assertStatusCode(response, 200);
        AssertionUtils.assertResponseContainsField(response, "content");
        AssertionUtils.assertResponseContainsField(response, "pageable");
        logger.info("✓ Products retrieved with pagination");
    }

    // ==================== UPDATE PRODUCT TESTS ====================

    @Test(priority = 6, dependsOnMethods = {"testCreateProductSuccess"})
    @Story("Update Product")
    @Description("Test updating an existing product")
    public void testUpdateProductSuccess() {
        logger.info("Test: Updating product: {}", createdProductId);

        UpdateProductRequest request = TestDataBuilder.buildValidUpdateProductRequest();

        Response response = RestAssured
            .given()
            .spec(requestSpec)
            .pathParam("id", createdProductId)
            .body(request)
            .when()
            .put("/products/{id}")
            .then()
            .spec(ResponseSpecificationBuilder.buildResponseSpec200())
            .extract()
            .response();

        AssertionUtils.assertStatusCode(response, 200);
        AssertionUtils.assertFieldValue(response, "id", createdProductId);
        AssertionUtils.assertFieldValue(response, "name", request.getName());
        logger.info("✓ Product updated successfully");
    }

    @Test(priority = 7)
    @Story("Update Product")
    @Description("Test updating non-existent product returns 404")
    public void testUpdateNonExistentProductFails() {
        logger.info("Test: Updating non-existent product");

        String nonExistentId = TestDataBuilder.getNonExistingProductId();
        UpdateProductRequest request = TestDataBuilder.buildValidUpdateProductRequest();

        Response response = RestAssured
            .given()
            .spec(requestSpec)
            .pathParam("id", nonExistentId)
            .body(request)
            .when()
            .put("/products/{id}")
            .then()
            .spec(ResponseSpecificationBuilder.buildResponseSpec404())
            .extract()
            .response();

        AssertionUtils.assertStatusCode(response, 404);
        logger.info("✓ Non-existent product update returns 404");
    }

    // ==================== DELETE PRODUCT TESTS ====================

    @Test(priority = 8, dependsOnMethods = {"testUpdateProductSuccess"})
    @Story("Delete Product")
    @Description("Test deleting an existing product")
    public void testDeleteProductSuccess() {
        logger.info("Test: Deleting product: {}", createdProductId);

        Response response = RestAssured
            .given()
            .spec(requestSpec)
            .pathParam("id", createdProductId)
            .when()
            .delete("/products/{id}")
            .then()
            .spec(ResponseSpecificationBuilder.buildResponseSpec204())
            .extract()
            .response();

        AssertionUtils.assertStatusCode(response, 204);
        logger.info("✓ Product deleted successfully");
    }

    @Test(priority = 9)
    @Story("Delete Product")
    @Description("Test deleting non-existent product returns 404")
    public void testDeleteNonExistentProductFails() {
        logger.info("Test: Deleting non-existent product");

        String nonExistentId = TestDataBuilder.getNonExistingProductId();

        Response response = RestAssured
            .given()
            .spec(requestSpec)
            .pathParam("id", nonExistentId)
            .when()
            .delete("/products/{id}")
            .then()
            .spec(ResponseSpecificationBuilder.buildResponseSpec404())
            .extract()
            .response();

        AssertionUtils.assertStatusCode(response, 404);
        logger.info("✓ Non-existent product deletion returns 404");
    }

    // ==================== GET BY CATEGORY TESTS ====================

    @Test(priority = 10)
    @Story("Get Products by Category")
    @Description("Test retrieving products by category")
    public void testGetProductsByCategory() {
        logger.info("Test: Retrieving products by category");

        String categoryId = "ELEC-001";

        Response response = RestAssured
            .given()
            .spec(requestSpec)
            .pathParam("categoryId", categoryId)
            .when()
            .get("/products/category/{categoryId}")
            .then()
            .spec(ResponseSpecificationBuilder.buildResponseSpec200())
            .extract()
            .response();

        AssertionUtils.assertStatusCode(response, 200);
        logger.info("✓ Products retrieved by category");
    }

    @Test(priority = 11)
    @Story("Get Products by Category")
    @Description("Test retrieving products by invalid category returns 404")
    public void testGetProductsByInvalidCategoryFails() {
        logger.info("Test: Retrieving products by invalid category");

        String invalidCategoryId = TestDataBuilder.getInvalidCategoryId();

        Response response = RestAssured
            .given()
            .spec(requestSpec)
            .pathParam("categoryId", invalidCategoryId)
            .when()
            .get("/products/category/{categoryId}")
            .then()
            .spec(ResponseSpecificationBuilder.buildResponseSpec404())
            .extract()
            .response();

        AssertionUtils.assertStatusCode(response, 404);
        logger.info("✓ Invalid category returns 404");
    }
}
