# Product Catalog Management System

A modern, scalable Product Catalog Management System built with Spring Boot 3, designed to manage and showcase product inventory with comprehensive features for browsing, searching, and managing products.

## Project Overview

**System Type**: REST API Backend  
**Architecture**: Layered (Controller → Service → Repository → Database)  
**Build Tool**: Maven  
**Java Version**: 17  
**Spring Boot Version**: 3.1.5  
**Database**: H2 (Development) / PostgreSQL (Production)  
**Base Package**: `com.wipro.productcatalog`

## Features

### Core Features
1. ✅ **View Products** - Browse all products in a paginated list
2. ✅ **Search by Category** - Filter products by category
3. ✅ **Search by ID** - Find specific products by ID
4. ✅ **Add Product** - Create new products (Admin only)
5. ✅ **Update Product** - Modify existing product information (Admin only)
6. ✅ **Delete Product** - Remove products from catalog (Admin only)

### Product Fields
- **ID** (Unique identifier)
- **Name** (Product name)
- **Description** (Product details)
- **Category** (Product category)
- **Price** (Product price)
- **Stock Quantity** (Available stock)

## Project Structure

```
product-catalog/
├── src/
│   ├── main/
│   │   ├── java/com/wipro/productcatalog/
│   │   │   ├── controller/          # HTTP request handlers
│   │   │   ├── service/             # Business logic
│   │   │   ├── repository/          # Data access layer
│   │   │   ├── entity/              # JPA entities
│   │   │   ├── dto/                 # Data transfer objects
│   │   │   │   ├── request/         # Request DTOs
│   │   │   │   └── response/        # Response DTOs
│   │   │   ├── exception/           # Custom exceptions
│   │   │   ├── validation/          # Validators
│   │   │   ├── config/              # Spring configurations
│   │   │   ├── middleware/          # Filters & interceptors
│   │   │   ├── util/                # Utility classes
│   │   │   └── ProductCatalogApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-dev.properties
│   │       ├── application-test.properties
│   │       ├── application-prod.properties
│   │       └── sql/
│   │           ├── schema.sql       # Database schema
│   │           └── data.sql         # Sample data
│   └── test/
│       └── java/com/wipro/productcatalog/
└── pom.xml
```

## Prerequisites

- **Java 17** or higher
- **Maven 3.6+** or higher
- **Git** (for version control)
- **IDE** (IntelliJ IDEA, Eclipse, VS Code, or Spring Tool Suite)

## Installation & Setup

### 1. Clone the Repository
```bash
cd /home/ubuntu/Desktop/demo/bfsbatch15-ProductCatalog
```

### 2. Build the Project
```bash
mvn clean install
```

### 3. Run the Application

#### Development Environment (H2 Database)
```bash
mvn spring-boot:run
```

#### With Specific Profile
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

#### Production Environment
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=prod"
```

### 4. Access the Application

**API Base URL**: `http://localhost:8080/api`

**Swagger UI** (Documentation): `http://localhost:8080/api/swagger-ui.html`

**H2 Console** (Development): `http://localhost:8080/api/h2-console`
- JDBC URL: `jdbc:h2:mem:productcatalog`
- Username: `sa`
- Password: (leave blank)

## API Endpoints

### Products

#### Get All Products (Paginated)
```
GET /api/v1/products?page=1&pageSize=20&sortBy=createdAt&sortOrder=DESC
```

#### Get Product by ID
```
GET /api/v1/products/{id}
```

#### Create Product (Admin)
```
POST /api/v1/products
Content-Type: application/json

{
  "id": "PROD-001",
  "name": "Laptop",
  "description": "High-performance laptop",
  "categoryId": "ELEC-001",
  "price": 999.99,
  "stockQuantity": 50
}
```

#### Update Product (Admin)
```
PUT /api/v1/products/{id}
Content-Type: application/json

{
  "name": "Updated Name",
  "price": 1199.99,
  "stockQuantity": 45,
  "version": 1
}
```

#### Delete Product (Admin)
```
DELETE /api/v1/products/{id}
```

### Categories

#### Get All Categories
```
GET /api/v1/categories
```

## Database Configuration

### Development (H2)
H2 is an in-memory database that's automatically created on startup. No configuration needed!

**Properties File**: `src/main/resources/application.properties`

### Production (PostgreSQL)
Set environment variables:
```bash
export DB_USERNAME=your_username
export DB_PASSWORD=your_password
export DB_HOST=your_host
export DB_PORT=5432
export DB_NAME=productcatalog
```

**Properties File**: `src/main/resources/application-prod.properties`

## Dependencies

### Core
- Spring Boot 3.1.5
- Spring Web
- Spring Data JPA
- Spring Validation

### Database
- H2 Database (Runtime)
- PostgreSQL Driver (Optional, for production)

