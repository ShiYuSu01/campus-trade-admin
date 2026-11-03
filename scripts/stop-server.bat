@echo off
setlocal
for %%I in ("%~dp0..") do set "ROOT=%%~fI"
set "CATALINA_HOME=D:\apache-tomcat-11.0.22"
set "CATALINA_BASE=%ROOT%\.tomcat"
set "JAVA_HOME=D:\Program Files\Java\jdk-25.0.4.1"
set "JRE_HOME=%JAVA_HOME%"

if not exist "%CATALINA_BASE%\bin\catalina-local.bat" (
    echo [INFO] Local Tomcat base is not initialized yet.
    pause
    exit /b 1
)

call "%CATALINA_BASE%\bin\catalina-local.bat" stop
echo.
echo Stop command sent.
pause
