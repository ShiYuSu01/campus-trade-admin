@echo off
setlocal
cd /d "%~dp0.."
set "ROOT=%CD%"
set "MAVEN_REPO="

if exist "%ROOT%\.m2repo" (
    set "MAVEN_REPO=-Dmaven.repo.local=%ROOT%\.m2repo"
)

echo ============================================================
echo   Campus Trade Admin - Build WAR
echo ============================================================
echo.

where mvn >nul 2>nul
if errorlevel 1 (
    echo [ERROR] Maven was not found in PATH.
    echo Default Maven path on this PC: D:\jdk\apache-maven-3.9.16\bin
    pause
    exit /b 1
)

if defined MAVEN_REPO (
    call mvn "%MAVEN_REPO%" clean package -DskipTests
) else (
    call mvn clean package -DskipTests
)
if errorlevel 1 (
    echo.
    echo [ERROR] Build failed. Read the Maven output above.
    pause
    exit /b 1
)

echo.
echo [OK] WAR generated: target\campus-trade-admin.war
echo Next: run scripts\start-server.bat

if /I "%~1"=="noprompt" goto end
pause
:end
