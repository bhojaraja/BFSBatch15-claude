-- =====================================================
-- Product Catalog Management System - Sample Data
-- This file will be loaded on application startup
-- =====================================================

-- Insert Categories
INSERT INTO categories (id, name, description, active) VALUES
('ELEC-001', 'Electronics', 'Electronic devices and gadgets', true),
('CLOTH-001', 'Clothing', 'Apparel and fashion items', true),
('HOME-001', 'Home & Garden', 'Home and garden products', true),
('BOOK-001', 'Books', 'Educational and recreational books', true),
('SPORT-001', 'Sports', 'Sports equipment and accessories', true),
('HEALTH-001', 'Health & Beauty', 'Health and beauty products', true);

-- Insert Sample Products
INSERT INTO products (id, name, description, category_id, price, stock_quantity, active, created_by, updated_by) VALUES
('PROD-001', 'Laptop Pro 15', 'High-performance laptop with Intel i7 processor', 'ELEC-001', 1299.99, 50, true, 'admin', 'admin'),
('PROD-002', 'Wireless Mouse', 'Ergonomic wireless mouse with USB receiver', 'ELEC-001', 29.99, 200, true, 'admin', 'admin'),
('PROD-003', 'USB-C Cable', 'Fast charging and data transfer USB-C cable', 'ELEC-001', 12.99, 500, true, 'admin', 'admin'),
('PROD-004', 'Cotton T-Shirt', 'Comfortable 100% cotton t-shirt', 'CLOTH-001', 19.99, 150, true, 'admin', 'admin'),
('PROD-005', 'Denim Jeans', 'Classic fit blue denim jeans', 'CLOTH-001', 49.99, 100, true, 'admin', 'admin'),
('PROD-006', 'Yoga Mat', 'Non-slip yoga mat for exercise', 'SPORT-001', 24.99, 80, true, 'admin', 'admin'),
('PROD-007', 'Water Bottle', 'Insulated stainless steel water bottle', 'HOME-001', 34.99, 120, true, 'admin', 'admin'),
('PROD-008', 'Coffee Maker', 'Automatic drip coffee maker', 'HOME-001', 89.99, 45, true, 'admin', 'admin'),
('PROD-009', 'Java Programming', 'Core Java: Volume 1 - Fundamentals', 'BOOK-001', 39.99, 30, true, 'admin', 'admin'),
('PROD-010', 'Shampoo', 'Organic shampoo for all hair types', 'HEALTH-001', 14.99, 200, true, 'admin', 'admin');

-- Note: These queries may need adjustment based on your specific database syntax
-- For H2 Database: CURRENT_TIMESTAMP is used
-- For PostgreSQL: CURRENT_TIMESTAMP or NOW() can be used
