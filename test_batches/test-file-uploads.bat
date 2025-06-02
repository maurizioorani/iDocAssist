@echo off
echo Testing DocAssist File Upload Endpoints
echo =======================================
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

REM Check if test files exist
if not exist "..\invoice_test\fattura-proforma-fac-simile-it-netto-750px.png" (
    echo ERROR: Test files not found in invoice_test directory
    pause
    exit /b 1
)

echo Testing OCR-only endpoints:
echo ===========================
echo.

echo [1/4] Testing OCR with Italian proforma invoice (PNG)...
curl -w "HTTP Status: %%{http_code}\n" ^
  -X POST http://localhost:8080/api/invoice/ocr-only ^
  -F "file=@../invoice_test/fattura-proforma-fac-simile-it-netto-750px.png" ^
  -F "language=ita"
echo.
echo ----------------------------------------
echo.

echo [2/4] Testing OCR with mono black invoice (PNG)...
curl -w "HTTP Status: %%{http_code}\n" ^
  -X POST http://localhost:8080/api/invoice/ocr-only ^
  -F "file=@../invoice_test/modello-fattura-it-mono-nero-750px.png" ^
  -F "language=ita"
echo.
echo ----------------------------------------
echo.

echo [3/4] Testing OCR with US flag invoice (PNG)...
curl -w "HTTP Status: %%{http_code}\n" ^
  -X POST http://localhost:8080/api/invoice/ocr-only ^
  -F "file=@../invoice_test/modello-fattura-it-bandiera-stati-uniti-750px.png" ^
  -F "language=ita"
echo.
echo ----------------------------------------
echo.

echo [4/4] Testing OCR with PDF invoice...
curl -w "HTTP Status: %%{http_code}\n" ^
  -X POST http://localhost:8080/api/invoice/ocr-only ^
  -F "file=@../invoice_test/modello_fattura.pdf" ^
  -F "language=ita"
echo.
echo ----------------------------------------
echo.

echo Testing full invoice processing:
echo ================================
echo.

echo [1/2] Full processing with PNG file...
curl -w "HTTP Status: %%{http_code}\n" ^
  -X POST http://localhost:8080/api/invoice/process ^
  -F "file=@../invoice_test/fattura-proforma-fac-simile-it-netto-750px.png" ^
  -F "language=ita"
echo.
echo ----------------------------------------
echo.

echo [2/2] Full processing with PDF file...
curl -w "HTTP Status: %%{http_code}\n" ^
  -X POST http://localhost:8080/api/invoice/process ^
  -F "file=@../invoice_test/modello_fattura.pdf" ^
  -F "language=ita"
echo.
echo ----------------------------------------
echo.

echo Testing Excel generation:
echo =========================
echo.

echo Generating Excel from PNG invoice...
curl -w "HTTP Status: %%{http_code}\n" ^
  -X POST http://localhost:8080/api/invoice/process-to-excel ^
  -F "file=@../invoice_test/fattura-proforma-fac-simile-it-netto-750px.png" ^
  -F "language=ita"
echo.
echo ----------------------------------------
echo.

echo Testing with English language (should work for most invoices):
echo =============================================================
echo.

echo OCR with English language...
curl -w "HTTP Status: %%{http_code}\n" ^
  -X POST http://localhost:8080/api/invoice/ocr-only ^
  -F "file=@../invoice_test/fattura-proforma-fac-simile-it-netto-750px.png" ^
  -F "language=eng"
echo.
echo ----------------------------------------
echo.

echo ========================================
echo File upload tests completed!
echo ========================================
echo.
echo Note: If Ollama is not running, full processing will fail but OCR should work.
echo To start Ollama: ollama serve
echo To pull required model: ollama pull llama3.2
echo.
pause
