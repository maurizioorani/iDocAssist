@echo off
echo DocAssist Test Suite Runner
echo ==========================
echo.

:menu
cls
echo Select a test to run:
echo ---------------------
echo 1. Test API Endpoints
echo 2. Test File Uploads
echo 3. Test Excel Generation
echo 4. Test Batch Excel Generation
echo 5. Test Application Startup
echo 6. View Test Summary
echo 7. Start Application
echo 8. Exit
echo.
choice /C 12345678 /N /M "Enter your choice (1-8): "

if errorlevel 8 goto :exit
if errorlevel 7 goto :startapp
if errorlevel 6 goto :testsummary
if errorlevel 5 goto :teststartup
if errorlevel 4 goto :testbatchexcel
if errorlevel 3 goto :testexcel
if errorlevel 2 goto :testfileupload
if errorlevel 1 goto :testendpoints

:testendpoints
cls
echo Running API Endpoints Test...
call test-endpoints.bat
pause
goto menu

:testfileupload
cls
echo Running File Upload Test...
call test-file-uploads.bat
pause
goto menu

:testexcel
cls
echo Running Excel Generation Test...
call test-excel-generation.bat
goto menu

:testbatchexcel
cls
echo Running Batch Excel Generation Test...
call test-batch-excel.bat
goto menu

:teststartup
cls
echo Running Application Startup Test...
call test-startup.bat
goto menu

:testsummary
cls
echo Displaying Test Summary...
call test-summary.bat
pause
goto menu

:startapp
cls
echo Starting DocAssist Application...
cd ..
start start-app.bat
cd test_batches
echo Application started in a new window.
pause
goto menu

:exit
echo.
echo Thank you for using DocAssist Test Suite Runner!
exit /b 0
