# Product Catalog Management System
## System Design Document

---

## Table of Contents
1. [High-Level Design](#1-high-level-design)
2. [Database Design](#2-database-design)
3. [REST API Design](#3-rest-api-design)
4. [Package Structure](#4-package-structure)
5. [Layered Architecture](#5-layered-architecture)
6. [Exception Handling](#6-exception-handling)
7. [Validation Strategy](#7-validation-strategy)

---

## 1. HIGH-LEVEL DESIGN

### 1.1 System Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                     CLIENT LAYER                            │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │   React UI   │  │   Mobile     │  │   Admin      │      │
│  │   (Customer) │  │   (Future)   │  │   Panel      │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
└─────────────────────────────────────────────────────────────┘
                              ↓ (HTTP/REST)
┌─────────────────────────────────────────────────────────────┐
│                   API GATEWAY LAYER                         │
│  (Authentication, Rate Limiting, Request Validation)       │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│              APPLICATION SERVER (Node.js/Java/Python)      │
│                                                             │
│  ┌────────────────────────────────────────────────────┐    │
│  │          CONTROLLER LAYER                         │    │
│  │  ┌──────────────┐  ┌──────────────┐              │    │
│  │  │ Product      │  │ Category     │              │    │
│  │  │ Controller   │  │ Controller   │              │    │
│  │  └──────────────┘  └──────────────┘              │    │
│  └────────────────────────────────────────────────────┘    │
│                         ↓                                   │
│  ┌────────────────────────────────────────────────────┐    │
│  │          SERVICE LAYER (Business Logic)           │    │
│  │  ┌──────────────┐  ┌──────────────┐              │    │
│  │  │ Product      │  │ Category     │              │    │
│  │  │ Service      │  │ Service      │              │    │
│  │  └──────────────┘  └──────────────┘              │    │
│  └────────────────────────────────────────────────────┘    │
│                         ↓                                   │
│  ┌────────────────────────────────────────────────────┐    │
│  │         REPOSITORY LAYER (Data Access)            │    │
│  │  ┌──────────────┐  ┌──────────────┐              │    │
│  │  │ Product      │  │ Category     │              │    │
│  │  │ Repository   │  │ Repository   │              │    │
│  │  └──────────────┘  └──────────────┘              │    │
│  └────────────────────────────────────────────────────┘    │
│                         ↓                                   │
│  ┌────────────────────────────────────────────────────┐    │
│  │      CROSS-CUTTING CONCERNS                       │    │
│  │  ┌──────────────────────────────────────────┐    │    │
│  │  │ Exception Handling │ Validation │ Logging│    │    │
│  │  └──────────────────────────────────────────┘    │    │
│  └────────────────────────────────────────────────────┘    │
│                                                             │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                  DATA PERSISTENCE LAYER                    │
│  ┌─────────────────────────────────────────────────┐       │
│  │     PostgreSQL / MySQL Database                │       │
│  │  ┌──────────────┐  ┌──────────────┐           │       │
│  │  │  Products    │  │  Categories  │           │       │
│  │  │  Table       │  │  Table       │           │       │
│  │  └──────────────┘  └──────────────┘           │       │
│  └─────────────────────────────────────────────────┘       │
│                                                             │
│  ┌─────────────────────────────────────────────────┐       │
│  │     Redis Cache (Optional for Phase 2)         │       │
│  └─────────────────────────────────────────────────┘       │
└─────────────────────────────────────────────────────────────┘
```

### 1.2 Key Components

| Component | Purpose | Responsibility |
|-----------|---------|-----------------|
| **Controller** | HTTP Request Handler | Parse requests, delegate to service, format responses |
| **Service** | Business Logic | Validation, transformation, orchestration, error handling |
| **Repository** | Data Access | Database queries, CRUD operations, transaction management |
| **DTO** | Data Transfer Object | Request/Response serialization, API contract |
| **Entity** | Domain Model | Database entity, ORM mapping, relationships |
| **Middleware** | Cross-Cutting | Authentication, logging, error handling, rate limiting |

### 1.3 Data Flow Example: Get Product by ID

```
1. HTTP Request: GET /api/products/PROD-001

2. Controller Layer
   └─> ProductController.getProductById(id)
       └─> Validates path parameter
       └─> Calls ProductService

3. Service Layer
   └─> ProductService.getProductById(id)
       └─> Validates business rules
       └─> Calls ProductRepository
       └─> Transforms Entity → DTO

4. Repository Layer
   └─> ProductRepository.findById(id)
       └─> Executes database query
       └─> Returns Product Entity or null

5. Exception Handling
   └─> If not found → ResourceNotFoundException
   └─> If database error → DatabaseException
   └─> If validation fails → ValidationException

6. Response Formatting
   └─> JSON Response with DTO
   └─> HTTP 200 OK with Product data
   └─> HTTP 404 Not Found if product doesn't exist

7. HTTP Response: 200 OK with ProductDTO
```

### 1.4 Request-Response Cycle

```
┌─────────────────────────────────────────────┐
│ HTTP Request                                │
│ {                                           │
│   "method": "POST",                        │
│   "path": "/api/products",                 │
│   "body": { ... }                          │
│ }                                           │
└─────────────────────────────────────────────┘
        ↓
┌─────────────────────────────────────────────┐
│ Request Validation Middleware               │
│ - Check Content-Type                        │
│ - Check Authorization                       │
│ - Rate Limiting                             │
└─────────────────────────────────────────────┘
        ↓
┌─────────────────────────────────────────────┐
│ Controller (ProductController)              │
│ - Parse request body into DTO               │
│ - Route to appropriate service method       │
└─────────────────────────────────────────────┘
        ↓
┌─────────────────────────────────────────────┐
│ Service (ProductService)                    │
│ - Validate business logic                   │
│ - Transform data                            │
│ - Call repository methods                   │
└─────────────────────────────────────────────┘
        ↓
┌─────────────────────────────────────────────┐
│ Repository (ProductRepository)              │
│ - Execute database operations               │
│ - Map rows to entities                      │
└─────────────────────────────────────────────┘
        ↓
┌─────────────────────────────────────────────┐
│ Exception Handling Layer                    │
│ - Catch domain exceptions                   │
│ - Convert to HTTP response                  │
└─────────────────────────────────────────────┘
        ↓
┌─────────────────────────────────────────────┐
│ Response Formatter                          │
│ - Serialize DTO to JSON                     │
│ - Add appropriate status code               │
│ - Add error details if needed               │
└─────────────────────────────────────────────┘
        ↓
┌─────────────────────────────────────────────┐
│ HTTP Response (200 OK / 400 / 404 / 500)   │
│ {                                           │
│   "statusCode": 200,                       │
│   "data": { ... },                         │
│   "timestamp": "2026-10-23T10:30:00Z"      │
│ }                                           │
└─────────────────────────────────────────────┘
```

---

## 2. DATABASE DESIGN

### 2.1 Entity-Relationship Diagram (ERD)

```
┌──────────────────────────────────────┐
│           PRODUCTS                   │
├──────────────────────────────────────┤
│ PK │ id (VARCHAR 50)                │
│    │ name (VARCHAR 100)             │
│    │ description (TEXT)             │
│ FK │ category_id (VARCHAR 50)       │
│    │ price (DECIMAL 10,2)           │
│    │ stockQuantity (INT)            │
│    │ active (BOOLEAN)               │
│    │ createdAt (DATETIME)           │
│    │ updatedAt (DATETIME)           │
│    │ createdBy (VARCHAR 255)        │
│    │ updatedBy (VARCHAR 255)        │
│    │ imageUrl (VARCHAR 500)         │
│    │ version (INT)                  │
└──────────────────────────────────────┘
            │
            │ FK (category_id)
            │ ON DELETE RESTRICT
            │
            ↓
┌──────────────────────────────────────┐
│        CATEGORIES                    │
├──────────────────────────────────────┤
│ PK │ id (VARCHAR 50)                │
│    │ name (VARCHAR 100)             │
│    │ description (TEXT)             │
│    │ active (BOOLEAN)               │
│    │ createdAt (DATETIME)           │
│    │ updatedAt (DATETIME)           │
└──────────────────────────────────────┘
```

### 2.2 Products Table Schema

```sql
CREATE TABLE products (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    category_id VARCHAR(50) NOT NULL,
    price DECIMAL(10, 2) NOT NULL CHECK (price > 0),
    stockQuantity INT NOT NULL DEFAULT 0 CHECK (stockQuantity >= 0),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    imageUrl VARCHAR(500),
    createdAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    createdBy VARCHAR(255),
    updatedBy VARCHAR(255),
    version INT NOT NULL DEFAULT 1,
    
    CONSTRAINT fk_product_category 
        FOREIGN KEY (category_id) 
        REFERENCES categories(id) 
        ON DELETE RESTRICT,
    
    INDEX idx_category_id (category_id),
    INDEX idx_active (active),
    INDEX idx_createdAt (createdAt),
    INDEX idx_name (name),
    UNIQUE INDEX idx_unique_id (id)
);
```

### 2.3 Categories Table Schema

```sql
CREATE TABLE categories (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    createdAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_name (name),
    INDEX idx_active (active)
);
```

### 2.4 Audit/Revision Table (Optional)

```sql
CREATE TABLE product_audit (
    id INT AUTO_INCREMENT PRIMARY KEY,
    product_id VARCHAR(50) NOT NULL,
    action VARCHAR(10) NOT NULL,
    changes JSON,
    changed_by VARCHAR(255),
    changed_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    INDEX idx_product_id (product_id),
    INDEX idx_changed_at (changed_at)
);
```

### 2.5 Indexes Strategy

| Index Name | Table | Columns | Reason |
|-----------|-------|---------|--------|
| `idx_category_id` | products | category_id | Filter by category (FR-2) |
| `idx_active` | products | active | Customer-facing queries only show active products |
| `idx_createdAt` | products | createdAt | Sort by newest (FR-1) |
| `idx_name` | products | name | Full-text search (future enhancement) |
| `idx_unique_id` | products | id | Primary key lookup (FR-3) |
| `idx_name` | categories | name | Category lookup |

### 2.6 Data Type Specifications

| Field | Type | Length | Constraints | Reasoning |
|-------|------|--------|-------------|-----------|
| id | VARCHAR | 50 | PK, NOT NULL, UNIQUE | Flexible ID format (alphanumeric) |
| name | VARCHAR | 100 | NOT NULL | Product name bounded |
| description | TEXT | - | NULLABLE | Large text content |
| category_id | VARCHAR | 50 | NOT NULL, FK | Link to category |
| price | DECIMAL | 10,2 | NOT NULL, >0 | Monetary value precision |
| stockQuantity | INT | - | NOT NULL, >=0 | Integer count |
| active | BOOLEAN | - | NOT NULL, DEFAULT TRUE | Soft delete indicator |
| createdAt | DATETIME | - | NOT NULL, DEFAULT NOW | Audit trail |
| updatedAt | DATETIME | - | NOT NULL, DEFAULT NOW | Audit trail |
| version | INT | - | NOT NULL, DEFAULT 1 | Optimistic locking |

### 2.7 Constraints & Relationships

```
Primary Key Constraints:
  - products.id (UNIQUE, NOT NULL)
  - categories.id (UNIQUE, NOT NULL)

Foreign Key Constraints:
  - products.category_id → categories.id (ON DELETE RESTRICT)
    Prevents deletion of categories with products

Check Constraints:
  - products.price > 0 (Must be positive)
  - products.stockQuantity >= 0 (Cannot be negative)

Unique Constraints:
  - categories.name (One category name per system)
  - products.id (One product per ID)
```

### 2.8 Indexing Strategy Summary

**Hot Queries (Most Frequent):**
1. `SELECT * FROM products WHERE active = TRUE ORDER BY createdAt DESC LIMIT 20`
   → Indexes: active, createdAt

2. `SELECT * FROM products WHERE category_id = ? AND active = TRUE`
   → Indexes: category_id, active

3. `SELECT * FROM products WHERE id = ?`
   → Index: id (PK)

**Optimization Recommendations:**
- Composite index: `(active, createdAt)` for browsing
- Composite index: `(category_id, active)` for category filtering
- Full-text index on name and description (future)

---

## 3. REST API DESIGN

### 3.1 API Overview

```
BASE_URL: https://api.productcatalog.com/api/v1

Resource Endpoints:
  Products:   /products
  Categories: /categories
```

### 3.2 Product Endpoints

#### 3.2.1 GET /products
**List All Products (with Pagination, Sorting, Filtering)**

```
Endpoint: GET /api/v1/products

Query Parameters:
  - page (integer, default: 1) - Page number
  - pageSize (integer, default: 20, max: 100) - Items per page
  - sortBy (string, default: 'createdAt') - Sort field (name, price, createdAt)
  - sortOrder (string, default: 'DESC') - ASC or DESC
  - category (string, optional) - Category ID filter
  - search (string, optional) - Text search (future enhancement)

Request:
  GET /api/v1/products?page=1&pageSize=20&sortBy=createdAt&sortOrder=DESC

Response: 200 OK
{
  "statusCode": 200,
  "data": {
    "products": [
      {
        "id": "PROD-001",
        "name": "Laptop",
        "category": "Electronics",
        "price": 999.99,
        "stockStatus": "In Stock",
        "createdAt": "2026-10-23T10:30:00Z"
      },
      ...
    ],
    "pagination": {
      "page": 1,
      "pageSize": 20,
      "totalItems": 150,
      "totalPages": 8
    }
  },
  "timestamp": "2026-10-23T10:30:00Z"
}

Response: 400 Bad Request
{
  "statusCode": 400,
  "error": "INVALID_PAGINATION",
  "message": "Page size must not exceed 100",
  "timestamp": "2026-10-23T10:30:00Z"
}

Response: 500 Internal Server Error
{
  "statusCode": 500,
  "error": "DATABASE_ERROR",
  "message": "Failed to retrieve products",
  "requestId": "req-123456",
  "timestamp": "2026-10-23T10:30:00Z"
}
```

#### 3.2.2 GET /products/{id}
**Get Product by ID**

```
Endpoint: GET /api/v1/products/{id}

Path Parameters:
  - id (string, required) - Product ID

Request:
  GET /api/v1/products/PROD-001

Response: 200 OK
{
  "statusCode": 200,
  "data": {
    "id": "PROD-001",
    "name": "Laptop",
    "description": "High-performance laptop",
    "category": "Electronics",
    "price": 999.99,
    "stockQuantity": 50,
    "stockStatus": "In Stock",
    "imageUrl": "https://...",
    "createdAt": "2026-10-23T10:30:00Z",
    "updatedAt": "2026-10-23T10:30:00Z"
  },
  "timestamp": "2026-10-23T10:30:00Z"
}

Response: 404 Not Found
{
  "statusCode": 404,
  "error": "RESOURCE_NOT_FOUND",
  "message": "Product with ID 'PROD-001' not found",
  "timestamp": "2026-10-23T10:30:00Z"
}
```

#### 3.2.3 POST /products
**Create New Product**

```
Endpoint: POST /api/v1/products

Headers:
  - Content-Type: application/json
  - Authorization: Bearer {token}

Request Body:
{
  "id": "PROD-001",
  "name": "Laptop",
  "description": "High-performance laptop",
  "categoryId": "ELEC-001",
  "price": 999.99,
  "stockQuantity": 50
}

Request Validation:
  - id: required, string, max 50 chars, unique
  - name: required, string, 1-100 chars
  - description: optional, string, max 1000 chars
  - categoryId: required, string, must exist in categories
  - price: required, decimal, > 0
  - stockQuantity: required, integer, >= 0

Response: 201 Created
{
  "statusCode": 201,
  "data": {
    "id": "PROD-001",
    "name": "Laptop",
    "description": "High-performance laptop",
    "category": "Electronics",
    "price": 999.99,
    "stockQuantity": 50,
    "createdAt": "2026-10-23T10:30:00Z",
    "updatedAt": "2026-10-23T10:30:00Z"
  },
  "message": "Product created successfully",
  "timestamp": "2026-10-23T10:30:00Z"
}

Response: 400 Bad Request
{
  "statusCode": 400,
  "error": "VALIDATION_ERROR",
  "message": "Validation failed",
  "details": [
    {
      "field": "price",
      "message": "Price must be greater than 0"
    },
    {
      "field": "id",
      "message": "Product with ID 'PROD-001' already exists"
    }
  ],
  "timestamp": "2026-10-23T10:30:00Z"
}

Response: 401 Unauthorized
{
  "statusCode": 401,
  "error": "UNAUTHORIZED",
  "message": "Authentication required"
}

Response: 409 Conflict
{
  "statusCode": 409,
  "error": "DUPLICATE_PRODUCT",
  "message": "Product ID 'PROD-001' already exists"
}
```

#### 3.2.4 PUT /products/{id}
**Update Product**

```
Endpoint: PUT /api/v1/products/{id}

Headers:
  - Content-Type: application/json
  - Authorization: Bearer {token}

Path Parameters:
  - id (string, required) - Product ID (immutable)

Request Body:
{
  "name": "Updated Laptop",
  "description": "Updated description",
  "categoryId": "ELEC-001",
  "price": 1199.99,
  "stockQuantity": 45,
  "version": 1
}

Request Validation:
  - All fields validated as in POST (but all optional except version)
  - version: required (for optimistic locking)
  - id: cannot be changed

Response: 200 OK
{
  "statusCode": 200,
  "data": {
    "id": "PROD-001",
    "name": "Updated Laptop",
    "price": 1199.99,
    "stockQuantity": 45,
    "updatedAt": "2026-10-23T10:35:00Z",
    "version": 2
  },
  "message": "Product updated successfully",
  "timestamp": "2026-10-23T10:35:00Z"
}

Response: 409 Conflict (Version Mismatch)
{
  "statusCode": 409,
  "error": "VERSION_CONFLICT",
  "message": "Product has been modified by another user",
  "details": {
    "currentVersion": 2,
    "requestVersion": 1
  }
}

Response: 404 Not Found
{
  "statusCode": 404,
  "error": "RESOURCE_NOT_FOUND",
  "message": "Product with ID 'PROD-001' not found"
}
```

#### 3.2.5 DELETE /products/{id}
**Delete Product (Soft Delete)**

```
Endpoint: DELETE /api/v1/products/{id}

Headers:
  - Authorization: Bearer {token}

Path Parameters:
  - id (string, required) - Product ID

Request:
  DELETE /api/v1/products/PROD-001

Response: 204 No Content
(Empty body, just status code)

Response: 404 Not Found
{
  "statusCode": 404,
  "error": "RESOURCE_NOT_FOUND",
  "message": "Product with ID 'PROD-001' not found"
}

Response: 401 Unauthorized
{
  "statusCode": 401,
  "error": "UNAUTHORIZED",
  "message": "Admin role required to delete products"
}
```

### 3.3 Category Endpoints

#### 3.3.1 GET /categories
**Get All Categories**

```
Endpoint: GET /api/v1/categories

Response: 200 OK
{
  "statusCode": 200,
  "data": [
    {
      "id": "ELEC-001",
      "name": "Electronics",
      "description": "Electronic devices",
      "productCount": 45
    },
    {
      "id": "CLOTH-001",
      "name": "Clothing",
      "description": "Apparel and fashion",
      "productCount": 120
    }
  ],
  "timestamp": "2026-10-23T10:30:00Z"
}
```

### 3.4 Error Response Format

```json
{
  "statusCode": 400,
  "error": "ERROR_CODE",
  "message": "Human-readable error message",
  "details": {
    "field": "fieldName",
    "validationErrors": [...]
  },
  "requestId": "req-abc123xyz",
  "timestamp": "2026-10-23T10:30:00Z"
}
```

### 3.5 Standard HTTP Status Codes

| Status | Code | Scenario |
|--------|------|----------|
| 200 | OK | Successful GET, PUT |
| 201 | Created | Successful POST |
| 204 | No Content | Successful DELETE |
| 400 | Bad Request | Invalid input/validation error |
| 401 | Unauthorized | Missing/invalid authentication |
| 403 | Forbidden | Insufficient permissions |
| 404 | Not Found | Resource doesn't exist |
| 409 | Conflict | Duplicate ID / Version conflict |
| 422 | Unprocessable Entity | Validation failed |
| 429 | Too Many Requests | Rate limit exceeded |
| 500 | Internal Server Error | Server/database error |
| 503 | Service Unavailable | Database down |

---

## 4. PACKAGE STRUCTURE

### 4.1 Project Directory Layout

```
product-catalog/
│
├── src/
│   ├── main/
│   │   ├── java/ (or src/ for Node.js/Python)
│   │   │   └── com/productcatalog/
│   │   │       │
│   │   │       ├── controller/
│   │   │       │   ├── ProductController.java
│   │   │       │   ├── CategoryController.java
│   │   │       │   └── HealthController.java
│   │   │       │
│   │   │       ├── service/
│   │   │       │   ├── ProductService.java
│   │   │       │   ├── ProductServiceImpl.java
│   │   │       │   ├── CategoryService.java
│   │   │       │   └── CategoryServiceImpl.java
│   │   │       │
│   │   │       ├── repository/
│   │   │       │   ├── ProductRepository.java
│   │   │       │   ├── CategoryRepository.java
│   │   │       │   └── JpaProductRepository.java
│   │   │       │
│   │   │       ├── entity/
│   │   │       │   ├── Product.java
│   │   │       │   └── Category.java
│   │   │       │
│   │   │       ├── dto/
│   │   │       │   ├── request/
│   │   │       │   │   ├── CreateProductRequest.java
│   │   │       │   │   ├── UpdateProductRequest.java
│   │   │       │   │   └── CreateCategoryRequest.java
│   │   │       │   └── response/
│   │   │       │       ├── ProductResponse.java
│   │   │       │       ├── ProductListResponse.java
│   │   │       │       ├── CategoryResponse.java
│   │   │       │       └── ApiResponse.java
│   │   │       │
│   │   │       ├── exception/
│   │   │       │   ├── AppException.java
│   │   │       │   ├── ResourceNotFoundException.java
│   │   │       │   ├── ValidationException.java
│   │   │       │   ├── DuplicateResourceException.java
│   │   │       │   ├── DatabaseException.java
│   │   │       │   └── ExceptionHandler.java
│   │   │       │
│   │   │       ├── validation/
│   │   │       │   ├── ProductValidator.java
│   │   │       │   ├── ValidationResult.java
│   │   │       │   └── ValidatorFactory.java
│   │   │       │
│   │   │       ├── config/
│   │   │       │   ├── DatabaseConfig.java
│   │   │       │   ├── AppConfig.java
│   │   │       │   └── SecurityConfig.java
│   │   │       │
│   │   │       ├── middleware/
│   │   │       │   ├── AuthenticationFilter.java
│   │   │       │   ├── LoggingFilter.java
│   │   │       │   ├── RateLimitingFilter.java
│   │   │       │   └── ErrorHandlingFilter.java
│   │   │       │
│   │   │       ├── util/
│   │   │       │   ├── ResponseBuilder.java
│   │   │       │   ├── DateTimeUtil.java
│   │   │       │   └── StringUtil.java
│   │   │       │
│   │   │       └── Application.java (Main entry point)
│   │   │
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-dev.properties
│   │       ├── application-prod.properties
│   │       └── sql/
│   │           ├── schema.sql
│   │           └── data.sql
│   │
│   ├── test/
│   │   ├── java/com/productcatalog/
│   │   │   ├── controller/
│   │   │   │   └── ProductControllerTest.java
│   │   │   ├── service/
│   │   │   │   └── ProductServiceTest.java
│   │   │   ├── repository/
│   │   │   │   └── ProductRepositoryTest.java
│   │   │   └── integration/
│   │   │       └── ProductIntegrationTest.java
│   │   │
│   │   └── resources/
│   │       ├── application-test.properties
│   │       └── test-data.sql
│   │
│   └── docs/
│       ├── API_DOCUMENTATION.md
│       └── ARCHITECTURE.md
│
├── pom.xml (for Maven) / build.gradle (for Gradle)
├── docker/
│   ├── Dockerfile
│   └── docker-compose.yml
│
├── .github/
│   └── workflows/
│       ├── build.yml
│       └── deploy.yml
│
├── README.md
├── REQUIREMENTS.md
└── .gitignore
```

### 4.2 Package Organization Principles

```
By Feature/Domain (Recommended):
  └── com.productcatalog
      ├── product/
      │   ├── controller/
      │   ├── service/
      │   ├── repository/
      │   ├── entity/
      │   ├── dto/
      │   └── exception/
      └── category/
          ├── controller/
          ├── service/
          └── repository/

By Layer (Alternative):
  └── com.productcatalog
      ├── controller/
      ├── service/
      ├── repository/
      ├── entity/
      ├── dto/
      └── exception/

** Recommendation: Use Feature-based organization for scalability
```

### 4.3 Dependency Management

```
Core Dependencies:
  - Spring Boot (3.1.x)
  - Spring Data JPA
  - PostgreSQL Driver
  - Validation API (Jakarta/Javax)
  - Lombok (reduces boilerplate)
  - MapStruct (DTO mapping)
  - Swagger/SpringDoc (API documentation)

Testing Dependencies:
  - JUnit 5
  - Mockito
  - TestContainers (Database testing)
  - Rest Assured (API testing)

Build Tools:
  - Maven or Gradle
  - Git
```

---

## 5. LAYERED ARCHITECTURE

### 5.1 Layer Responsibilities

#### 5.1.1 Controller Layer

**Responsibility:** Handle HTTP requests and responses

```java
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    
    private final ProductService productService;
    
    // 1. Receive HTTP request
    // 2. Extract and validate path/query/body parameters
    // 3. Call service method
    // 4. Format response
    // 5. Return HTTP response
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(
            @PathVariable String id) {
        // Validate input
        // Call service
        // Convert Entity to DTO
        // Return response
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Valid @RequestBody CreateProductRequest request) {
        // Validate request
        // Call service
        // Handle exceptions
        // Return 201 Created
    }
}
```

**Responsibilities:**
- ✅ Request parsing and validation
- ✅ Authorization checks (via annotations)
- ✅ Delegate to service layer
- ✅ Format responses
- ✅ Set HTTP status codes
- ❌ Business logic (belongs to Service)
- ❌ Database queries (belongs to Repository)
- ❌ Data transformation beyond DTO mapping

#### 5.1.2 Service Layer

**Responsibility:** Encapsulate business logic

```java
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    
    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final ProductValidator validator;
    
    // 1. Receive request from controller
    // 2. Validate business rules
    // 3. Orchestrate repository calls
    // 4. Handle cross-cutting concerns
    // 5. Return domain objects or DTOs
    
    @Override
    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        // Validate business rules
        validator.validateCreateProduct(request);
        
        // Check if category exists
        Category category = categoryService.getCategory(request.getCategoryId());
        
        // Check for duplicate ID
        if (productRepository.existsById(request.getId())) {
            throw new DuplicateResourceException("Product ID already exists");
        }
        
        // Create entity
        Product product = ProductMapper.toEntity(request);
        
        // Save to database
        Product saved = productRepository.save(product);
        
        // Return DTO
        return ProductMapper.toResponse(saved);
    }
}
```

**Responsibilities:**
- ✅ Business logic and validation
- ✅ Data transformation
- ✅ Orchestration of multiple repositories
- ✅ Transaction management
- ✅ Exception handling and conversion
- ✅ Caching logic (if applicable)
- ❌ HTTP request/response handling
- ❌ Direct database queries
- ❌ Entity mapping to persistence layer

#### 5.1.3 Repository Layer

**Responsibility:** Data access and persistence

```java
@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    
    // 1. Execute database queries
    // 2. Return entities
    // 3. Handle database errors
    
    List<Product> findByActiveTrue();
    
    List<Product> findByCategoryIdAndActiveTrue(String categoryId);
    
    Page<Product> findByActiveTrue(Pageable pageable);
    
    Optional<Product> findByIdAndActiveTrue(String id);
    
    boolean existsById(String id);
}

// Implementation (if using JPA)
@Repository
public class ProductRepositoryImpl implements ProductRepository {
    
    @Autowired
    private EntityManager entityManager;
    
    // Custom queries not covered by JPA
    public List<Product> findByCustomCriteria(SearchCriteria criteria) {
        // Use Criteria API or JPQL
        // Build dynamic queries
        // Execute and return
    }
}
```

**Responsibilities:**
- ✅ Database queries (CRUD)
- ✅ Entity mapping from rows
- ✅ Query optimization
- ✅ Database error handling (as exceptions)
- ✅ Transaction management (if applicable)
- ✅ Connection pooling
- ❌ Business logic
- ❌ Request/response handling
- ❌ Validation (beyond database constraints)

#### 5.1.4 Entity Layer

**Responsibility:** Domain model representation

```java
@Entity
@Table(name = "products", indexes = {
    @Index(name = "idx_category_id", columnList = "category_id"),
    @Index(name = "idx_active", columnList = "active")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    
    @Id
    private String id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(nullable = false)
    private Integer stockQuantity;
    
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean active;
    
    @Column(nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;
    
    @Column(nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;
    
    @Version
    private Integer version;  // Optimistic locking
    
    // Lifecycle callbacks
    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.active = true;
        this.version = 1;
    }
    
    @PreUpdate
    private void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
```

**Responsibilities:**
- ✅ Domain model properties
- ✅ JPA/ORM annotations
- ✅ Relationships (ManyToOne, OneToMany)
- ✅ Constraints (Column, Index)
- ✅ Lifecycle callbacks (@PrePersist, @PreUpdate)
- ❌ Business logic
- ❌ API serialization (DTO handles this)
- ❌ Validation (minimal, DB constraints only)

#### 5.1.5 DTO Layer

**Responsibility:** Data transfer between layers

```java
// Request DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRequest {
    
    @NotBlank(message = "Product ID is required")
    @Size(max = 50, message = "Product ID cannot exceed 50 characters")
    private String id;
    
    @NotBlank(message = "Product name is required")
    @Size(min = 1, max = 100, message = "Name must be 1-100 characters")
    private String name;
    
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;
    
    @NotBlank(message = "Category ID is required")
    private String categoryId;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @DecimalMax(value = "999999.99", message = "Price cannot exceed 999999.99")
    private BigDecimal price;
    
    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    @Max(value = 1000000, message = "Stock quantity cannot exceed 1,000,000")
    private Integer stockQuantity;
}

// Response DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    
    private String id;
    private String name;
    private String description;
    private String categoryId;
    private String categoryName;
    private BigDecimal price;
    private Integer stockQuantity;
    private String stockStatus;  // "In Stock", "Low Stock", "Out of Stock"
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer version;
}

// Generic API Response
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    
    private Integer statusCode;
    private T data;
    private String message;
    private LocalDateTime timestamp;
    private String error;
    private String requestId;
    
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(200, data, message, LocalDateTime.now(), null, null);
    }
    
    public static <T> ApiResponse<T> error(Integer statusCode, String error, String message) {
        return new ApiResponse<>(statusCode, null, message, LocalDateTime.now(), error, null);
    }
}
```

**Responsibilities:**
- ✅ Request validation (@NotNull, @Size, etc.)
- ✅ API contract definition
- ✅ Data serialization/deserialization
- ✅ Separation from entity model
- ❌ Business logic
- ❌ Database operations
- ❌ Response status code logic

#### 5.1.6 Exception Handling Layer

**Responsibility:** Centralized exception management

```java
// Custom Exception Hierarchy
public abstract class AppException extends RuntimeException {
    private final String errorCode;
    private final Integer statusCode;
    
    public AppException(String message, String errorCode, Integer statusCode) {
        super(message);
        this.errorCode = errorCode;
        this.statusCode = statusCode;
    }
}

public class ResourceNotFoundException extends AppException {
    public ResourceNotFoundException(String message) {
        super(message, "RESOURCE_NOT_FOUND", 404);
    }
}

public class ValidationException extends AppException {
    private final List<FieldError> fieldErrors;
    
    public ValidationException(String message, List<FieldError> fieldErrors) {
        super(message, "VALIDATION_ERROR", 400);
        this.fieldErrors = fieldErrors;
    }
}

public class DuplicateResourceException extends AppException {
    public DuplicateResourceException(String message) {
        super(message, "DUPLICATE_RESOURCE", 409);
    }
}

public class DatabaseException extends AppException {
    public DatabaseException(String message) {
        super(message, "DATABASE_ERROR", 500);
    }
}

// Global Exception Handler
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleNotFoundException(
            ResourceNotFoundException ex, HttpServletRequest request) {
        
        ApiResponse<?> response = ApiResponse.error(404, "RESOURCE_NOT_FOUND", ex.getMessage());
        response.setRequestId(extractRequestId(request));
        
        log.warn("Resource not found: {}", ex.getMessage());
        return ResponseEntity.status(404).body(response);
    }
    
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationException(
            ValidationException ex, HttpServletRequest request) {
        
        ApiResponse<?> response = ApiResponse.error(400, "VALIDATION_ERROR", "Validation failed");
        response.setRequestId(extractRequestId(request));
        // Add validation details
        
        log.warn("Validation failed: {}", ex.getMessage());
        return ResponseEntity.status(400).body(response);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGenericException(
            Exception ex, HttpServletRequest request) {
        
        ApiResponse<?> response = ApiResponse.error(500, "INTERNAL_ERROR", "An error occurred");
        response.setRequestId(extractRequestId(request));
        
        log.error("Unexpected error", ex);
        return ResponseEntity.status(500).body(response);
    }
}
```

**Responsibilities:**
- ✅ Custom exception definitions
- ✅ Exception hierarchy
- ✅ Global exception handler
- ✅ Error response formatting
- ✅ Logging and monitoring
- ❌ Business logic
- ❌ Data access

#### 5.1.7 Validation Layer

**Responsibility:** Input and business rule validation

```java
@Component
public class ProductValidator {
    
    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    
    public void validateCreateProduct(CreateProductRequest request) {
        // 1. Check for duplicate ID
        if (productRepository.existsById(request.getId())) {
            throw new DuplicateResourceException("Product ID already exists");
        }
        
        // 2. Validate category exists
        try {
            categoryService.getCategory(request.getCategoryId());
        } catch (ResourceNotFoundException e) {
            throw new ValidationException("Category not found", List.of(
                new FieldError("categoryId", "Category does not exist")
            ));
        }
        
        // 3. Validate business rules
        if (request.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Invalid price", List.of(
                new FieldError("price", "Price must be greater than 0")
            ));
        }
        
        // 4. Validate stock quantity
        if (request.getStockQuantity() < 0) {
            throw new ValidationException("Invalid stock", List.of(
                new FieldError("stockQuantity", "Stock cannot be negative")
            ));
        }
    }
    
    public void validateUpdateProduct(UpdateProductRequest request, Product existing) {
        // Similar validations, but skip duplicate ID check
        // Check version for optimistic locking
        if (!existing.getVersion().equals(request.getVersion())) {
            throw new VersionConflictException("Product has been modified");
        }
    }
}

