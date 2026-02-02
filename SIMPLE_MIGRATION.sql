-- SIMPLE MIGRATION: Add 3 Image Columns to Products Table
-- Copy and paste this entire script in MySQL Workbench and click Execute

-- Step 1: Select your database (change if your database name is different)
USE secucctv_db;
-- If your database is named cctv_shop, use this instead:
-- USE cctv_shop;

-- Step 2: Check current table structure
SELECT 'Current table structure:' AS Info;
DESCRIBE products;

-- Step 3: Add image columns (using IF NOT EXISTS for safety)
SELECT 'Adding new image columns...' AS Info;

ALTER TABLE products 
ADD COLUMN IF NOT EXISTS image_url1 MEDIUMTEXT AFTER image_url;

ALTER TABLE products 
ADD COLUMN IF NOT EXISTS image_url2 MEDIUMTEXT AFTER image_url1;

ALTER TABLE products 
ADD COLUMN IF NOT EXISTS image_url3 MEDIUMTEXT AFTER image_url2;

-- Step 4: Migrate existing image data to image_url1
SELECT 'Migrating existing images...' AS Info;

UPDATE products 
SET image_url1 = image_url 
WHERE (image_url1 IS NULL OR image_url1 = '') 
  AND image_url IS NOT NULL 
  AND image_url != '';

-- Step 5: Verify new structure
SELECT 'New table structure:' AS Info;
DESCRIBE products;

-- Step 6: Check if any products have images
SELECT 'Products with images:' AS Info;
SELECT 
  id,
  name,
  CASE WHEN image_url1 IS NOT NULL AND image_url1 != '' THEN 'Yes' ELSE 'No' END AS has_img1,
  CASE WHEN image_url2 IS NOT NULL AND image_url2 != '' THEN 'Yes' ELSE 'No' END AS has_img2,
  CASE WHEN image_url3 IS NOT NULL AND image_url3 != '' THEN 'Yes' ELSE 'No' END AS has_img3
FROM products
LIMIT 10;

SELECT 'Migration completed successfully!' AS Status;
