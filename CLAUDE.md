# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

---

## Quick Start

### Build & Run
```bash
# Build project and run tests
mvn clean install

# Run application (development)
mvn spring-boot:run

# Run tests only
mvn test

# Run specific test class
mvn test -Dtest=ProductServiceImplTest

# Build production JAR
mvn clean package
```

### Database Access (Development)
- **H2 Console**: http://localhost:8080/api/h2-console
- **JDBC URL**: `jdbc:h2:mem:productcatalog`
- **Username**: `sa` (no password)

### API Documentation
- **Swagger UI**: http://localhost:8080/api/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8080/api/v3/api-docs

---

## Project Architecture

### Layered Architecture Pattern
```
HTTP Request
    ↓
Controller Layer (ProductController)
├─ Parses HTTP requests
├─ Triggers @Valid validation
├─ Delegates to service
└─ Returns ResponseEntity with DTOs

↓
Service Layer (ProductServiceImpl)
├─ Business logic and validation
├─ DTO ↔ Entity transformation
├─ Calls repository methods
└─ Throws custom exceptions

↓
Repository Layer (ProductRepository)
├─ Data access (JpaRepository methods)
├─ Queries database
└─ Returns entities

↓
Database (H2/PostgreSQL)
```

### Key Design Patterns

**1. Constructor Injection (Dependencies)**
```java
@Service
@RequiredArgsConstructor  // Generates constructor automatically
public class ProductServiceImpl {
    private final ProductRepository repository;
    private final CategoryService categoryService;
}
```
- All dependencies are `final` (immutable)
- Testable: easy to mock dependencies
- Use this pattern for all services

**2. DTO Pattern (Request/Response)**
```
Client JSON → CreateProductRequest (DTO with @Valid validation)
    ↓
Service transforms to Product entity
    ↓
Product entity returned from DB
    ↓
Service transforms to ProductResponse (DTO)
    ↓
ProductResponse JSON → Client
```
- DTOs live in `dto/request/` and `dto/response/` packages
- Never expose entities directly in API responses
- Use manual mapping or MapStruct for entity ↔ DTO transformations

**3. Exception Handling (@ControllerAdvice)**
```
Service throws custom exception (ProductNotFoundException, DuplicateProductException)
    ↓
GlobalExceptionHandler.@ControllerAdvice catches
    ↓
@ExceptionHandler method returns ErrorResponse
    ↓
Client gets 404/409/400/500 with formatted JSON
```
- Exceptions are `RuntimeException` (unchecked) - no need to declare in method signature
- Custom exceptions go in `exception/` package
- Add @ExceptionHandler method in GlobalExceptionHandler for new exception types

**4. Validation (Multi-Layer)**
```
Layer 1: DTO validation (@Valid in controller)
├─ @NotBlank, @Size, @Pattern, @DecimalMin, @Min, @Max
├─ Applied automatically by Spring
└─ Returns 400 Bad Request if fails

Layer 2: Service validation (business rules)
├─ Duplicate checks (repository queries)
├─ Category existence (service calls)
├─ Cross-field validations
└─ Throw custom exceptions

Layer 3: Database constraints (fallback)
├─ UNIQUE, NOT NULL, CHECK, FOREIGN KEY
└─ Catches layer 1-2 failures
```

**5. Soft Delete & Versioning**
```java
@Entity
public class Product {
    @Column(columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean active;  // Soft delete: mark as false instead of removing
    
    @Version
    private Integer version;  // Optimistic locking: incremented on every update
}
```
- Deleted products: set `active = false` (data preserved for audit)
- Queries filter by `active = true` for customer views
- Version field prevents lost updates in concurrent scenarios

---

## Code Organization

### Package Structure
```
com.wipro.productcatalog/
├── controller/        ProductController (HTTP handlers)
├── service/           ProductService (interface), ProductServiceImpl (implementation)
├── repository/        ProductRepository (JpaRepository)
├── entity/            Product, Category (JPA entities)
├── dto/
│   ├── request/       CreateProductRequest, UpdateProductRequest
│   └── response/      ProductResponse, ErrorResponse
├── exception/         ProductNotFoundException, DuplicateProductException, GlobalExceptionHandler
├── validation/        (Future: custom validators)
├── config/            (Future: Spring configurations)
└── util/              (Future: utility classes)
```

