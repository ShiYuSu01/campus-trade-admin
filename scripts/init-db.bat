@echo off
setlocal
set "MYSQL=C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"

if not exist "%MYSQL%" (
    where mysql >nul 2>nul
    if errorlevel 1 (
        echo [ERROR] mysql.exe was not found.
        pause
        exit /b 1
    )
    set "MYSQL=mysql"
)

echo ============================================================
echo   Initialize database: campus_trade
echo   Account: root / 123456
echo ============================================================
echo.

"%MYSQL%" -uroot -p123456 --default-character-set=utf8mb4 < "%~dp0..\sql\schema.sql"
if errorlevel 1 (
    echo [ERROR] Schema initialization failed. Check MySQL and the root password.
    pause
    exit /b 1
)

"%MYSQL%" -uroot -p123456 --default-character-set=utf8mb4 < "%~dp0..\sql\data.sql"
if errorlevel 1 (
    echo [ERROR] Demo data initialization failed.
    pause
    exit /b 1
)

echo.
echo [OK] Database campus_trade is ready.
echo Admin: admin / 123456
pause
