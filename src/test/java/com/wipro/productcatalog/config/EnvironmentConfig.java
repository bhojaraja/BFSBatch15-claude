package com.wipro.productcatalog.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * EnvironmentConfig - Load configuration from properties files
 * Supports multiple environments: dev, staging, prod
 */
public class EnvironmentConfig {
    private static final Logger logger = LoggerFactory.getLogger(EnvironmentConfig.class);
    private static Properties config;
    private static final String ENVIRONMENT = System.getProperty("env", "dev");

    static {
        loadConfiguration();
    }

    /**
     * Load configuration from properties file based on environment
     */
    private static void loadConfiguration() {
        config = new Properties();
        try {
            String configFile = String.format("src/test/resources/config/config-%s.properties", ENVIRONMENT);
            try (InputStream input = new FileInputStream(configFile)) {
                config.load(input);
                logger.info("Configuration loaded from: {}", configFile);
            } catch (IOException ex) {
                logger.warn("Config file not found: {}. Loading default configuration.", configFile);
                String defaultFile = "src/test/resources/config/config-dev.properties";
                try (InputStream input = new FileInputStream(defaultFile)) {
                    config.load(input);
                }
            }
        } catch (Exception e) {
            logger.error("Error loading configuration", e);
            throw new RuntimeException("Failed to load configuration", e);
        }
    }

    /**
     * Get base URL for API
     */
    public static String getBaseUrl() {
        return config.getProperty("api.baseUrl", "http://localhost").toString();
    }

    /**
     * Get base path for API endpoints
     */
    public static String getBasePath() {
        return config.getProperty("api.basePath", "/api/v1").toString();
    }

    /**
     * Get port number
     */
    public static int getPort() {
        String port = config.getProperty("api.port", "9090").toString();
        return Integer.parseInt(port);
    }

    /**
     * Get timeout in milliseconds
     */
    public static long getTimeout() {
        String timeout = config.getProperty("api.timeout", "5000").toString();
        return Long.parseLong(timeout);
    }

    /**
     * Get connection timeout
     */
    public static long getConnectionTimeout() {
        String timeout = config.getProperty("api.connectionTimeout", "3000").toString();
        return Long.parseLong(timeout);
    }

    /**
     * Get authentication type
     */
    public static String getAuthType() {
        return config.getProperty("api.authType", "none").toString();
    }

    /**
     * Get API key if authentication is enabled
     */
    public static String getApiKey() {
        return config.getProperty("api.apiKey", "").toString();
    }

    /**
     * Get environment name
     */
    public static String getEnvironment() {
        return ENVIRONMENT;
    }

    /**
     * Check if environment is production
     */
    public static boolean isProduction() {
        return "prod".equalsIgnoreCase(ENVIRONMENT);
    }

    /**
     * Get any configuration value as string
     */
    public static String getProperty(String key) {
        return config.getProperty(key, "").toString();
    }

    /**
     * Get any configuration value as string with default
     */
    public static String getProperty(String key, String defaultValue) {
        return config.getProperty(key, defaultValue).toString();
    }
}
