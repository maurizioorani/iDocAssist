@echo off
echo Testing DocAssist Endpoints
echo ============================
echo.

REM Test GET endpoints first
echo ===================
echo Testing GET Endpoints
echo ===================
echo.

echo Testing root API endpoint (GET /api/invoice):
curl -X GET http://localhost:8080/api/invoice
echo.
echo.

echo Testing health endpoint (GET /api/invoice/health):
curl -X GET http://localhost:8080/api/invoice/health
echo.
echo.

echo Testing simple test controller (GET /api/test/hello):
curl -X GET http://localhost:8080/api/test/hello
echo.
echo.

echo Testing test health endpoint (GET /api/test/health):
curl -X GET http://localhost:8080/api/test/health
echo.
echo.

REM Test file upload endpoints with actual invoice files
echo ===========================
echo Testing File Upload Endpoints
echo ===========================
echo.

echo Testing OCR-only endpoint with PNG invoice:
curl -X POST http://localhost:8080/api/invoice/ocr-only ^
  -F "file=@invoice_test/fattura-proforma-fac-simile-it-netto-750px.png" ^
  -F "language=ita"
echo.
echo.

echo Testing OCR-only endpoint with mono PNG invoice:
curl -X POST http://localhost:8080/api/invoice/ocr-only ^
  -F "file=@invoice_test/modello-fattura-it-mono-nero-750px.png" ^
  -F "language=ita"
echo.
echo.

echo Testing OCR-only endpoint with US flag PNG invoice:
curl -X POST http://localhost:8080/api/invoice/ocr-only ^
  -F "file=@invoice_test/modello-fattura-it-bandiera-stati-uniti-750px.png" ^
  -F "language=ita"
echo.
echo.

echo Testing OCR-only endpoint with PDF invoice:
curl -X POST http://localhost:8080/api/invoice/ocr-only ^
  -F "file=@invoice_test/modello_fattura.pdf" ^
  -F "language=ita"
echo.
echo.

echo Testing full invoice processing with PNG file:
curl -X POST http://localhost:8080/api/invoice/process ^
  -F "file=@invoice_test/fattura-proforma-fac-simile-it-netto-750px.png" ^
  -F "language=ita"
echo.
echo.

echo Testing Excel generation with PNG file:
curl -X POST http://localhost:8080/api/invoice/process-to-excel ^
  -F "file=@invoice_test/fattura-proforma-fac-simile-it-netto-750px.png" ^
  -F "language=ita"
echo.
echo.

echo Testing full invoice processing with PDF file:
curl -X POST http://localhost:8080/api/invoice/process ^
  -F "file=@invoice_test/modello_fattura.pdf" ^
  -F "language=ita"
echo.
echo.

echo ======================
echo All endpoint tests completed!
echo ======================