### Tools
- Lombok (Reduces boilerplate)
- SpringDoc OpenAPI (Swagger/API Documentation)
- Jackson (JSON Processing)

### Testing
- JUnit 5
- Spring Boot Test
- Mockito (via Spring Boot Test)

## Build & Deploy

### Build WAR/JAR
```bash
mvn clean package
```

### Build Docker Image
```bash
docker build -f docker/Dockerfile -t product-catalog:1.0.0 .
```

### Run with Docker
```bash
docker run -p 8080:8080 \
  -e DB_USERNAME=sa \
  -e DB_PASSWORD=password \
  product-catalog:1.0.0
```

## Development Guidelines

### Code Organization
```
By Package/Feature:
  com.wipro.productcatalog.product.*
  com.wipro.productcatalog.category.*

By Layer:
  controller/ - HTTP handlers
  service/    - Business logic
  repository/ - Data access
  entity/     - Domain models
  dto/        - Transfer objects
```

### Naming Conventions
- **Classes**: PascalCase (e.g., `ProductController`)
- **Methods**: camelCase (e.g., `getProductById`)
- **Constants**: UPPER_CASE (e.g., `MAX_PAGE_SIZE`)
- **Packages**: lowercase with dots (e.g., `com.wipro.productcatalog.controller`)

### Best Practices
1. **Use DTOs** for API requests/responses
2. **Use Services** for business logic
3. **Use Repositories** for data access
4. **Add Validation** at multiple layers
5. **Handle Exceptions** globally
6. **Log Important Events** for debugging
7. **Write Tests** for critical functionality

## Testing

### Run All Tests
```bash
mvn test
```

### Run Tests for Specific Class
```bash
mvn test -Dtest=ProductControllerTest
```

### Run Tests with Coverage
```bash
mvn test jacoco:report
```

## Documentation

### Generated Documentation
- **Swagger UI**: Available at `/api/swagger-ui.html`
- **OpenAPI JSON**: Available at `/api/v3/api-docs`

### Related Documents
- **[REQUIREMENTS.md](./REQUIREMENTS.md)** - Complete requirements specification
- **[USER_STORIES_ACCEPTANCE_CRITERIA.md](./USER_STORIES_ACCEPTANCE_CRITERIA.md)** - User stories and test cases
- **[SYSTEM_DESIGN.md](./SYSTEM_DESIGN.md)** - Technical design and architecture

## Configuration Profiles

### Development (Default)
```bash
# Automatic H2 database creation
# SQL logging enabled
# Swagger UI enabled
spring.profiles.active=dev
```

### Test
```bash
# In-memory H2 database
# Minimal logging
# Swagger disabled
spring.profiles.active=test
```

### Production
```bash
# PostgreSQL database
# No SQL logging
# Swagger disabled
# Enhanced security
spring.profiles.active=prod
```

## Troubleshooting

### Application won't start
1. Check Java version: `java -version` (should be 17+)
2. Check Maven: `mvn -version`
3. Clean and rebuild: `mvn clean install`

### H2 Console not accessible
1. Ensure `spring.h2.console.enabled=true` in properties
2. Check application is running on port 8080
3. Access: `http://localhost:8080/api/h2-console`

### Database connection error
1. Check database credentials in application.properties
2. Verify database service is running
3. Check network connectivity

### Port already in use
1. Change port in `application.properties`: `server.port=8081`
2. Or kill existing process on port 8080

## Contributing

1. Create a new branch: `git checkout -b feature/your-feature`
2. Make your changes
3. Write/update tests
4. Commit changes: `git commit -m "Add your feature"`
5. Push to branch: `git push origin feature/your-feature`
6. Submit a pull request

## Performance Optimization

### Database
- Indexes on frequently queried columns
- Connection pooling configured
- Batch processing for bulk operations

### Caching
- Enable Redis for production (future enhancement)
- Cache category list
- Cache frequently accessed products

### API Response
- Pagination to limit data transfer
- Compression enabled
- JSON serialization optimized

## Security Considerations

### Implemented
- Input validation and sanitization
- SQL injection prevention (Parameterized queries)
- XSS prevention (Output encoding)
- HTTPS/TLS enforcement (Production)

### Future Enhancements
- Authentication (JWT/OAuth)
- Authorization (Role-based access control)
- Rate limiting
- API key management

## License

This project is licensed under the MIT License.

## Support & Contact

For issues, questions, or suggestions:
- Create an issue in the repository
- Contact the development team
- Check documentation in REQUIREMENTS.md and SYSTEM_DESIGN.md

## Changelog

### Version 1.0.0 (Current)
- Initial Spring Boot project setup
- Maven configuration
- Database schema and sample data
- Application properties for dev/test/prod
- Basic test class

---

**Last Updated**: 2026-10-23  
**Project Status**: Ready for Feature Implementation
