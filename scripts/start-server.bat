@echo off
setlocal
for %%I in ("%~dp0..") do set "ROOT=%%~fI"
set "CATALINA_HOME=D:\apache-tomcat-11.0.22"
set "CATALINA_BASE=%ROOT%\.tomcat"
set "JAVA_HOME=D:\Program Files\Java\jdk-25.0.4.1"
set "JRE_HOME=%JAVA_HOME%"
set "CATALINA_OPTS=-Dfile.encoding=UTF-8"

if not exist "%CATALINA_HOME%\bin\catalina.bat" (
    echo [ERROR] Tomcat 11 not found: %CATALINA_HOME%
    echo Edit CATALINA_HOME in scripts\start-server.bat.
    pause
    exit /b 1
)
if not exist "%JAVA_HOME%\bin\java.exe" (
    echo [ERROR] JDK not found: %JAVA_HOME%
    echo Edit JAVA_HOME in scripts\start-server.bat.
    pause
    exit /b 1
)

if not exist "%ROOT%\target\campus-trade-admin.war" (
    echo [INFO] WAR not found. Building first...
    call "%~dp0build.bat" noprompt
    if errorlevel 1 exit /b 1
)

echo ============================================================
echo   Campus Trade Admin - Tomcat 11
echo   URL:  http://localhost:8080/campus-trade-admin/
echo   User: admin / 123456
echo ============================================================
echo.

powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0init-tomcat.ps1" -TomcatHome "%CATALINA_HOME%" -JavaHome "%JAVA_HOME%"
if errorlevel 1 (
    echo [ERROR] Failed to initialize the local Tomcat base.
    pause
    exit /b 1
)

call "%CATALINA_BASE%\bin\catalina-local.bat" run
echo.
echo Tomcat stopped.
pause
