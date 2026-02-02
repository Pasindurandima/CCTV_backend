@echo off
echo ========================================
echo Running Database Migration
echo ========================================
echo.

REM Try common MySQL installation paths
set MYSQL_PATH=""
if exist "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" set MYSQL_PATH="C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"
if exist "C:\Program Files\MySQL\MySQL Server 5.7\bin\mysql.exe" set MYSQL_PATH="C:\Program Files\MySQL\MySQL Server 5.7\bin\mysql.exe"
if exist "C:\Program Files (x86)\MySQL\MySQL Server 8.0\bin\mysql.exe" set MYSQL_PATH="C:\Program Files (x86)\MySQL\MySQL Server 8.0\bin\mysql.exe"
if exist "C:\xampp\mysql\bin\mysql.exe" set MYSQL_PATH="C:\xampp\mysql\bin\mysql.exe"

if %MYSQL_PATH%=="" (
    echo ERROR: MySQL not found in common installation paths
    echo.
    echo Please run the SQL script manually:
    echo 1. Open MySQL Workbench
    echo 2. Connect to your database
    echo 3. Open file: add_multiple_images.sql
    echo 4. Click Execute (lightning bolt icon)
    echo.
    echo Or use command line:
    echo mysql -u root -p cctv_shop ^< add_multiple_images.sql
    echo.
    pause
    exit /b 1
)

echo Found MySQL at: %MYSQL_PATH%
echo.
echo Enter MySQL root password when prompted...
%MYSQL_PATH% -u root -p cctv_shop < add_multiple_images.sql

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo Migration completed successfully!
    echo ========================================
) else (
    echo.
    echo ========================================
    echo Migration failed!
    echo ========================================
    echo Please run manually in MySQL Workbench
)

echo.
pause
