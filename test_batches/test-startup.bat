@echo off
echo.
echo ===================================
echo   DocAssist Application Test
echo ===================================
echo.

echo Step 1: Testing compilation...
cd /d "C:\Users\marsk\Documents\Java\docassist"
call mvnw.cmd compile -q

if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Compilation failed!
    pause
    exit /b 1
)

echo ✓ Compilation successful!
echo.

echo Step 2: Checking for conflicting controllers...
if exist "target\classes\com\marsk\docassist\controller\TestController.class" (
    echo WARNING: Found TestController.class - removing...
    del "target\classes\com\marsk\docassist\controller\TestController.class"
)

echo ✓ No conflicts detected
echo.

echo Step 3: Ready to start application!
echo Run the following command to start:
echo    cd .. && mvnw.cmd spring-boot:run
echo.
echo Or double-click ..\start-app.bat
echo.

pause
