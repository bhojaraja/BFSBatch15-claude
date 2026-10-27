# REST Assured API Automation Framework
## Product Catalog Management System

A production-ready API automation framework built with REST Assured, TestNG, and Allure Reports for comprehensive REST API testing.

---

## 📋 Table of Contents

1. [Technology Stack](#technology-stack)
2. [Framework Architecture](#framework-architecture)
3. [Folder Structure](#folder-structure)
4. [Setup & Configuration](#setup--configuration)
5. [Running Tests](#running-tests)
6. [Viewing Reports](#viewing-reports)
7. [Framework Components](#framework-components)
8. [Best Practices](#best-practices)

---

## 🔧 Technology Stack

- **Language**: Java 21
- **Build Tool**: Maven 3.x
- **Testing Framework**: TestNG 7.10.2
- **API Testing**: REST Assured 5.4.0
- **Reporting**: Allure Reports 2.25.0
- **Logging**: SLF4J + Logback
- **Configuration**: Apache Commons Configuration

---

## 🏗️ Framework Architecture

```
┌─────────────────────────────────────────┐
│           Test Execution Layer          │
│      (ProductAPITest, ValidationTest)   │
└─────────────────────┬───────────────────┘
                      │
┌─────────────────────▼───────────────────┐
│           Base Test Class               │
│    (Setup, Teardown, Utilities)         │
└─────────────────────┬───────────────────┘
                      │
┌─────────────────────▼───────────────────┐
│         Request/Response Specs          │
│  (RequestSpecBuilder, ResponseSpecBuilder)
└─────────────────────┬───────────────────┘
                      │
┌─────────────────────▼───────────────────┐
│         Utility & Helper Classes        │
│ (TestDataBuilder, AssertionUtils)       │
└─────────────────────┬───────────────────┘
                      │
┌─────────────────────▼───────────────────┐
│         Environment Configuration       │
│    (EnvironmentConfig, Properties)      │
└─────────────────────┬───────────────────┘
                      │
┌─────────────────────▼───────────────────┐
│           REST API Endpoints            │
│      (Product Catalog Service)          │
└─────────────────────────────────────────┘
```

---

## 📁 Folder Structure

```
src/test/
├── java/com/wipro/productcatalog/
│   ├── base/
│   │   └── BaseTest.java                 # Base class for all tests
│   ├── config/
│   │   ├── EnvironmentConfig.java        # Environment configuration
│   │   ├── RequestSpecificationBuilder.java
│   │   └── ResponseSpecificationBuilder.java
│   ├── tests/
│   │   ├── ProductAPITest.java           # CRUD API tests
│   │   └── ProductValidationTest.java    # Validation tests
│   ├── utils/
│   │   ├── TestDataBuilder.java          # Test data generation
│   │   └── AssertionUtils.java           # Custom assertions
│   └── dto/                              # (Shared with main)
│       └── request/
│           ├── CreateProductRequest.java
│           └── UpdateProductRequest.java
└── resources/
    ├── config/
    │   ├── config-dev.properties         # Dev configuration
    │   ├── config-staging.properties     # Staging configuration
    │   └── config-prod.properties        # Prod configuration
    ├── logback-test.xml                  # Logging configuration
    └── testng.xml                        # TestNG configuration
```

---

## ⚙️ Setup & Configuration

### 1. Prerequisites

```bash
# Java 21 or higher
java -version

# Maven 3.x
mvn -version
```

### 2. Dependencies

All dependencies are configured in `pom.xml`. Key dependencies:

```xml
<!-- REST Assured -->
<dependency>
    <groupId>io.rest-assured</groupId>
    <artifactId>rest-assured</artifactId>
    <version>5.4.0</version>
</dependency>

<!-- TestNG -->
<dependency>
    <groupId>org.testng</groupId>
    <artifactId>testng</artifactId>
    <version>7.10.2</version>
</dependency>

<!-- Allure Reports -->
<dependency>
    <groupId>io.qameta.allure</groupId>
    <artifactId>allure-testng</artifactId>
    <version>2.25.0</version>
</dependency>
```

### 3. Environment Configuration

Configure environment-specific settings in `src/test/resources/config/config-{env}.properties`:

```properties
# Development Configuration (config-dev.properties)
api.baseUrl=http://localhost
api.basePath=/api/v1
api.port=9090
api.timeout=5000
api.connectionTimeout=3000
api.authType=none
```

**Supported Environments**:
- `dev` (Default) - http://localhost:9090/api/v1
- `staging` - https://staging-api.productcatalog.com
- `prod` - https://api.productcatalog.com

---

## 🧪 Running Tests

### Run All Tests

```bash
mvn clean test
```

### Run Tests for Specific Environment

```bash
# Development (default)
mvn clean test

# Staging
mvn clean test -Denv=staging

# Production
mvn clean test -Denv=prod
```

### Run Specific Test Class

```bash
mvn clean test -Dtest=ProductAPITest
mvn clean test -Dtest=ProductValidationTest
```

### Run Specific Test Method

```bash
mvn clean test -Dtest=ProductAPITest#testCreateProductSuccess
```

### Run with Multiple Threads

```bash
mvn clean test -DthreadCount=5
```

### Generate Allure Report After Tests

```bash
mvn allure:report
```

---

## 📊 Viewing Reports

### Allure Report

Generate and view Allure report:

```bash
# Generate Allure report
mvn allure:report

# Open in browser
mvn allure:serve

# View results directory
ls -la target/allure-results
```

Report Location: `target/site/allure-report/index.html`

### Test Logs

```bash
# View test execution logs
tail -f target/logs/test-execution.log

# View all logs
ls -la target/logs/
```

---

## 🔌 Framework Components

### BaseTest Class
- Initializes request specification before each test
- Handles setup and teardown
- Provides utility methods for common operations
- Integrates with Allure for reporting

### RequestSpecificationBuilder
- Builds request specifications with default configurations
- Handles authentication (Bearer token, API Key)
- Configures timeout, content type, headers
- Enables request/response logging

### ResponseSpecificationBuilder
- Provides pre-built response specs for common HTTP status codes
- 200 OK, 201 Created, 204 No Content
- 400 Bad Request, 404 Not Found, 409 Conflict, 500 Server Error
- Customizable response validation

### TestDataBuilder
- Generates valid test data
- Creates invalid data for negative testing
- Provides test data for different scenarios
- Maintains test data consistency

### AssertionUtils
- Custom assertions for API testing
- Status code validation
- Field existence and value assertions
- Response time assertions
- Error message validation
- List size assertions

### EnvironmentConfig
- Centralized configuration management
- Supports multiple environments
- Dynamic property loading
- Authentication configuration

---

## ✅ Test Coverage

### ProductAPITest (11 tests)

1. **testCreateProductSuccess** - Create product with valid data
2. **testCreateProductDuplicateIdFails** - Duplicate ID validation
3. **testGetProductByIdSuccess** - Retrieve existing product
4. **testGetNonExistentProductFails** - 404 handling for non-existent product
5. **testGetAllProductsWithPagination** - List products with pagination
6. **testUpdateProductSuccess** - Update existing product
7. **testUpdateNonExistentProductFails** - Update non-existent product
8. **testDeleteProductSuccess** - Delete existing product
9. **testDeleteNonExistentProductFails** - Delete non-existent product
10. **testGetProductsByCategory** - Filter products by category
11. **testGetProductsByInvalidCategoryFails** - Invalid category handling

### ProductValidationTest (12 tests)

1. **testCreateProductWithZeroPriceFails** - Price validation
2. **testCreateProductWithNegativeStockFails** - Stock validation
3. **testCreateProductWithBlankNameFails** - Name validation
4. **testCreateProductWithInvalidCategoryFails** - Category validation
5. **testCreateProductWithMissingFieldsFails** - Required fields validation
6. **testCreateProductResponseStructure** - Response structure validation
7. **testErrorResponseStructure** - Error response format
8. **testGetProductResponseTimeAcceptable** - Performance: Single product
9. **testGetAllProductsResponseTimeAcceptable** - Performance: List products
10. **testCreateProductWithDuplicateNameFails** - Duplicate name validation

---

## 🎯 Best Practices Implemented

### 1. **Test Independence**
- Tests can run in any order
- No dependencies between tests (except noted ones)
- Fresh setup for each test

### 2. **Comprehensive Logging**
- All requests and responses logged
- Debug-level logging for test execution
- Separate log files for each run

### 3. **Error Handling**
- Graceful failure handling
- Detailed error messages
- Request/response details captured on failure

### 4. **Maintainability**
- Reusable test data builders
- Common assertion utilities
- Centralized configuration

### 5. **Performance Testing**
- Response time assertions
- Timeout configurations
- Performance benchmarks

### 6. **Security**
- Environment-specific credentials
- Secure configuration management
- No hardcoded sensitive data

### 7. **Reporting**
- Allure integration for detailed reports
- Test categorization by feature/story
- Screenshots and logs attached on failure

### 8. **Scalability**
- Parameterized tests for multiple data sets
- Parallel test execution support
- Environment-specific configurations

---

## 🔄 CI/CD Integration

### GitHub Actions

```yaml
name: API Tests
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - uses: actions/setup-java@v2
        with:
          java-version: 21
      - run: mvn clean test -Denv=staging
      - run: mvn allure:report
```

### Jenkins

```groovy
pipeline {
    stages {
        stage('Test') {
            steps {
                sh 'mvn clean test -Denv=staging'
            }
        }
        stage('Report') {
            steps {
                sh 'mvn allure:report'
                publishAllure([
                    includeProperties: false,
                    jdk: '',
                    results: [[path: 'target/allure-results']]
                ])
            }
        }
    }
}
```

---

## 🚀 Advanced Usage

### Custom Assertions

```java
AssertionUtils.assertFieldValue(response, "id", expectedId);
AssertionUtils.assertListSize(response, "content", 10);
AssertionUtils.assertResponseTime(response, 5000);
```

### Test Data Generation

```java
CreateProductRequest request = TestDataBuilder.buildValidCreateProductRequest();
UpdateProductRequest updateRequest = TestDataBuilder.buildValidUpdateProductRequest();
```

### Environment-Specific Tests

```bash
mvn clean test -Denv=prod -Dtest=ProductAPITest
```

### Parallel Execution

```bash
mvn clean test -DthreadCount=10 -DparallelMode=classes
```

---

## 📝 Troubleshooting

### Issue: Tests fail with Connection Timeout

**Solution**: Check if the API server is running and accessible.

```bash
# Verify API is running
curl -X GET http://localhost:9090/api/v1/products

# Update timeout in config if needed
api.timeout=10000
api.connectionTimeout=5000
```

### Issue: Allure Report not generating

**Solution**: Generate Allure results and serve report.

```bash
# Generate report
mvn allure:report

# Serve with live updates
mvn allure:serve
```

### Issue: Tests passing locally but failing in CI/CD

**Solution**: Verify environment configuration in CI/CD pipeline.

```bash
# Run with specific environment
mvn clean test -Denv=staging

# Check logs
cat target/logs/test-execution.log
```

---

## 📚 Documentation References

- [REST Assured Documentation](https://rest-assured.io/)
- [TestNG Documentation](https://testng.org/doc/)
- [Allure Documentation](https://docs.qameta.io/allure/)
- [Maven Documentation](https://maven.apache.org/)

---

## 👥 Contributing

When adding new tests:

1. Extend `BaseTest` class
2. Use `TestDataBuilder` for test data
3. Use `AssertionUtils` for validations
4. Add `@Description` and `@Story` annotations for Allure
5. Follow existing naming conventions
6. Keep tests independent
7. Add appropriate logging

---

## 📄 License

This framework is part of the Product Catalog Management System.

---

## ✨ Summary

This production-ready API automation framework provides:

✅ Comprehensive REST API testing  
✅ Multiple environment support  
✅ Detailed reporting with Allure  
✅ Reusable components and utilities  
✅ Best practices implementation  
✅ Easy CI/CD integration  
✅ Extensive logging and debugging  
✅ Performance testing capabilities  

**Ready for immediate use and continuous integration!**
