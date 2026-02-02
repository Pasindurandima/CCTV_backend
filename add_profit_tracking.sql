-- Add cost_price column to products table for profit calculation
ALTER TABLE products ADD COLUMN cost_price DECIMAL(10, 2) DEFAULT 0.00 COMMENT 'Cost/wholesale price for profit calculation';

-- Update existing products with default cost price (you can update these manually later)
UPDATE products SET cost_price = price * 0.6 WHERE cost_price IS NULL OR cost_price = 0;

-- Add profit columns to sales_history table
ALTER TABLE sales_history ADD COLUMN total_profit DECIMAL(10, 2) DEFAULT 0.00 COMMENT 'Total profit from sale';
ALTER TABLE sales_history ADD COLUMN total_cost DECIMAL(10, 2) DEFAULT 0.00 COMMENT 'Total cost of products sold';

-- Add index for profit queries
CREATE INDEX idx_sales_profit ON sales_history(total_profit);
CREATE INDEX idx_sales_date ON sales_history(completed_date);
