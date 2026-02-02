-- Add three image URL columns to products table
-- This allows storing up to 3 images per product for slideshow functionality

-- Add columns only if they don't exist (MySQL 5.7+ syntax)
SET @dbname = 'cctv_shop';
SET @tablename = 'products';

-- Add image_url1
SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = 'image_url1';

SET @query = IF(@col_exists = 0,
    'ALTER TABLE products ADD COLUMN image_url1 MEDIUMTEXT AFTER image_url',
    'SELECT "Column image_url1 already exists" AS message');
PREPARE stmt FROM @query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Add image_url2
SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = 'image_url2';

SET @query = IF(@col_exists = 0,
    'ALTER TABLE products ADD COLUMN image_url2 MEDIUMTEXT AFTER image_url1',
    'SELECT "Column image_url2 already exists" AS message');
PREPARE stmt FROM @query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Add image_url3
SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = 'image_url3';

SET @query = IF(@col_exists = 0,
    'ALTER TABLE products ADD COLUMN image_url3 MEDIUMTEXT AFTER image_url2',
    'SELECT "Column image_url3 already exists" AS message');
PREPARE stmt FROM @query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Migrate existing data
UPDATE products 
SET image_url1 = image_url 
WHERE (image_url1 IS NULL OR image_url1 = '') 
  AND image_url IS NOT NULL 
  AND image_url != '';

SELECT 'Migration completed successfully!' AS message;
