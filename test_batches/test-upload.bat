@echo off
echo.
echo Testing DocAssist File Upload Endpoints
echo =======================================
echo.

echo Creating test file...
echo INVOICE TEST > test-simple.txt
echo Invoice Number: TEST-001 >> test-simple.txt
echo Amount: 100.00 >> test-simple.txt
echo.

echo Testing OCR-only endpoint with PowerShell...
powershell -Command "$response = Invoke-RestMethod -Uri 'http://localhost:8080/api/invoice/ocr-only' -Method Post -InFile 'test-simple.txt' -ContentType 'multipart/form-data'; $response | ConvertTo-Json -Depth 3"

echo.
echo Cleaning up test file...
del test-simple.txt

echo.
echo File upload test complete!
pause
