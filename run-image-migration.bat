@echo off
echo Running database migration to add multiple image columns...
echo Please enter your MySQL root password when prompted
mysql -u root -p cctv_shop < add_multiple_images.sql
if %ERRORLEVEL% EQU 0 (
    echo Migration completed successfully!
) else (
    echo Migration failed. Please check the error above.
)
pause
