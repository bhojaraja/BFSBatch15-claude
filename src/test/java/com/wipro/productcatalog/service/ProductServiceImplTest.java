package com.wipro.productcatalog.service;

import com.wipro.productcatalog.dto.request.CreateProductRequest;
import com.wipro.productcatalog.dto.request.UpdateProductRequest;
import com.wipro.productcatalog.dto.response.ProductResponse;
import com.wipro.productcatalog.entity.Category;
import com.wipro.productcatalog.entity.Product;
import com.wipro.productcatalog.exception.DuplicateProductException;
import com.wipro.productcatalog.exception.ProductNotFoundException;
import com.wipro.productcatalog.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductServiceImpl Test Suite")
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private ProductServiceImpl productService;

    private Category category;
    private Product product;
    private CreateProductRequest createRequest;
    private UpdateProductRequest updateRequest;

    @BeforeEach
    void setUp() {
        category = Category.builder()
            .id("ELEC-001")
            .name("Electronics")
            .description("Electronic items")
            .active(true)
            .build();

        product = Product.builder()
            .id("PROD-001")
            .name("Laptop Pro 15")
            .description("High-performance laptop")
            .category(category)
            .price(BigDecimal.valueOf(999.99))
            .stockQuantity(50)
            .active(true)
            .version(1)
            .build();

        createRequest = CreateProductRequest.builder()
            .id("PROD-001")
            .name("Laptop Pro 15")
            .description("High-performance laptop")
            .categoryId("ELEC-001")
            .price(BigDecimal.valueOf(999.99))
            .stockQuantity(50)
            .build();

        updateRequest = UpdateProductRequest.builder()
            .name("Laptop Pro 16")
            .price(BigDecimal.valueOf(1199.99))
            .stockQuantity(45)
            .build();
    }

    // ==================== ADD PRODUCT TEST CASES ====================

    @Test
    @DisplayName("Test 1: Add Product Successfully - Should create and return new product")
    void testAddProductSuccess() {
        // Arrange
        when(productRepository.existsById("PROD-001")).thenReturn(false);
        when(productRepository.existsByName("Laptop Pro 15")).thenReturn(false);
        when(categoryService.getCategoryById("ELEC-001")).thenReturn(category);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Act
        ProductResponse response = productService.addProduct(createRequest);

        // Assert
        assertNotNull(response);
        assertEquals("PROD-001", response.getId());
        assertEquals("Laptop Pro 15", response.getName());
        assertEquals("Electronics", response.getCategory());
        assertEquals(BigDecimal.valueOf(999.99), response.getPrice());
        assertEquals(50, response.getStockQuantity());

        // Verify
        verify(productRepository, times(1)).existsById("PROD-001");
        verify(productRepository, times(1)).existsByName("Laptop Pro 15");
        verify(categoryService, times(1)).getCategoryById("ELEC-001");
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Test 2: Add Product with Duplicate ID - Should throw DuplicateProductException")
    void testAddProductDuplicateId() {
        // Arrange
        when(productRepository.existsById("PROD-001")).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateProductException.class, () -> {
            productService.addProduct(createRequest);
        });

        // Verify
        verify(productRepository, times(1)).existsById("PROD-001");
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Test 3: Add Product with Duplicate Name - Should throw DuplicateProductException")
    void testAddProductDuplicateName() {
        // Arrange
        when(productRepository.existsById("PROD-001")).thenReturn(false);
        when(productRepository.existsByName("Laptop Pro 15")).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateProductException.class, () -> {
            productService.addProduct(createRequest);
        });

        // Verify
        verify(productRepository, times(1)).existsById("PROD-001");
        verify(productRepository, times(1)).existsByName("Laptop Pro 15");
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Test 4: Add Product with Invalid Category - Should throw ProductNotFoundException")
    void testAddProductInvalidCategory() {
        // Arrange
        when(productRepository.existsById("PROD-001")).thenReturn(false);
        when(productRepository.existsByName("Laptop Pro 15")).thenReturn(false);
        when(categoryService.getCategoryById("INVALID-CAT")).thenReturn(null);

        CreateProductRequest requestWithInvalidCategory = CreateProductRequest.builder()
            .id("PROD-001")
            .name("Laptop Pro 15")
            .description("High-performance laptop")
            .categoryId("INVALID-CAT")
            .price(BigDecimal.valueOf(999.99))
            .stockQuantity(50)
            .build();

        // Act & Assert
        assertThrows(ProductNotFoundException.class, () -> {
            productService.addProduct(requestWithInvalidCategory);
        });

        // Verify
        verify(productRepository, never()).save(any(Product.class));
    }

    // ==================== GET PRODUCT TEST CASES ====================

    @Test
    @DisplayName("Test 5: Get Product by ID Successfully - Should return product details")
    void testGetProductByIdSuccess() {
        // Arrange
        when(productRepository.findById("PROD-001")).thenReturn(Optional.of(product));

        // Act
        ProductResponse response = productService.getProductById("PROD-001");

        // Assert
        assertNotNull(response);
        assertEquals("PROD-001", response.getId());
        assertEquals("Laptop Pro 15", response.getName());
        assertEquals("Electronics", response.getCategory());

        // Verify
        verify(productRepository, times(1)).findById("PROD-001");
    }

    @Test
    @DisplayName("Test 6: Get Non-existent Product - Should throw ProductNotFoundException")
    void testGetProductByIdNotFound() {
        // Arrange
        when(productRepository.findById("PROD-999")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProductNotFoundException.class, () -> {
            productService.getProductById("PROD-999");
        });

        // Verify
        verify(productRepository, times(1)).findById("PROD-999");
    }

    @Test
    @DisplayName("Test 7: Get All Products with Pagination - Should return paginated products")
    void testGetAllProductsSuccess() {
        // Arrange
        Product product2 = Product.builder()
            .id("PROD-002")
            .name("Wireless Mouse")
            .category(category)
            .price(BigDecimal.valueOf(29.99))
            .stockQuantity(100)
            .active(true)
            .build();

        List<Product> productList = Arrays.asList(product, product2);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> pageResult = new PageImpl<>(productList, pageable, 2);

        when(productRepository.findAll(pageable)).thenReturn(pageResult);

        // Act
        Page<ProductResponse> response = productService.getAllProducts(pageable);

        // Assert
        assertNotNull(response);
        assertEquals(2, response.getContent().size());
        assertEquals(0, response.getNumber());  // Page 0 (first page)
        assertEquals(10, response.getSize());

        // Verify
        verify(productRepository, times(1)).findAll(pageable);
    }

    // ==================== UPDATE PRODUCT TEST CASES ====================

    @Test
    @DisplayName("Test 8: Update Product Successfully - Should update and return modified product")
    void testUpdateProductSuccess() {
        // Arrange
        when(productRepository.findById("PROD-001")).thenReturn(Optional.of(product));
        when(productRepository.existsByNameAndIdNot("Laptop Pro 16", "PROD-001")).thenReturn(false);

        Product updatedProduct = Product.builder()
            .id("PROD-001")
            .name("Laptop Pro 16")
            .description("High-performance laptop")
            .category(category)
            .price(BigDecimal.valueOf(1199.99))
            .stockQuantity(45)
            .active(true)
            .version(2)
            .build();

        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        // Act
        ProductResponse response = productService.updateProduct("PROD-001", updateRequest);

        // Assert
        assertNotNull(response);
        assertEquals("PROD-001", response.getId());
        assertEquals("Laptop Pro 16", response.getName());
        assertEquals(BigDecimal.valueOf(1199.99), response.getPrice());
        assertEquals(45, response.getStockQuantity());

        // Verify
        verify(productRepository, times(1)).findById("PROD-001");
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Test 9: Update Non-existent Product - Should throw ProductNotFoundException")
    void testUpdateProductNotFound() {
        // Arrange
        when(productRepository.findById("PROD-999")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProductNotFoundException.class, () -> {
            productService.updateProduct("PROD-999", updateRequest);
        });

        // Verify
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Test 10: Update Product with Duplicate Name - Should throw DuplicateProductException")
    void testUpdateProductDuplicateName() {
        // Arrange
        when(productRepository.findById("PROD-001")).thenReturn(Optional.of(product));
        when(productRepository.existsByNameAndIdNot("Laptop Pro 16", "PROD-001")).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateProductException.class, () -> {
            productService.updateProduct("PROD-001", updateRequest);
        });

        // Verify
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Test 11: Update Product with Invalid Category - Should throw ProductNotFoundException")
    void testUpdateProductInvalidCategory() {
        // Arrange
        when(productRepository.findById("PROD-001")).thenReturn(Optional.of(product));

        UpdateProductRequest updateWithInvalidCategory = UpdateProductRequest.builder()
            .categoryId("INVALID-CAT")
            .build();

        when(categoryService.getCategoryById("INVALID-CAT")).thenReturn(null);

        // Act & Assert
        assertThrows(ProductNotFoundException.class, () -> {
            productService.updateProduct("PROD-001", updateWithInvalidCategory);
        });

        // Verify
        verify(productRepository, never()).save(any(Product.class));
    }

    // ==================== DELETE PRODUCT TEST CASES ====================

    @Test
    @DisplayName("Test 12: Delete Product Successfully - Should soft delete product")
    void testDeleteProductSuccess() {
        // Arrange
        when(productRepository.findById("PROD-001")).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Act
        productService.deleteProduct("PROD-001");

        // Assert - Verify soft delete was called
        verify(productRepository, times(1)).findById("PROD-001");
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Test 13: Delete Non-existent Product - Should throw ProductNotFoundException")
    void testDeleteProductNotFound() {
        // Arrange
        when(productRepository.findById("PROD-999")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProductNotFoundException.class, () -> {
            productService.deleteProduct("PROD-999");
        });

        // Verify
        verify(productRepository, never()).save(any(Product.class));
    }

    // ==================== GET PRODUCTS BY CATEGORY TEST CASES ====================

    @Test
    @DisplayName("Test 14: Get Products by Category - Should return list of products in category")
    void testGetProductsByCategorySuccess() {
        // Arrange
        Product product2 = Product.builder()
            .id("PROD-002")
            .name("Keyboard")
            .category(category)
            .price(BigDecimal.valueOf(79.99))
            .stockQuantity(30)
            .active(true)
            .build();

        List<Product> productList = Arrays.asList(product, product2);

        when(categoryService.getCategoryById("ELEC-001")).thenReturn(category);
        when(productRepository.findActiveByCategoryId("ELEC-001")).thenReturn(productList);

        // Act
        List<ProductResponse> response = productService.getProductsByCategory("ELEC-001");

        // Assert
        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals("PROD-001", response.get(0).getId());
        assertEquals("PROD-002", response.get(1).getId());

        // Verify
        verify(categoryService, times(1)).getCategoryById("ELEC-001");
        verify(productRepository, times(1)).findActiveByCategoryId("ELEC-001");
    }

    @Test
    @DisplayName("Test 15: Get Products by Invalid Category - Should throw ProductNotFoundException")
    void testGetProductsByCategoryNotFound() {
        // Arrange
        when(categoryService.getCategoryById("INVALID-CAT")).thenReturn(null);

        // Act & Assert
        assertThrows(ProductNotFoundException.class, () -> {
            productService.getProductsByCategory("INVALID-CAT");
        });

        // Verify
        verify(productRepository, never()).findActiveByCategoryId(anyString());
    }

    @Test
    @DisplayName("Test 16: Get Products by Category with Empty Results - Should return empty list")
    void testGetProductsByCategoryEmpty() {
        // Arrange
        when(categoryService.getCategoryById("ELEC-001")).thenReturn(category);
        when(productRepository.findActiveByCategoryId("ELEC-001")).thenReturn(Arrays.asList());

        // Act
        List<ProductResponse> response = productService.getProductsByCategory("ELEC-001");

        // Assert
        assertNotNull(response);
        assertEquals(0, response.size());

        // Verify
        verify(categoryService, times(1)).getCategoryById("ELEC-001");
        verify(productRepository, times(1)).findActiveByCategoryId("ELEC-001");
    }
}
