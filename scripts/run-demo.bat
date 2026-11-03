@echo off
setlocal
call "%~dp0build.bat" noprompt
if errorlevel 1 exit /b 1
call "%~dp0start-server.bat"
