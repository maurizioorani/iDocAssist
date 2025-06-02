@echo off
echo Testing Batch Excel Generation
echo ==============================
echo.

REM Check if application is running
echo Checking if application is running...
curl -s -X GET http://localhost:8080/api/invoice/health > nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo ERROR: Application is not running on localhost:8080
    echo Please start the application first with: start-app.bat
    pause
    exit /b 1
)
echo Application is running!
echo.

echo Testing batch processing with ALL invoice files...
echo This will create ONE consolidated Excel file with all invoices!
echo.

curl -w "HTTP Status: %%{http_code}\n" ^
  -X POST http://localhost:8080/api/invoice/process-batch-to-excel ^
  -F "files=@invoice_test/fattura-proforma-fac-simile-it-netto-750px.png" ^
  -F "files=@invoice_test/modello-fattura-it-mono-nero-750px.png" ^
  -F "files=@invoice_test/modello-fattura-it-bandiera-stati-uniti-750px.png" ^
  -F "files=@invoice_test/modello_fattura.pdf" ^
  -F "language=ita"

echo.
echo ========================================
echo Batch Excel generation test completed!
echo ========================================
echo.
echo Check the output directory for the consolidated Excel file.
echo.
pause