### Critical Files to Understand Architecture
- **[REQUIREMENTS.md](./REQUIREMENTS.md)** - Business & functional requirements (start here)
- **[SYSTEM_DESIGN.md](./SYSTEM_DESIGN.md)** - Technical design, database schema, API specs
- **[USER_STORIES_ACCEPTANCE_CRITERIA.md](./USER_STORIES_ACCEPTANCE_CRITERIA.md)** - Detailed test cases
- **[ENTITY_ANNOTATIONS_GUIDE.md](./ENTITY_ANNOTATIONS_GUIDE.md)** - JPA/Lombok annotations explained
- **[DTO_VALIDATION_GUIDE.md](./DTO_VALIDATION_GUIDE.md)** - Bean validation annotations explained

---

## Conventions & Patterns to Follow

### 1. Service Implementation Pattern
```java
@Service
@RequiredArgsConstructor
@Slf4j  // Use Lombok for logging
public class NewServiceImpl implements NewService {
    private final SomeDependency dependency;
    
    @Transactional  // For write operations
    public SomeResponse doSomething(SomeRequest request) {
        log.info("Doing something: {}", request.getId());
        
        // Business logic
        // Throw custom exceptions
        // Transform DTOs
        
        return toResponse(result);
    }
    
    @Transactional(readOnly = true)  // For read operations
    public SomeResponse get() {
        // Query and return
    }
    
    private SomeResponse toResponse(SomeEntity entity) {
        // DTO transformation
    }
}
```

### 2. Exception Throwing Pattern
```java
// When product not found
throw new ProductNotFoundException(
    "Product with ID '" + id + "' not found"
);

// When duplicate detected
throw new DuplicateProductException(
    "Product with ID '" + id + "' already exists"
);
```

### 3. Logging Pattern
```java
log.info("Creating product with ID: {}", request.getId());        // Success
log.warn("Duplicate product attempted: {}", productId);           // Business rule violation
log.error("Database error", exception);                            // Unexpected error
```

### 4. Controller Endpoint Pattern
```java
@PostMapping
public ResponseEntity<SomeResponse> create(
        @Valid @RequestBody CreateSomeRequest request) {
    
    log.info("Creating resource");
    SomeResponse response = service.create(request);
    
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(response);
}
```
- All endpoints return `ResponseEntity<T>`
- Use `@Valid` on DTOs to trigger validation
- Delegate all business logic to service
- Set appropriate HTTP status code

### 5. DTO Pattern
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SomeRequest {
    @NotBlank(message = "Field is required")
    @Size(max = 100)
    private String name;
}
```
- Request DTOs: in `dto/request/`, have validation annotations
- Response DTOs: in `dto/response/`, no validation (generated internally)
- Use Lombok annotations for boilerplate

---

## Adding New Features

### Checklist for New CRUD Feature

1. **Create Request DTO** (`dto/request/CreateXxxRequest.java`)
   - Add @Valid annotations
   - Document validation rules

2. **Create Response DTO** (`dto/response/XxxResponse.java`)
   - No validation annotations

3. **Add Service Methods** (`service/XxxService.java` interface + implementation)
   - Add @Transactional
   - Include logging
   - Throw custom exceptions
   - Transform DTOs

4. **Add Repository Methods** (`repository/XxxRepository.java`)
   - Extend JpaRepository
   - Add custom query methods if needed
   - Use @Query for complex queries

5. **Add Controller Endpoints** (`controller/XxxController.java`)
   - Use @Valid on request DTOs
   - Delegate to service
   - Return ResponseEntity with status codes

6. **Add Exception Handling** if creating new exception types
   - Extend RuntimeException
   - Add @ExceptionHandler in GlobalExceptionHandler

---

## Common Modifications

### Add New Entity Field
```java
@Entity
public class Product {
    @Column(name = "new_field", nullable = false)
    @NotNull(message = "Field is required")
    private String newField;
    