@Data
@AllArgsConstructor
public class FieldError {
    private String field;
    private String message;
}
```

**Responsibilities:**
- ✅ Input validation (@Valid, @NotNull, etc.)
- ✅ Business rule validation
- ✅ Cross-field validation
- ✅ Database-level validation (duplicate checks)
- ✅ Consistent error responses
- ❌ HTTP request handling
- ❌ Database operations (except read-only checks)

### 5.2 Layer Communication Flow

```
User Request
    ↓
HTTP Server/Framework
    ↓
Security/Authentication Middleware
    ↓
Validation Middleware (Request format check)
    ↓
┌──────────────────────────────────────────┐
│ CONTROLLER LAYER                         │
│ - Parse @PathVariable, @RequestBody      │
│ - Call ProductValidator (implicit @Valid)│
│ - Delegate to Service                    │
└──────────────────────────────────────────┘
    ↓
┌──────────────────────────────────────────┐
│ SERVICE LAYER                            │
│ - ProductValidator.validateCreateProduct │
│ - CategoryService.validateCategory       │
│ - Call ProductRepository                 │
│ - Handle exceptions                      │
│ - Return DTO                             │
└──────────────────────────────────────────┘
    ↓
┌──────────────────────────────────────────┐
│ REPOSITORY LAYER                         │
│ - ProductRepository.save(entity)         │
│ - EntityManager.persist()                │
│ - Execute INSERT query                   │
│ - Handle SQL errors                      │
│ - Return saved Entity                    │
└──────────────────────────────────────────┘
    ↓
