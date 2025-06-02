# DocAssist Test Scripts

This directory contains batch scripts for testing various aspects of the DocAssist application.

## Running Tests

The easiest way to run all tests is to use the provided menu-driven interface:

```
run-all-tests.bat
```

This script provides a menu to select and run individual tests.

## Available Test Scripts

- **test-endpoints.bat** - Tests all API endpoints
- **test-file-uploads.bat** - Tests file upload functionality with different file types
- **test-excel-generation.bat** - Tests Excel generation for individual invoices
- **test-batch-excel.bat** - Tests batch Excel generation with multiple invoices
- **test-startup.bat** - Tests application startup and environment
- **test-summary.bat** - Shows a summary of all available tests

## Running Individual Tests

You can also run each test individually. Make sure the application is running before executing these tests:

1. Start the application from the root directory:
   ```
   cd ..
   start-app.bat
   ```

2. Run any of the test scripts:
   ```
   test-batch-excel.bat
   ```

## Test Files

The tests use sample invoice files located in the `../invoice_test/` directory:

- `fattura-proforma-fac-simile-it-netto-750px.png` - Sample Italian proforma invoice
- `modello-fattura-it-mono-nero-750px.png` - Black and white Italian invoice template
- `modello-fattura-it-bandiera-stati-uniti-750px.png` - Italian invoice with US flag
- `modello_fattura.pdf` - PDF invoice sample

## Output Files

Generated Excel files are stored in the `../output/` directory.

## Note

These test scripts are designed to be run from the `test_batches` directory. All paths are relative to this directory.
