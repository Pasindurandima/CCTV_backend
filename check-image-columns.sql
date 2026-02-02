-- Check if the image columns exist
SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'cctv_shop'
  AND TABLE_NAME = 'products'
  AND COLUMN_NAME IN ('image_url', 'image_url1', 'image_url2', 'image_url3')
ORDER BY COLUMN_NAME;