Database (PostgreSQL/MySQL)
    ↓
[Return path - reverse order]
    ↓
Entity → DTO Mapping (Service)
    ↓
ApiResponse formatting (Controller)
    ↓
HTTP Response (201 Created)
    ↓
Client
```

### 5.3 Dependency Injection

```java
// Constructor Injection (Recommended)
@Service
@RequiredArgsConstructor  // Lombok generates constructor
public class ProductServiceImpl implements ProductService {
    
    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final ProductValidator validator;
    
    // Tested and decoupled
}

// Method Injection (Less common)
@Service
public class ProductService {
    
    @Autowired
    public void setRepository(ProductRepository repo) {
        this.productRepository = repo;
    }
}

// Field Injection (Not recommended - tight coupling)
@Service
public class ProductService {
    
    @Autowired  // Avoid this pattern
    private ProductRepository productRepository;
}
```

---

## 6. EXCEPTION HANDLING

### 6.1 Exception Hierarchy

```
Throwable
  └─ Exception
      └─ RuntimeException (Unchecked)
          └─ AppException (Custom base)
              ├─ ResourceNotFoundException (404)
              ├─ ValidationException (400)
              ├─ DuplicateResourceException (409)
              ├─ UnauthorizedException (401)
              ├─ ForbiddenException (403)
              ├─ DatabaseException (500)
              ├─ VersionConflictException (409)
              └─ RateLimitException (429)
