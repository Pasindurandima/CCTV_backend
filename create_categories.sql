-- Create categories table
CREATE TABLE IF NOT EXISTS categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(500),
    display_order INT NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Insert default categories
INSERT INTO categories (name, description, display_order, is_active, created_at, updated_at) VALUES
('Wireless Camera', 'WiFi and battery-powered security cameras', 1, TRUE, NOW(), NOW()),
('IP Camera', 'Network-based high-resolution security cameras', 2, TRUE, NOW(), NOW()),
('Analog CCTV', 'Traditional analog CCTV cameras and systems', 3, TRUE, NOW(), NOW()),
('DVR', 'Digital Video Recorders for analog camera systems', 4, TRUE, NOW(), NOW()),
('NVR', 'Network Video Recorders for IP camera systems', 5, TRUE, NOW(), NOW()),
('CCTV Package', 'Complete CCTV system packages', 6, TRUE, NOW(), NOW()),
('Hard Drive Memory', 'Storage solutions for surveillance systems', 7, TRUE, NOW(), NOW()),
('Cameras', 'General purpose security cameras', 8, TRUE, NOW(), NOW()),
('Mobile Accessories', 'Cables, power supplies, and mounting accessories', 9, TRUE, NOW(), NOW()),
('Power Bank', 'Portable power banks and charging solutions', 10, TRUE, NOW(), NOW());

-- Show created categories
SELECT * FROM categories ORDER BY display_order;
