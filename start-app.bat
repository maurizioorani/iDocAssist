@echo off
echo Starting DocAssist Application...
echo ================================

cd /d C:\Users\marsk\Documents\Java\docassist

echo Cleaning and compiling application...
call mvnw.cmd clean compile

echo.
echo Starting Spring Boot application...
echo Press Ctrl+C to stop the application
echo.

call mvnw.cmd spring-boot:run

echo Application stopped.
pause
