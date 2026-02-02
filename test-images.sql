-- Quick test to verify images are saving and fetching correctly
-- Run this in MySQL Workbench

USE secucctv_db;

-- Check if columns exist
SHOW COLUMNS FROM products LIKE 'image_url%';

-- Check products with images
SELECT 
  id,
  name,
  CASE WHEN image_url IS NOT NULL AND image_url != '' THEN 'Yes' ELSE 'No' END AS has_old_img,
  CASE WHEN image_url1 IS NOT NULL AND image_url1 != '' THEN 'Yes' ELSE 'No' END AS has_img1,
  CASE WHEN image_url2 IS NOT NULL AND image_url2 != '' THEN 'Yes' ELSE 'No' END AS has_img2,
  CASE WHEN image_url3 IS NOT NULL AND image_url3 != '' THEN 'Yes' ELSE 'No' END AS has_img3,
  LENGTH(image_url1) as img1_bytes,
  LENGTH(image_url2) as img2_bytes,
  LENGTH(image_url3) as img3_bytes
FROM products
ORDER BY id DESC
LIMIT 5;

-- If you see columns don't exist, run:
-- ALTER TABLE products ADD COLUMN IF NOT EXISTS image_url1 MEDIUMTEXT;
-- ALTER TABLE products ADD COLUMN IF NOT EXISTS image_url2 MEDIUMTEXT;
-- ALTER TABLE products ADD COLUMN IF NOT EXISTS image_url3 MEDIUMTEXT;
