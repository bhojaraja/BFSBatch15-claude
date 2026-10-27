# Product Entity - JPA & Lombok Annotations Guide

## Overview

The Product entity uses JPA (Java Persistence API) annotations for database mapping and Lombok annotations for code generation. This guide explains each annotation used in the Product entity.

---

## JPA Annotations

### 1. **@Entity**
```java
@Entity
public class Product { ... }
```

**Purpose**: Marks the class as a JPA entity  
**Scope**: Class-level  
**Effect**: Hibernate maps this class to a database table  
**Requirements**: 
- Must have a no-arg constructor (even if private)
- Must have an @Id field
- Must not be final

**Example in Product**:
```java
@Entity  // This class is a database entity
public class Product { ... }
```

---

### 2. **@Table**
```java
@Table(
    name = "products",
    indexes = {
        @Index(name = "idx_category_id", columnList = "category_id"),
        @Index(name = "idx_active", columnList = "active")
    }
)
```

**Purpose**: Specifies the database table name and configuration  
**Scope**: Class-level (paired with @Entity)  
**Attributes**:
- `name` - Table name in database (default: class name lowercase)
- `indexes` - Array of indexes for performance optimization
- `schema` - Schema name (if multi-schema database)
- `uniqueConstraints` - Unique constraint definitions

**Example in Product**:
```java
@Table(name = "products")  // Maps to 'products' table
```

**Index Definition**:
```java
@Index(name = "idx_category_id", columnList = "category_id")
// Creates index: CREATE INDEX idx_category_id ON products(category_id);
```

**Benefits**:
- Faster queries on indexed columns
- Better search performance for category filtering
- Automatic query optimization

---

### 3. **@Id**
```java
@Id
private String id;
```

**Purpose**: Marks the field as primary key  
**Scope**: Field-level  
**Characteristics**:
- Must be unique for each record
- Cannot be null
- Only one @Id per entity

**Example in Product**:
```java
@Id
@Column(name = "id", length = 50, nullable = false)
private String id;  // Primary key: PROD-001, PROD-002, etc.
```

**Important**: In Product entity, ID is NOT auto-generated (business requirement)

---

### 4. **@GeneratedValue** (Not used in Product - See Note)
```java
// NOT USED in Product entity because ID is manually assigned
// Example for reference:
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
```

**Purpose**: Specifies auto-generation strategy for @Id fields  
**Strategies**:
- `IDENTITY` - Database auto-increment (MySQL, PostgreSQL)
- `SEQUENCE` - Database sequence (Oracle, PostgreSQL)
- `TABLE` - Table-based generation
- `UUID` - UUID generation

**Why NOT used in Product**:
- Product ID is manually assigned (e.g., PROD-001)
- Business logic determines the ID format
- Users input the ID during product creation

**Alternative for Reference ID** (if we need auto-generated numeric ID):
```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long refId;  // Auto-generated: 1, 2, 3, ...
```

---

### 5. **@Column**
```java
@Column(name = "id", length = 50, nullable = false)
private String id;
```

**Purpose**: Specifies column properties in the table  
**Scope**: Field-level  
**Common Attributes**:

| Attribute | Purpose | Example |
|-----------|---------|---------|
| `name` | Column name in DB | `@Column(name = "product_id")` |
| `length` | String max length | `@Column(length = 100)` |
| `nullable` | Allow NULL values | `@Column(nullable = false)` |
| `unique` | Unique constraint | `@Column(unique = true)` |
| `precision` | Total digits (decimals) | `@Column(precision = 10)` |
| `scale` | Decimal places | `@Column(scale = 2)` |
| `updatable` | Allow updates | `@Column(updatable = false)` |
| `columnDefinition` | Raw SQL definition | `@Column(columnDefinition = "TEXT")` |

**Examples in Product**:

```java
// String field with length constraint
@Column(name = "name", length = 100, nullable = false)
private String name;  // VARCHAR(100) NOT NULL

// Text field
@Column(name = "description", columnDefinition = "TEXT")
private String description;  // TEXT (unlimited)

// Decimal field with precision
@Column(name = "price", precision = 10, scale = 2, nullable = false)
private BigDecimal price;  // DECIMAL(10,2) NOT NULL

// Immutable field (cannot be updated)
@Column(name = "created_at", updatable = false)
private LocalDateTime createdAt;  // Set once, never changed

// Boolean with default
@Column(name = "active", columnDefinition = "BOOLEAN DEFAULT TRUE")
private Boolean active = true;  // BOOLEAN DEFAULT TRUE
```

