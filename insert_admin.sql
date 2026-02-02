-- Insert Admin User
-- Run this SQL script in your MySQL database

USE secucctv_db;

-- Insert default admin user
INSERT INTO users (full_name, email, password, role, created_at, is_active) 
VALUES ('Admin User', 'admin@cctv.com', 'admin123', 'ADMIN', NOW(), true);

-- Verify the admin user was created
SELECT * FROM users WHERE email = 'admin@cctv.com';
