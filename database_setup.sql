-- Database Setup for CCTV E-Commerce Application
-- Database: secucctv_db

-- ADMIN LOGIN CREDENTIALS:
-- URL: http://localhost:5173/admin/login
-- Email: admin@cctv.com
-- Password: admin123

-- The Product Entity creates two tables:
-- 1. products - Main product information
-- 2. product_features - Product features (one-to-many relationship)

-- Table structure will be automatically created by Spring Boot JPA
-- When you run the Spring Boot application, it will create:

-- CREATE TABLE products (
--     id BIGINT AUTO_INCREMENT PRIMARY KEY,
--     name VARCHAR(255) NOT NULL,
--     brand VARCHAR(255) NOT NULL,
--     price DOUBLE NOT NULL,
--     original_price DOUBLE,
--     category VARCHAR(255) NOT NULL,
--     short_desc VARCHAR(500),
--     image_url VARCHAR(255)
-- );

-- CREATE TABLE product_features (
--     product_id BIGINT NOT NULL,
--     feature VARCHAR(500),
--     FOREIGN KEY (product_id) REFERENCES products(id)
-- );

-- Insert default admin user (Password: admin123)
-- Note: In production, passwords should be hashed. This is for development only.
INSERT INTO users (full_name, email, password, role, created_at, is_active) VALUES
('Admin User', 'admin@cctv.com', 'admin123', 'ADMIN', NOW(), true);

-- Sample data to insert (you can add this through the Admin Panel or manually):

INSERT INTO products (name, brand, price, original_price, category, short_desc, image_url) VALUES
('EZVIZ H3C Wireless Smart Home AI Camera', 'EZVIZ', 129.99, 159.99, 'Wireless Camera', 'WiFi Pan-Tilt Camera, 1080p / 2MP, WiFi 2.4GHz', NULL),
('Hikvision DS-2CD1223G0E-I 2MP Fixed Bullet IP Camera', 'Hikvision', 89.99, NULL, 'IP Camera', '2MP Resolution, IR Night Vision, IP67 Weatherproof', NULL),
('Hikvision DS-7108HGHI-M1 8 Channel DVR', 'Hikvision', 229.99, NULL, 'DVR', '720p Recording, Motion Detection, 8 Channel', NULL);

-- To insert features for the products:
INSERT INTO product_features (product_id, feature) VALUES
(1, 'AI Human & Vehicle Detection'),
(1, 'Color Night Vision – see clearly at night'),
(1, 'Two-way audio – speak and listen remotely'),
(1, 'Weatherproof outdoor design'),
(2, '2MP Resolution'),
(2, 'IR Night Vision'),
(2, 'IP67 Weatherproof'),
(3, '8 Channel Recording'),
(3, 'Motion Detection'),
(3, '1080p Support');

-- Note: The best way to add products is through the Admin Panel at http://localhost:5173/admin
-- This ensures proper data validation and relationship management.

-- Additional tables for Inventory and Orders will be created automatically by Spring Boot JPA
-- When you run the application, these tables will be created:

-- CREATE TABLE inventory (
--     id BIGINT AUTO_INCREMENT PRIMARY KEY,
--     product_id BIGINT NOT NULL,
--     product_name VARCHAR(255) NOT NULL,
--     quantity INT NOT NULL,
--     reorder_level INT NOT NULL,
--     unit_price DOUBLE NOT NULL,
--     location VARCHAR(255),
--     last_updated TIMESTAMP NOT NULL
-- );

-- CREATE TABLE orders (
--     id BIGINT AUTO_INCREMENT PRIMARY KEY,
--     customer_name VARCHAR(255) NOT NULL,
--     customer_email VARCHAR(255) NOT NULL,
--     product_count INT NOT NULL,
--     total_amount DOUBLE NOT NULL,
--     status VARCHAR(50) NOT NULL,
--     order_date TIMESTAMP NOT NULL,
--     notes VARCHAR(500)
-- );

-- Sample inventory data (run after products are inserted)
-- INSERT INTO inventory (product_id, product_name, quantity, reorder_level, unit_price, location, last_updated) VALUES
-- (1, 'EZVIZ H3C Wireless Smart Home AI Camera', 50, 10, 129.99, 'Warehouse A-1', NOW()),
-- (2, 'Hikvision DS-2CD1223G0E-I 2MP Fixed Bullet IP Camera', 75, 15, 89.99, 'Warehouse A-2', NOW()),
-- (3, 'Hikvision DS-7108HGHI-M1 8 Channel DVR', 30, 5, 229.99, 'Warehouse B-1', NOW());

-- Sample orders data
-- INSERT INTO orders (customer_name, customer_email, product_count, total_amount, status, order_date) VALUES
-- ('John Doe', 'john@example.com', 2, 259.98, 'COMPLETED', DATE_SUB(NOW(), INTERVAL 5 DAY)),
-- ('Jane Smith', 'jane@example.com', 1, 129.99, 'PENDING', DATE_SUB(NOW(), INTERVAL 2 DAY)),
-- ('Bob Johnson', 'bob@example.com', 3, 449.97, 'COMPLETED', DATE_SUB(NOW(), INTERVAL 10 DAY));