---

### 6. **@ManyToOne**
```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "category_id", nullable = false)
private Category category;
```

**Purpose**: Defines many-to-one relationship (Product ← Category)  
**Scope**: Field-level  
**Relationship**: Many Products can belong to ONE Category

**Attributes**:
- `fetch` - FetchType.LAZY (load on demand) or FetchType.EAGER (load immediately)
- `optional` - Whether relationship is required (default: true)
- `cascade` - Propagate operations (PERSIST, MERGE, REMOVE, etc.)

**Fetch Types**:

| Type | Behavior | Use Case |
|------|----------|----------|
| LAZY | Loads category only when accessed | ✅ Default, prevents N+1 queries |
| EAGER | Loads category immediately | ❌ Can cause performance issues |

**Example in Product**:
```java
@ManyToOne(fetch = FetchType.LAZY)  // Don't load category until needed
private Category category;
```

**SQL Generated**:
```sql
-- When Product is loaded, Category is NOT loaded
SELECT * FROM products WHERE id = 'PROD-001';

-- Category is loaded only when accessed: product.getCategory()
SELECT * FROM categories WHERE id = 'ELEC-001';
```

---

### 7. **@JoinColumn**
```java
@JoinColumn(name = "category_id", nullable = false)
private Category category;
```

**Purpose**: Specifies the foreign key column  
**Scope**: Field-level (paired with @ManyToOne, @OneToOne)  
**Attributes**:
- `name` - Column name for foreign key
- `nullable` - Allow NULL (if false, relationship is required)
- `unique` - Force uniqueness
- `referencedColumnName` - Column in referenced table (default: @Id)

**Example in Product**:
```java
@JoinColumn(name = "category_id", nullable = false)
// Creates: FOREIGN KEY (category_id) REFERENCES categories(id)
```

**SQL Generated**:
```sql
ALTER TABLE products ADD CONSTRAINT fk_product_category
    FOREIGN KEY (category_id) REFERENCES categories(id);
```

---

### 8. **@Version**
```java
@Version
@Column(name = "version", nullable = false)
private Integer version;
```

**Purpose**: Enables Optimistic Locking for concurrency control  
**Scope**: Field-level  
**Behavior**:
- Automatically incremented on every update
- Prevents lost updates when multiple users edit same record
- Raises OptimisticLockingFailureException if version mismatch

**Optimistic Locking Example**:
```
Scenario: Two admins update same product simultaneously

Admin A (version 1):
  UPDATE products SET price = 1199.99, version = 2 
  WHERE id = 'PROD-001' AND version = 1;
  ✅ Success (version matches)

Admin B (version 1):
  UPDATE products SET name = 'New Name', version = 2 
  WHERE id = 'PROD-001' AND version = 1;
  ❌ Fails (version is now 2, not 1)
  
// Admin B must reload product (now version 2) and retry update
```

**Benefits**:
- No row-level locking (better performance)
- Detects concurrent modifications
- Allows graceful handling of conflicts

---

### 9. **@PrePersist**
```java
@PrePersist
protected void onCreate() {
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
    this.version = 1;
    this.active = true;
}
```

**Purpose**: Lifecycle callback before INSERT  
**Scope**: Method-level  
**Timing**: Called by Hibernate just before inserting entity into database  
**Use Cases**:
- Set creation timestamp
- Initialize default values
- Set audit fields (createdBy)
- Generate IDs or keys

**Example in Product**:
```java
@PrePersist
protected void onCreate() {
    // Automatically called when: session.save(product) or repository.save(product)
    this.createdAt = LocalDateTime.now();  // Set creation time
    this.version = 1;                      // Initialize version
}

// Usage:
Product product = new Product();
product.setId("PROD-001");
product.setName("Laptop");
// ... set other fields ...
repository.save(product);
// @PrePersist fires here, createdAt is set automatically
```