```

### 6.2 Exception Handling Strategy

```java
// 1. Throw domain-specific exceptions
@Override
public ProductResponse getProductById(String id) {
    return productRepository.findById(id)
        .map(ProductMapper::toResponse)
        .orElseThrow(() -> new ResourceNotFoundException(
            "Product with ID '" + id + "' not found"
        ));
}

// 2. Catch and convert lower-level exceptions
@Override
public ProductResponse createProduct(CreateProductRequest request) {
    try {
        validator.validateCreateProduct(request);
        Product entity = ProductMapper.toEntity(request);
        Product saved = productRepository.save(entity);
        return ProductMapper.toResponse(saved);
    } catch (DataIntegrityViolationException e) {
        throw new DuplicateResourceException("Product ID already exists");
    } catch (DataAccessException e) {
        log.error("Database error creating product", e);
        throw new DatabaseException("Failed to create product");
    }
}

// 3. Global exception handler formats response
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> handleAppException(AppException ex) {
        return ResponseEntity
            .status(ex.getStatusCode())
            .body(new ErrorResponse(ex.getErrorCode(), ex.getMessage()));
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unexpected exception", ex);
        return ResponseEntity
            .status(500)
            .body(new ErrorResponse("INTERNAL_ERROR", "An error occurred"));
    }
}
```

### 6.3 Common Error Scenarios

| Scenario | Exception | Status | Message |
|----------|-----------|--------|---------|
| Product not found | ResourceNotFoundException | 404 | "Product with ID 'X' not found" |
| Duplicate product ID | DuplicateResourceException | 409 | "Product ID 'X' already exists" |
| Invalid input | ValidationException | 400 | "Validation failed: [details]" |
| Invalid category | ValidationException | 400 | "Category does not exist" |
| Price validation | ValidationException | 400 | "Price must be greater than 0" |
| Stock validation | ValidationException | 400 | "Stock cannot be negative" |
| Version mismatch | VersionConflictException | 409 | "Product has been modified" |
| Auth missing | UnauthorizedException | 401 | "Authentication required" |
| Insufficient role | ForbiddenException | 403 | "Admin role required" |
| Database down | DatabaseException | 500 | "Failed to process request" |
| Rate limit | RateLimitException | 429 | "Too many requests" |

---

## 7. VALIDATION STRATEGY

### 7.1 Validation Layers

```
┌─────────────────────────────────────────┐
│ Validation Layer 1: Bean Validation     │ (DTO level)
│ @NotNull, @Size, @Min, @Max, @Email    │
└─────────────────────────────────────────┘
             ↓
