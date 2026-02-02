-- Create sales_history table to store completed order details
CREATE TABLE IF NOT EXISTS sales_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    customer_name VARCHAR(255) NOT NULL,
    customer_email VARCHAR(255) NOT NULL,
    customer_phone VARCHAR(255),
    shipping_address VARCHAR(500),
    product_count INT NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    payment_method VARCHAR(50),
    product_details VARCHAR(1000),
    order_date DATETIME NOT NULL,
    completed_date DATETIME NOT NULL,
    
    -- Add indexes for faster searching
    INDEX idx_customer_email (customer_email),
    INDEX idx_customer_name (customer_name),
    INDEX idx_customer_phone (customer_phone),
    INDEX idx_order_id (order_id),
    INDEX idx_completed_date (completed_date DESC),
    
    -- Foreign key to orders table
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);

-- Add comment to table
ALTER TABLE sales_history COMMENT = 'Stores completed order history for sales reporting and analytics';