---

### 10. **@PreUpdate**
```java
@PreUpdate
protected void onUpdate() {
    this.updatedAt = LocalDateTime.now();
}
```

**Purpose**: Lifecycle callback before UPDATE  
**Scope**: Method-level  
**Timing**: Called by Hibernate just before updating entity in database  
**Use Cases**:
- Update modification timestamp
- Update audit fields (updatedBy)
- Validate state changes
- Log changes

**Example in Product**:
```java
@PreUpdate
protected void onUpdate() {
    // Automatically called when: session.update(product) or repository.save(existing_product)
    this.updatedAt = LocalDateTime.now();  // Always update timestamp
}

// Usage:
Product product = repository.findById("PROD-001");
product.setPrice(new BigDecimal("1199.99"));
repository.save(product);
// @PreUpdate fires here, updatedAt is set automatically
```

---

## Lombok Annotations

### 1. **@Data**
```java
@Data
public class Product { ... }
```

**Purpose**: Auto-generates getters, setters, equals(), hashCode(), toString()  
**Scope**: Class-level  
**Generates**:
- Getter for every field
- Setter for every field
- equals() - compares all fields
- hashCode() - based on all fields
- toString() - shows all field values
- canEqual() - helper for equals()

**Example**:
```java
@Data
public class Product {
    private String id;
    private String name;
}

// Generated code (automatically):
public String getId() { return this.id; }
public void setId(String id) { this.id = id; }
public String getName() { return this.name; }
public void setName(String name) { this.name = name; }
public boolean equals(Object o) { /* ... */ }
public int hashCode() { /* ... */ }
public String toString() { /* ... */ }
```

**Excludes**:
- Static fields
- Fields with @Exclude annotation

---

### 2. **@NoArgsConstructor**
```java
@NoArgsConstructor
public class Product { ... }
```

**Purpose**: Generates a no-argument constructor  
**Scope**: Class-level  
**Generated**:
```java
public Product() {
    // Empty constructor
}
```

**Why Needed in JPA**:
- JPA requires an empty constructor for reflection-based entity instantiation
- Hibernate uses this to create instances from database rows
- Required for deserialization

**Example**:
```java
@NoArgsConstructor
public class Product { ... }

// Usage:
Product p = new Product();  // Works because constructor generated
```

---

### 3. **@AllArgsConstructor**
```java
@AllArgsConstructor
public class Product { ... }
```

**Purpose**: Generates constructor with all fields as parameters  
**Scope**: Class-level  
**Generated**:
```java
public Product(String id, String name, String description, 
               Category category, BigDecimal price, 
               Integer stockQuantity, Boolean active, 
               LocalDateTime createdAt, LocalDateTime updatedAt, 
               String createdBy, String updatedBy, Integer version) {
    this.id = id;
    this.name = name;
    // ... all fields ...
}
```

**Use Case**: Easy object creation with all values
```java
Product product = new Product(
    "PROD-001",
    "Laptop",
    "High-performance laptop",
    category,
    new BigDecimal("999.99"),
    50,
    true,
    LocalDateTime.now(),
    LocalDateTime.now(),
    "admin",
    "admin",
    1
);
```

---

### 4. **@Builder**
```java
@Builder
public class Product { ... }
```

**Purpose**: Generates Builder pattern for object creation  
**Scope**: Class-level  
**Generates**: Builder inner class with fluent API  

**Use Case**: Clean, readable object construction
```java
Product product = Product.builder()
    .id("PROD-001")
    .name("Laptop")
    .description("High-performance laptop")
    .category(electronics)
    .price(new BigDecimal("999.99"))
    .stockQuantity(50)
    .active(true)
    .createdBy("admin")
    .updatedBy("admin")
    .build();
```

**Benefits**:
- ✅ More readable than constructor
- ✅ Don't need to remember parameter order
- ✅ Can set optional fields
- ✅ Self-documenting

---

## Validation Annotations

### Jakarta Bean Validation Annotations

These annotations ensure data integrity at the entity level:

