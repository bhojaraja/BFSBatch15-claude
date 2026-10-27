-- =====================================================
-- Product Catalog Management System - Database Schema
-- Database: H2 (Development) / PostgreSQL (Production)
-- =====================================================

-- Categories Table
CREATE TABLE IF NOT EXISTS categories (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    INDEX idx_name (name),
    INDEX idx_active (active)
);

-- Products Table
CREATE TABLE IF NOT EXISTS products (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    category_id VARCHAR(50) NOT NULL,
    price DECIMAL(10, 2) NOT NULL CHECK (price > 0),
    stock_quantity INT NOT NULL DEFAULT 0 CHECK (stock_quantity >= 0),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    image_url VARCHAR(500),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version INT NOT NULL DEFAULT 1,

    CONSTRAINT fk_product_category
        FOREIGN KEY (category_id)
        REFERENCES categories(id)
        ON DELETE RESTRICT,

    INDEX idx_category_id (category_id),
    INDEX idx_active (active),
    INDEX idx_created_at (created_at),
    INDEX idx_name (name)
);

-- Product Audit Table (Optional - for tracking changes)
CREATE TABLE IF NOT EXISTS product_audit (
    id INT AUTO_INCREMENT PRIMARY KEY,
    product_id VARCHAR(50) NOT NULL,
    action VARCHAR(10) NOT NULL,
    changes LONGTEXT,
    changed_by VARCHAR(255),
    changed_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_audit_product
        FOREIGN KEY (product_id)
        REFERENCES products(id)
        ON DELETE CASCADE,

    INDEX idx_product_id (product_id),
    INDEX idx_changed_at (changed_at)
);
