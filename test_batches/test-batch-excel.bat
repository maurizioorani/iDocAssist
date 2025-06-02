@echo off
echo Testing Batch Excel Generation
echo ==============================
echo.

REM Check if application is running
echo Checking if application is running...
curl -s -X GET http://localhost:8080/api/invoice/health > nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo ERROR: Application is not running on localhost:8080
    echo Please start the application first with: ..\start-app.bat
    pause
    exit /b 1
)
echo Application is running!
echo.

echo Testing batch processing with ALL invoice files...
echo This will create ONE consolidated Excel file with all invoices!
echo.

echo Using absolute paths for files and output...
set TIMESTAMP=%date:~-4,4%%date:~-7,2%%date:~-10,2%_%time:~0,2%%time:~3,2%%time:~6,2%
set TIMESTAMP=%TIMESTAMP: =0%

curl -v -X POST http://localhost:8080/api/invoice/process-batch-to-excel ^
  -F "files=@%CD%\..\invoice_test\fattura-proforma-fac-simile-it-netto-750px.png" ^
  -F "files=@%CD%\..\invoice_test\modello-fattura-it-mono-nero-750px.png" ^
  -F "files=@%CD%\..\invoice_test\modello-fattura-it-bandiera-stati-uniti-750px.png" ^
  -F "files=@%CD%\..\invoice_test\modello_fattura.pdf" ^
  -F "language=ita" ^
  -F "outputPath=%CD%\..\output\consolidated_batch_%TIMESTAMP%.xlsx"

echo.
echo ========================================
echo Batch Excel generation test completed!
echo ========================================
echo.
echo Check the output directory for the consolidated Excel file.
echo.
pause