```java
@NotNull(message = "Price cannot be null")
private BigDecimal price;

@NotBlank(message = "Product name cannot be blank")
private String name;

@DecimalMin(value = "0.01", message = "Price must be greater than 0")
private BigDecimal price;

@Size(min = 1, max = 100, message = "Name must be 1-100 characters")
private String name;
```

**Validation Trigger Points**:
1. Entity creation
2. Entity update
3. API request validation (via @Valid in controller)

**Validation Layers**:
- Layer 1: DTO validation (in request)
- Layer 2: Entity validation (before persist)
- Layer 3: Database constraints (fallback)

---

## Complete Annotation Reference

| Annotation | Level | Purpose | Example |
|-----------|-------|---------|---------|
| @Entity | Class | Mark as JPA entity | `@Entity` |
| @Table | Class | Map to table, add indexes | `@Table(name="products")` |
| @Id | Field | Primary key | `@Id private String id;` |
| @Column | Field | Column properties | `@Column(name="id", length=50)` |
| @ManyToOne | Field | Many-to-one relationship | `@ManyToOne private Category category;` |
| @JoinColumn | Field | Foreign key column | `@JoinColumn(name="category_id")` |
| @Version | Field | Optimistic locking | `@Version private Integer version;` |
| @PrePersist | Method | Before insert | `@PrePersist void onCreate()` |
| @PreUpdate | Method | Before update | `@PreUpdate void onUpdate()` |
| @Data | Class | Generate getters/setters | `@Data` |
| @NoArgsConstructor | Class | Empty constructor | `@NoArgsConstructor` |
| @AllArgsConstructor | Class | All-args constructor | `@AllArgsConstructor` |
| @Builder | Class | Builder pattern | `@Builder` |
| @NotNull | Field | Null validation | `@NotNull` |
| @NotBlank | Field | Blank validation | `@NotBlank` |
| @Size | Field | Size validation | `@Size(min=1, max=100)` |
| @DecimalMin | Field | Minimum decimal | `@DecimalMin("0.01")` |
| @Min | Field | Minimum value | `@Min(0)` |

---

## Database Schema Generated

```sql
CREATE TABLE products (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    category_id VARCHAR(50) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    stock_quantity INT NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    image_url VARCHAR(500),
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version INT NOT NULL,
    
    FOREIGN KEY (category_id) REFERENCES categories(id),
    INDEX idx_category_id (category_id),
    INDEX idx_active (active),
    INDEX idx_created_at (created_at),
    INDEX idx_name (name)
);
```

---

## Common Patterns

### Pattern 1: Required Field with Constraint
```java
@Column(name = "price", precision = 10, scale = 2, nullable = false)
@NotNull(message = "Price cannot be null")
@DecimalMin(value = "0.01", message = "Price must be greater than 0")
private BigDecimal price;
```
**Layers**: 3 validations (entity, constraint, DB)

### Pattern 2: Optional Field with Length Constraint
```java
@Column(name = "description", columnDefinition = "TEXT")
@Size(max = 1000, message = "Description cannot exceed 1000 characters")
private String description;
```
**Layers**: 2 validations (entity, optional at entity level)

### Pattern 3: Immutable Audit Field
```java
@Column(name = "created_at", nullable = false, updatable = false)
private LocalDateTime createdAt;
```
**Property**: Set once via @PrePersist, never updated

### Pattern 4: Relationship with Constraint
```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "category_id", nullable = false)
@NotNull(message = "Category cannot be null")
private Category category;
```
**Validation**: Required relationship, lazy-loaded

---

## Key Takeaways

1. **@Entity + @Table**: Maps Java class to database table
2. **@Id**: Marks primary key field
3. **@Column**: Configures column properties and constraints
4. **@ManyToOne + @JoinColumn**: Defines relationships
5. **@Version**: Enables optimistic locking
6. **@PrePersist / @PreUpdate**: Auto-populate timestamps and defaults
7. **@Data**: Generates boilerplate (getters, setters, equals, hashCode)
8. **@NoArgsConstructor + @AllArgsConstructor**: Constructors for flexibility
9. **@Builder**: Fluent API for object creation
10. **Validation Annotations**: Multi-layer data integrity

---

**File Location**: `/src/main/java/com/wipro/productcatalog/entity/Product.java`  
**Last Updated**: 2026-10-26