    @PrePersist
    protected void onCreate() {
        // Initialize defaults if needed
    }
}
```
- Add column definition in schema.sql
- Update entity with JPA annotations
- Add to DTO if part of API
- Add validation annotations

### Add New Repository Query
```java
@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    
    // Simple method name query (Spring generates SQL)
    List<Product> findByActiveTrue();
    
    // Custom JPQL query
    @Query("SELECT p FROM Product p WHERE p.status = :status AND p.price > :minPrice")
    List<Product> findActiveByMinPrice(@Param("status") String status, @Param("minPrice") BigDecimal minPrice);
}
```

### Add New Exception Type
```java
// 1. Create exception class
public class NewException extends RuntimeException {
    private static final String ERROR_CODE = "NEW_ERROR";
    private static final int HTTP_STATUS = 400;
    
    public NewException(String message) {
        super(message);
    }
}

// 2. Add handler in GlobalExceptionHandler
@ExceptionHandler(NewException.class)
public ResponseEntity<ErrorResponse> handleNewException(NewException ex, HttpServletRequest request) {
    return ResponseEntity
        .status(400)
        .body(ErrorResponse.builder()
            .status(400)
            .error(ex.getErrorCode())
            .message(ex.getMessage())
            .build());
}
```

---

## Testing

### Test Structure
- Unit tests: Mock dependencies, test service logic
- Integration tests: Use TestContainers for real database
- Controller tests: MockMvc for HTTP layer

### Example Test
```java
@SpringBootTest
public class ProductServiceImplTest {
    @MockBean
    private ProductRepository repository;
    
    @InjectMocks
    private ProductServiceImpl service;
    
    @Test
    public void testAddProductThrowsDuplicateException() {
        when(repository.existsById("PROD-001"))
            .thenReturn(true);
        
        assertThrows(DuplicateProductException.class,
            () -> service.addProduct(request));
    }
}
```

---

## Configuration & Profiles

### Application Profiles
- **dev** (default): H2 in-memory, logging enabled, Swagger UI on
- **test**: H2 test database, minimal logging, Swagger off
- **prod**: PostgreSQL, logging warnings only, Swagger off

### Run with Profile
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=prod"
```

---

## Important Notes

1. **Transactions**: Use `@Transactional` on service methods that write to database
2. **Logging**: Use `@Slf4j` and log at appropriate levels (info, warn, error)
3. **Validation**: Perform at multiple layers (DTO, Service, Database)
4. **Exceptions**: Throw custom exceptions in service, handle globally in @ControllerAdvice
5. **DTOs**: Always transform entities to DTOs before returning from controller
6. **Dependencies**: Inject via constructor with `@RequiredArgsConstructor`
7. **Soft Delete**: Mark inactive instead of removing, filter by `active=true` in queries
8. **Version**: @Version field for optimistic locking - check before concurrent updates

---

## Database Schema

Key entities:
- **products**: id (PK), name, description, category_id (FK), price, stockQuantity, active, timestamps, version
- **categories**: id (PK), name, description
- **Indexes**: category_id, active, created_at, name (for query performance)

See [SYSTEM_DESIGN.md](./SYSTEM_DESIGN.md) for complete schema.

---

## Status

**Project Phase**: MVP Implementation  
**Completed**: Entity, DTOs, Repository, Service, Controller, Exception Handling  
**Next Steps**: Category entity & service, Integration tests, API documentation, Deployment setup

---

## References

- [REQUIREMENTS.md](./REQUIREMENTS.md) - Start here for business context
- [SYSTEM_DESIGN.md](./SYSTEM_DESIGN.md) - Architecture, database, API design
- [USER_STORIES_ACCEPTANCE_CRITERIA.md](./USER_STORIES_ACCEPTANCE_CRITERIA.md) - Test cases
- [README.md](./README.md) - Setup and running instructions
- Spring Boot Docs: https://spring.io/projects/spring-boot
- Spring Data JPA: https://spring.io/projects/spring-data-jpa
