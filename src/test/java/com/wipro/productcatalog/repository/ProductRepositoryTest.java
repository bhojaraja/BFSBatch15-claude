package com.wipro.productcatalog.repository;

import com.wipro.productcatalog.entity.Category;
import com.wipro.productcatalog.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DisplayName("ProductRepository Test Suite")
class ProductRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category electronics;
    private Category clothing;
    private Product laptop;
    private Product mouse;
    private Product shirt;

    @BeforeEach
    void setUp() {
        // Create and persist test categories first
        electronics = Category.builder()
            .id("ELEC-001")
            .name("Electronics")
            .description("Electronic items")
            .active(true)
            .build();

        clothing = Category.builder()
            .id("CLOTH-001")
            .name("Clothing")
            .description("Apparel and accessories")
            .active(true)
            .build();

        categoryRepository.save(electronics);
        categoryRepository.save(clothing);
        entityManager.flush();

        // Refresh categories to ensure they are managed
        electronics = entityManager.merge(electronics);
        clothing = entityManager.merge(clothing);

        // Create test products in electronics category
        laptop = Product.builder()
            .id("PROD-001")
            .name("Laptop Pro 15")
            .description("High-performance laptop")
            .category(electronics)
            .price(BigDecimal.valueOf(999.99))
            .stockQuantity(50)
            .active(true)
            .build();

        mouse = Product.builder()
            .id("PROD-002")
            .name("Wireless Mouse")
            .description("Ergonomic wireless mouse")
            .category(electronics)
            .price(BigDecimal.valueOf(29.99))
            .stockQuantity(100)
            .active(true)
            .build();

        // Create test product in clothing category
        shirt = Product.builder()
            .id("PROD-003")
            .name("Cotton T-Shirt")
            .description("Comfortable cotton shirt")
            .category(clothing)
            .price(BigDecimal.valueOf(19.99))
            .stockQuantity(200)
            .active(true)
            .build();

        productRepository.save(laptop);
        productRepository.save(mouse);
        productRepository.save(shirt);

        entityManager.flush();
    }

    // ==================== FIND BY CATEGORY ID TEST CASES ====================

    @Test
    @DisplayName("Test 1: Find Products by Category ID - Should return all products in category")
    void testFindByCategoryId() {
        // Act
        List<Product> electronicsProducts = productRepository.findByCategoryId("ELEC-001");

        // Assert
        assertNotNull(electronicsProducts);
        assertEquals(2, electronicsProducts.size());
        assertTrue(electronicsProducts.stream().anyMatch(p -> p.getId().equals("PROD-001")));
        assertTrue(electronicsProducts.stream().anyMatch(p -> p.getId().equals("PROD-002")));
        assertFalse(electronicsProducts.stream().anyMatch(p -> p.getId().equals("PROD-003")));
    }

    @Test
    @DisplayName("Test 2: Find Products by Invalid Category ID - Should return empty list")
    void testFindByCategoryIdNotFound() {
        // Act
        List<Product> products = productRepository.findByCategoryId("INVALID-CAT");

        // Assert
        assertNotNull(products);
        assertEquals(0, products.size());
    }

    @Test
    @DisplayName("Test 3: Find Products by Category ID - Should include all products (no active filter)")
    void testFindByCategoryIdWithDeletedProducts() {
        // Arrange - Create and delete a product
        Product deletedProduct = Product.builder()
            .id("PROD-004")
            .name("Keyboard")
            .description("Mechanical keyboard")
            .category(electronics)
            .price(BigDecimal.valueOf(99.99))
            .stockQuantity(30)
            .active(false)  // Marked as deleted
            .build();
        productRepository.save(deletedProduct);
        entityManager.flush();

        // Act
        List<Product> electronicsProducts = productRepository.findByCategoryId("ELEC-001");

        // Assert - Should include all products in category (no active filter in this method)
        assertNotNull(electronicsProducts);
        assertEquals(3, electronicsProducts.size());
    }

    // ==================== FIND ACTIVE BY CATEGORY ID TEST CASES ====================

    @Test
    @DisplayName("Test 4: Find Active Products by Category ID - Should return only active products")
    void testFindActiveByCategoryId() {
        // Act
        List<Product> activeElectronicsProducts = productRepository.findActiveByCategoryId("ELEC-001");

        // Assert
        assertNotNull(activeElectronicsProducts);
        assertEquals(2, activeElectronicsProducts.size());
        assertTrue(activeElectronicsProducts.stream().allMatch(p -> p.getActive()));
    }

    @Test
    @DisplayName("Test 5: Find Active Products by Category ID - Should exclude deleted products")
    void testFindActiveByCategoryIdExcludesDeletedProducts() {
        // Arrange - Soft delete an existing product
        laptop.setActive(false);
        productRepository.save(laptop);
        entityManager.flush();

        // Act
        List<Product> activeElectronicsProducts = productRepository.findActiveByCategoryId("ELEC-001");

        // Assert - Should NOT include deleted product
        assertNotNull(activeElectronicsProducts);
        assertEquals(1, activeElectronicsProducts.size());  // Only mouse should be returned
        assertFalse(activeElectronicsProducts.stream().anyMatch(p -> p.getId().equals("PROD-001")));
        assertTrue(activeElectronicsProducts.stream().anyMatch(p -> p.getId().equals("PROD-002")));
    }

    @Test
    @DisplayName("Test 6: Find Active Products by Invalid Category - Should return empty list")
    void testFindActiveByCategoryIdNotFound() {
        // Act
        List<Product> products = productRepository.findActiveByCategoryId("INVALID-CAT");

        // Assert
        assertNotNull(products);
        assertEquals(0, products.size());
    }

    // ==================== EXISTS BY NAME TEST CASES ====================

    @Test
    @DisplayName("Test 7: Check if Product Name Exists - Should return true for existing name")
    void testExistsByNameTrue() {
        // Act
        boolean exists = productRepository.existsByName("Laptop Pro 15");

        // Assert
        assertTrue(exists);
    }

    @Test
    @DisplayName("Test 8: Check if Product Name Exists - Should return false for non-existing name")
    void testExistsByNameFalse() {
        // Act
        boolean exists = productRepository.existsByName("Non-existent Product");

        // Assert
        assertFalse(exists);
    }

    @Test
    @DisplayName("Test 9: Check if Deleted Product Name Exists - Should still return true for soft deleted")
    void testExistsByNameSoftDeleted() {
        // Arrange - Delete the product (soft delete)
        laptop.setActive(false);
        productRepository.save(laptop);
        entityManager.flush();

        // Act
        boolean exists = productRepository.existsByName("Laptop Pro 15");

        // Assert - Name still exists in DB even though marked as inactive
        assertTrue(exists);
    }

    // ==================== EXISTS BY NAME AND ID NOT TEST CASES ====================

    @Test
    @DisplayName("Test 10: Check Duplicate Name Excluding Self - Should return false for same product")
    void testExistsByNameAndIdNotSameProduct() {
        // Act - Check if "Laptop Pro 15" exists excluding PROD-001
        boolean exists = productRepository.existsByNameAndIdNot("Laptop Pro 15", "PROD-001");

        // Assert - Should be false because we're excluding PROD-001
        assertFalse(exists);
    }

    @Test
    @DisplayName("Test 11: Check Duplicate Name Excluding Self - Should return true for different product")
    void testExistsByNameAndIdNotDifferentProduct() {
        // Act - Check if "Laptop Pro 15" exists excluding PROD-002
        boolean exists = productRepository.existsByNameAndIdNot("Laptop Pro 15", "PROD-002");

        // Assert - Should be true because PROD-001 has this name
        assertTrue(exists);
    }

    @Test
    @DisplayName("Test 12: Check Duplicate Name Excluding Self - Should return false for non-existing name")
    void testExistsByNameAndIdNotNotFound() {
        // Act
        boolean exists = productRepository.existsByNameAndIdNot("Non-existent Product", "PROD-001");

        // Assert
        assertFalse(exists);
    }

    // ==================== FIND BY CATEGORY ENTITY TEST CASES ====================

    @Test
    @DisplayName("Test 13: Find Products by Category Entity - Should return products in category")
    void testFindByCategory() {
        // Act
        List<Product> electronicsProducts = productRepository.findByCategory(electronics);

        // Assert
        assertNotNull(electronicsProducts);
        assertEquals(2, electronicsProducts.size());
        assertTrue(electronicsProducts.stream().allMatch(p -> p.getCategory().equals(electronics)));
    }

    @Test
    @DisplayName("Test 14: Find Products by Category Entity - Should include deleted products")
    void testFindByCategoryIncludesDeleted() {
        // Arrange - Create and delete a product
        Product deletedProduct = Product.builder()
            .id("PROD-004")
            .name("Keyboard")
            .category(electronics)
            .price(BigDecimal.valueOf(99.99))
            .stockQuantity(30)
            .active(false)
            .build();
        productRepository.save(deletedProduct);
        entityManager.flush();

        // Act
        List<Product> allElectronicsProducts = productRepository.findByCategory(electronics);

        // Assert - Should include deleted product
        assertNotNull(allElectronicsProducts);
        assertEquals(3, allElectronicsProducts.size());
    }

    // ==================== COUNT BY CATEGORY ID TEST CASES ====================

    @Test
    @DisplayName("Test 15: Count Products by Category ID - Should return correct count")
    void testCountByCategoryId() {
        // Act
        long count = productRepository.countByCategoryId("ELEC-001");

        // Assert
        assertEquals(2, count);
    }

    @Test
    @DisplayName("Test 16: Count Products by Category ID - Should return 0 for empty category")
    void testCountByCategoryIdEmpty() {
        // Act
        long count = productRepository.countByCategoryId("INVALID-CAT");

        // Assert
        assertEquals(0, count);
    }

    @Test
    @DisplayName("Test 17: Count Products by Category ID - Should include deleted products")
    void testCountByCategoryIdIncludesDeleted() {
        // Arrange - Create and delete a product
        Product deletedProduct = Product.builder()
            .id("PROD-004")
            .name("Keyboard")
            .category(electronics)
            .price(BigDecimal.valueOf(99.99))
            .stockQuantity(30)
            .active(false)
            .build();
        productRepository.save(deletedProduct);
        entityManager.flush();

        // Act
        long count = productRepository.countByCategoryId("ELEC-001");

        // Assert
        assertEquals(3, count);
    }

    // ==================== EXISTS BY ID AND ACTIVE TRUE TEST CASES ====================

    @Test
    @DisplayName("Test 18: Check Product Exists and Active - Should return true for active product")
    void testExistsByIdAndActiveTrueActive() {
        // Act
        boolean exists = productRepository.existsByIdAndActiveTrue("PROD-001");

        // Assert
        assertTrue(exists);
    }

    @Test
    @DisplayName("Test 19: Check Product Exists and Active - Should return false for deleted product")
    void testExistsByIdAndActiveTrueDeleted() {
        // Arrange - Delete the product
        laptop.setActive(false);
        productRepository.save(laptop);
        entityManager.flush();

        // Act
        boolean exists = productRepository.existsByIdAndActiveTrue("PROD-001");

        // Assert
        assertFalse(exists);
    }

    @Test
    @DisplayName("Test 20: Check Product Exists and Active - Should return false for non-existent product")
    void testExistsByIdAndActiveTrueNotFound() {
        // Act
        boolean exists = productRepository.existsByIdAndActiveTrue("PROD-999");

        // Assert
        assertFalse(exists);
    }

    // ==================== INHERITED CRUD METHODS TEST CASES ====================

    @Test
    @DisplayName("Test 21: Save Product - Should persist product to database")
    void testSaveProduct() {
        // Arrange
        Product newProduct = Product.builder()
            .id("PROD-005")
            .name("Monitor")
            .description("4K Monitor")
            .category(electronics)
            .price(BigDecimal.valueOf(299.99))
            .stockQuantity(20)
            .active(true)
            .build();

        // Act
        productRepository.save(newProduct);
        entityManager.flush();

        // Assert
        Optional<Product> saved = productRepository.findById("PROD-005");
        assertTrue(saved.isPresent());
        assertEquals("Monitor", saved.get().getName());
    }

    @Test
    @DisplayName("Test 22: Find By ID - Should return product details")
    void testFindById() {
        // Act
        Optional<Product> product = productRepository.findById("PROD-001");

        // Assert
        assertTrue(product.isPresent());
        assertEquals("Laptop Pro 15", product.get().getName());
        assertEquals(BigDecimal.valueOf(999.99), product.get().getPrice());
    }

    @Test
    @DisplayName("Test 23: Find By ID - Should return empty for non-existent product")
    void testFindByIdNotFound() {
        // Act
        Optional<Product> product = productRepository.findById("PROD-999");

        // Assert
        assertFalse(product.isPresent());
    }

    @Test
    @DisplayName("Test 24: Find All - Should return all products")
    void testFindAll() {
        // Act
        List<Product> products = productRepository.findAll();

        // Assert
        assertNotNull(products);
        assertEquals(3, products.size());
    }

    @Test
    @DisplayName("Test 25: Delete Product - Should remove product from database")
    void testDeleteProduct() {
        // Act
        productRepository.delete(laptop);
        entityManager.flush();

        // Assert
        Optional<Product> deleted = productRepository.findById("PROD-001");
        assertFalse(deleted.isPresent());
    }
}
