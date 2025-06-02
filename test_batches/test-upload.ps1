# Test file upload endpoints
Write-Host "Testing DocAssist File Upload Endpoints" -ForegroundColor Green
Write-Host "=======================================" -ForegroundColor Green

# Create a simple test file
$testContent = @"
INVOICE
Invoice Number: TEST-001
Date: 2024-06-01
Vendor: Test Company
Amount: €100.00
"@

$testFile = "test-invoice.txt"
Set-Content -Path $testFile -Value $testContent
Write-Host "Created test file: $testFile" -ForegroundColor Yellow

try {
    # Test OCR-only endpoint
    Write-Host "`nTesting OCR-only endpoint..." -ForegroundColor Yellow
    
    $uri = "http://localhost:8080/api/invoice/ocr-only"
    $fileBytes = [System.IO.File]::ReadAllBytes((Resolve-Path $testFile))
    $boundary = [System.Guid]::NewGuid().ToString()
    
    $bodyLines = @(
        "--$boundary",
        'Content-Disposition: form-data; name="file"; filename="test-invoice.txt"',
        'Content-Type: text/plain',
        '',
        [System.Text.Encoding]::UTF8.GetString($fileBytes),
        "--$boundary--"
    ) -join "`r`n"
    
    $response = Invoke-RestMethod -Uri $uri -Method Post -Body $bodyLines -ContentType "multipart/form-data; boundary=$boundary"
    
    Write-Host "Response:" -ForegroundColor Green
    $response | ConvertTo-Json -Depth 3
    
} catch {
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
} finally {
    # Clean up
    if (Test-Path $testFile) {
        Remove-Item $testFile
        Write-Host "`nCleaned up test file" -ForegroundColor Yellow
    }
}

Write-Host "`nFile upload test complete!" -ForegroundColor Green