┌─────────────────────────────────────────┐
│ Validation Layer 2: Custom Validators   │ (Business rules)
│ @UniqueProductId, @ValidCategory       │
└─────────────────────────────────────────┘
             ↓
┌─────────────────────────────────────────┐
│ Validation Layer 3: Service Validators  │ (Cross-field, DB checks)
│ ProductValidator.validateCreateProduct  │
└─────────────────────────────────────────┘
             ↓
┌─────────────────────────────────────────┐
│ Validation Layer 4: Database Constraints│ (Last resort)
│ UNIQUE, NOT NULL, CHECK constraints     │
└─────────────────────────────────────────┘
```

### 7.2 Validation Implementation

```java
// Layer 1: DTO Validation (Declarative)
@Data
public class CreateProductRequest {
    
    @NotBlank(message = "Product ID is required")
    @Size(max = 50, message = "ID cannot exceed 50 characters")
    @Pattern(regexp = "^[A-Z0-9-]*$", message = "ID must contain only uppercase letters, numbers, and hyphens")
    private String id;
    
    @NotBlank(message = "Product name is required")
    @Size(min = 1, max = 100, message = "Name must be 1-100 characters")
    private String name;
    
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;
    
    @NotBlank(message = "Category ID is required")
    private String categoryId;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be > 0")
    @DecimalMax(value = "999999.99", message = "Price cannot exceed 999999.99")
    private BigDecimal price;
    
    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock cannot be negative")
    @Max(value = 1000000, message = "Stock cannot exceed 1,000,000")
    private Integer stockQuantity;
}

