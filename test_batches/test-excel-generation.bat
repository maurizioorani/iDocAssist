@echo off
echo Testing Excel Generation with All Invoice Files
echo ===============================================
echo.

REM Check if application is running
curl -s -X GET http://localhost:8080/api/invoice/health > nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo ERROR: Application is not running on localhost:8080
    echo Please start the application first with: ..\start-app.bat
    pause
    exit /b 1
)

echo Application is running, proceeding with Excel generation tests...
echo.

REM Create output directory if it doesn't exist
if not exist "..\output" mkdir ..\output

echo Processing all invoice files to Excel format:
echo ============================================
echo.

echo [1/4] Processing Italian proforma invoice (PNG)...
echo Creating file with absolute path...
curl -v -X POST http://localhost:8080/api/invoice/process-to-excel ^
  -F "file=@%CD%\..\invoice_test\fattura-proforma-fac-simile-it-netto-750px.png" ^
  -F "language=ita" ^
  -F "outputPath=%CD%\..\output\proforma_invoice.xlsx" 2>&1
echo.
echo ----------------------------------------
echo.

echo [2/4] Processing mono black invoice (PNG)...
curl -w "HTTP Status: %%{http_code}\n" ^
  -X POST http://localhost:8080/api/invoice/process-to-excel ^
  -F "file=@../invoice_test/modello-fattura-it-mono-nero-750px.png" ^
  -F "language=ita" ^
  -F "outputPath=../output/mono_invoice.xlsx"
echo.
echo ----------------------------------------
echo.

echo [3/4] Processing US flag invoice (PNG)...
curl -w "HTTP Status: %%{http_code}\n" ^
  -X POST http://localhost:8080/api/invoice/process-to-excel ^
  -F "file=@../invoice_test/modello-fattura-it-bandiera-stati-uniti-750px.png" ^
  -F "language=ita" ^
  -F "outputPath=../output/us_flag_invoice.xlsx"
echo.
echo ----------------------------------------
echo.

echo [4/4] Processing PDF invoice...
curl -w "HTTP Status: %%{http_code}\n" ^
  -X POST http://localhost:8080/api/invoice/process-to-excel ^
  -F "file=@../invoice_test/modello_fattura.pdf" ^
  -F "language=ita" ^
  -F "outputPath=../output/pdf_invoice.xlsx"
echo.
echo ----------------------------------------
echo.

echo Checking generated Excel files:
echo ===============================
dir ..\output\*.xlsx
echo.

echo Excel generation test completed!
echo Generated files are in the '..\output' directory.
echo.

echo To open generated Excel files:
echo ------------------------------
echo start ..\output\proforma_invoice.xlsx
echo start ..\output\mono_invoice.xlsx  
echo start ..\output\us_flag_invoice.xlsx
echo start ..\output\pdf_invoice.xlsx
echo.

pause
