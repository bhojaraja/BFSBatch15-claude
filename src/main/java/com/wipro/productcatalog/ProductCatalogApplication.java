package com.wipro.productcatalog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot Application entry point for Product Catalog Management System
 *
 * Configuration:
 * - Java 17
 * - Spring Boot 3.1.x
 * - Spring Web
 * - Spring Data JPA
 * - H2 Database
 * - Validation
 * - Lombok
 */
@SpringBootApplication
public class ProductCatalogApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductCatalogApplication.class, args);
    }

}