// Layer 2: Custom Annotation Validators
@Constraint(validatedBy = UniqueProductIdValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueProductId {
    String message() default "Product ID already exists";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

public class UniqueProductIdValidator implements ConstraintValidator<UniqueProductId, String> {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;
        return !productRepository.existsById(value);
    }
}

// Layer 3: Service Validator (Imperative, for complex rules)
@Component
@Slf4j
public class ProductValidator {
    
    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    
    public void validateCreateProduct(CreateProductRequest request) {
        List<FieldError> errors = new ArrayList<>();
        
        // Check ID uniqueness
        if (productRepository.existsById(request.getId())) {
            errors.add(new FieldError("id", "Product ID already exists"));
        }
        
        // Check category exists
        try {
            categoryService.getCategory(request.getCategoryId());
        } catch (ResourceNotFoundException e) {
            errors.add(new FieldError("categoryId", "Category does not exist"));
        }
        
        // Additional business rules
        if (request.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            errors.add(new FieldError("price", "Price must be greater than 0"));
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Validation failed", errors);
        }
        
        log.info("Product validation passed for ID: {}", request.getId());
    }
}

// Layer 4: Database Constraints (SQL Level)
/*
CREATE TABLE products (
    id VARCHAR(50) PRIMARY KEY,
    price DECIMAL(10, 2) NOT NULL CHECK (price > 0),
    stockQuantity INT NOT NULL CHECK (stockQuantity >= 0),
    ...
    UNIQUE KEY unique_id (id),
    FOREIGN KEY (category_id) REFERENCES categories(id)
);
*/
```

### 7.3 Validation Best Practices

```
✅ DO:
  - Use declarative validation (@Valid, @NotNull) for simple rules
  - Use custom validators for cross-field validation
  - Use service validators for database-dependent checks
  - Use database constraints as last resort
  - Provide clear, specific error messages
  - Group validations by type (structural, business, database)

❌ DON'T:
  - Put business logic in validation annotations
  - Validate in multiple places (single responsibility)
  - Perform expensive database checks in every request
  - Ignore database constraint violations
  - Use generic error messages ("Invalid input")
  - Validate at controller level (should be in service/validator)

Example: Where to validate each field

id:
  - Layer 1: @NotBlank, @Size, @Pattern (format check)
  - Layer 3: Service validator (uniqueness check)

price:
  - Layer 1: @NotNull, @DecimalMin, @DecimalMax (range check)
  - Layer 3: Service validator (business rules, e.g., "Cannot lower price of In-Stock items")

categoryId:
  - Layer 1: @NotBlank (required check)
  - Layer 3: Service validator (category exists check via DB)
```

---

## 8. IMPLEMENTATION SUMMARY

### 8.1 Layer Interaction Example: Create Product

```
1. HTTP Request arrives
   POST /api/v1/products
   {
     "id": "PROD-001",
     "name": "Laptop",
     "categoryId": "ELEC-001",
     "price": 999.99,
     "stockQuantity": 50
   }

2. CONTROLLER (ProductController.createProduct)
   - @Valid triggers Layer 1 validation (Bean Validation)
   - Catch MethodArgumentNotValidException
   - Convert request to CreateProductRequest DTO
   - Call service.createProduct(request)

3. SERVICE (ProductService.createProduct)
   - Layer 3 validation: productValidator.validateCreateProduct(request)
     └─ Check ID uniqueness (Repository call)
     └─ Check category exists (Service call)
     └─ Check business rules (price > 0, stock >= 0)
   - Convert DTO to Entity
   - Call repository.save(entity)

4. REPOSITORY (ProductRepository.save)
   - Create new Product entity in database
   - Layer 4 validation: Database constraints
   - Return saved entity with generated fields (createdAt, etc.)

5. SERVICE (continue)
   - Convert Entity back to ResponseDTO
   - Return to Controller

6. CONTROLLER
   - Catch exceptions (Global Exception Handler)
   - Format response with ApiResponse wrapper
   - Return HTTP 201 Created

7. Response sent to client
   {
     "statusCode": 201,
     "data": { /* ProductResponse */ },
     "message": "Product created successfully",
     "timestamp": "2026-10-23T10:30:00Z"
   }
```

### 8.2 File Examples

See separate code files for complete implementation examples:
- ProductController.java
- ProductService.java
- ProductRepository.java
- Product.java (Entity)
- ProductResponse.java (DTO)
- ProductValidator.java
- GlobalExceptionHandler.java

---

## 9. TESTING STRATEGY

### 9.1 Test Coverage by Layer

```
Controller Layer:
  - Unit tests with MockMvc
  - Tests for endpoint routing
  - Tests for HTTP status codes
  - Tests for request/response mapping

Service Layer:
  - Unit tests with Mockito
  - Tests for business logic
  - Tests for service interactions
  - Tests for exception handling

Repository Layer:
  - Integration tests with TestContainers
  - Tests for database queries
  - Tests for transactions

End-to-End:
  - Full workflow tests
  - Real database
  - All layers integrated
```

---

## 10. DEPLOYMENT ARCHITECTURE

### 10.1 Containerization

```dockerfile
FROM openjdk:17-slim
WORKDIR /app
COPY target/product-catalog.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 10.2 Environment Configuration

```properties
# application-prod.properties
spring.datasource.url=jdbc:postgresql://prod-db:5432/productcatalog
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.properties.hibernate.jdbc.batch_size=10
logging.level.root=WARN
```

---

## Document Control

| Property | Value |
|----------|-------|
| **Document Version** | 1.0 |
| **Created Date** | 2026-10-23 |
| **Last Updated** | 2026-10-23 |
| **Status** | Complete |
| **Components Covered** | 5 (Design, Database, API, Package, Architecture) |
| **Layers Defined** | 7 (Controller, Service, Repository, Entity, DTO, Exception, Validation) |
