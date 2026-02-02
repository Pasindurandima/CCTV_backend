@echo off
echo ====================================
echo    Database Schema Update
echo ====================================
echo.
echo This script will update your database schema to add the order_items table.
echo Make sure MySQL is running before continuing.
echo.
pause

echo.
echo Updating database schema...
echo.

REM Update this path to match your MySQL installation
set MYSQL_PATH="C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"

REM Update these credentials to match your database configuration
set DB_HOST=localhost
set DB_PORT=3306
set DB_NAME=cctv_db
set DB_USER=root

echo Enter your MySQL password:
%MYSQL_PATH% -h %DB_HOST% -P %DB_PORT% -u %DB_USER% -p %DB_NAME% < create_order_items_table.sql

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ====================================
    echo   Database updated successfully!
    echo ====================================
    echo.
    echo The order_items table has been created.
    echo You can now restart your backend server.
    echo.
) else (
    echo.
    echo ====================================
    echo   Database update failed!
    echo ====================================
    echo.
    echo Please check:
    echo 1. MySQL is running
    echo 2. Database credentials are correct
    echo 3. The database 'cctv_db' exists
    echo.
)

pause
