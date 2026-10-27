package com.wipro.productcatalog.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * OpenApiConfig
 *
 * Swagger/OpenAPI 3.0 configuration for API documentation
 * Generates interactive API docs at: /swagger-ui.html
 * OpenAPI specification at: /v3/api-docs
 *
 * @Configuration: Spring bean configuration
 * Provides OpenAPI bean for Springdoc-OpenAPI to use
 */
@Configuration
public class OpenApiConfig {

    /**
     * Configure OpenAPI documentation
     *
     * Defines:
     * - API title, description, version
     * - Contact information
     * - License
     * - Server information
     * - External documentation
     *
     * @return - Configured OpenAPI object
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Product Catalog Management System")
                .version("1.0.0")
                .description("""
                    Product Catalog Management System API

                    A comprehensive REST API for managing product inventory with features for:
                    - Adding, updating, and deleting products
                    - Browsing and searching products
                    - Filtering products by category
                    - Managing inventory stock levels

                    ## Features
                    - **FR-1**: View all products (paginated)
                    - **FR-2**: Search products by category
                    - **FR-3**: Search products by ID
                    - **FR-4**: Add new products
                    - **FR-5**: Update product information
                    - **FR-6**: Delete products (soft delete)

                    ## Endpoints
                    All endpoints are under `/api/v1/products` base path
                    """)
                .contact(new Contact()
                    .name("Product Catalog Team")
                    .email("support@productcatalog.com")
                    .url("https://productcatalog.com"))
                .license(new License()
                    .name("MIT License")
                    .url("https://opensource.org/licenses/MIT")))
            .servers(List.of(
                new Server()
                    .url("http://localhost:8080")
                    .description("Development Server"),
                new Server()
                    .url("https://api.productcatalog.com")
                    .description("Production Server")))
            ;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:8080", "https://productcatalog.com"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }

}
