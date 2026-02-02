@echo off
echo ============================================
echo   Quick Backend Startup Guide
echo ============================================
echo.
echo IMPORTANT: Follow these steps in order:
echo.
echo Step 1: Run Database Migration
echo -------------------------------
echo Open MySQL Workbench or Command Prompt and run:
echo    USE cctv_shop;
echo    ALTER TABLE products ADD COLUMN IF NOT EXISTS image_url1 MEDIUMTEXT AFTER image_url;
echo    ALTER TABLE products ADD COLUMN IF NOT EXISTS image_url2 MEDIUMTEXT AFTER image_url1;
echo    ALTER TABLE products ADD COLUMN IF NOT EXISTS image_url3 MEDIUMTEXT AFTER image_url2;
echo    UPDATE products SET image_url1 = image_url WHERE image_url IS NOT NULL AND image_url != '';
echo.
echo Step 2: Start Backend
echo -------------------------------
echo Run this command:
echo    mvnw spring-boot:run
echo.
pause
