package com.marsk.docassist.controller;

import com.marsk.docassist.model.InvoiceData;
import com.marsk.docassist.service.ExcelService;
import com.marsk.docassist.service.OcrService;
import com.marsk.docassist.service.OllamaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST controller for invoice processing operations.
 * Provides endpoints for uploading documents, extracting invoice data,
 * and generating Excel reports.
 */
@RestController
@RequestMapping("/api/invoice")
@CrossOrigin(origins = "*") // Allow frontend to access the API
public class InvoiceController {
    private static final Logger logger = LoggerFactory.getLogger(InvoiceController.class);
    
    private final OcrService ocrService;
    private final OllamaService ollamaService;
    private final ExcelService excelService;

    public InvoiceController(OcrService ocrService, OllamaService ollamaService, ExcelService excelService) {
        this.ocrService = ocrService;
        this.ollamaService = ollamaService;
        this.excelService = excelService;
    }

    /**
     * Processes an uploaded document to extract invoice data.
     * 
     * @param file The uploaded document file (PDF, image, etc.)
     * @param language OCR language (optional, defaults to "eng")
     * @return JSON response with extracted invoice data
     */
    @PostMapping(value = "/process", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> processInvoice(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "language", defaultValue = "eng") String language) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (file.isEmpty()) {
                response.put("success", false);
                response.put("error", "No file uploaded");
                return ResponseEntity.badRequest().body(response);
            }

            String filename = file.getOriginalFilename();
            logger.info("Processing invoice file: {} (size: {} bytes)", filename, file.getSize());

            // Step 1: Perform OCR on the uploaded file
            logger.info("Starting OCR processing for file: {}", filename);
            String ocrText = ocrService.performOcr(file, language);
            
            if (ocrText == null || ocrText.trim().isEmpty()) {
                response.put("success", false);
                response.put("error", "No text could be extracted from the document");
                return ResponseEntity.ok(response);
            }

            // Step 2: Extract invoice data using Ollama AI service
            logger.info("Starting invoice data extraction for file: {}", filename);
            OllamaService.InvoiceExtractionResult result = ollamaService.processInvoice(ocrText, filename);
            
            // Build response
            response.put("success", result.isSuccess());
            response.put("filename", filename);
            response.put("ocrTextLength", ocrText.length());
            response.put("confidence", result.getConfidence());
            response.put("invoiceData", result.getInvoiceData());
            
            if (!result.isSuccess() && result.getErrorMessage() != null) {
                response.put("error", result.getErrorMessage());
            }

            logger.info("Successfully processed invoice file: {} with confidence: {}", 
                       filename, result.getConfidence());
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error processing invoice file: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("error", "Processing failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Processes an uploaded document and generates an Excel file.
     * 
     * @param file The uploaded document file
     * @param language OCR language (optional, defaults to "eng")
     * @param outputPath Output path for the Excel file (optional)
     * @return JSON response with processing result and Excel file path
     */
    @PostMapping(value = "/process-to-excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> processInvoiceToExcel(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "language", defaultValue = "eng") String language,
            @RequestParam(value = "outputPath", required = false) String outputPath) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (file.isEmpty()) {
                response.put("success", false);
                response.put("error", "No file uploaded");
                return ResponseEntity.badRequest().body(response);
            }

            String filename = file.getOriginalFilename();
            logger.info("Processing invoice file to Excel: {}", filename);

            // Step 1: Perform OCR
            String ocrText = ocrService.performOcr(file, language);
            
            if (ocrText == null || ocrText.trim().isEmpty()) {
                response.put("success", false);
                response.put("error", "No text could be extracted from the document");
                return ResponseEntity.ok(response);
            }

            // Step 2: Extract invoice data
            OllamaService.InvoiceExtractionResult result = ollamaService.processInvoice(ocrText, filename);
            
            if (!result.isSuccess()) {
                response.put("success", false);
                response.put("error", "Invoice extraction failed: " + result.getErrorMessage());
                return ResponseEntity.ok(response);
            }

            // Step 3: Generate Excel file
            String excelPath = outputPath;
            if (excelPath == null || excelPath.trim().isEmpty()) {
                // Generate default path
                String baseName = filename != null ? filename.replaceAll("\\.[^.]+$", "") : "invoice";
                excelPath = "output/" + baseName + "_extracted.xlsx";
            }
            
            // Ensure output directory exists
            java.nio.file.Files.createDirectories(Paths.get(excelPath).getParent());
            
            excelService.createExcelFile(result.getInvoiceData(), excelPath);
            
            // Build response
            response.put("success", true);
            response.put("filename", filename);
            response.put("excelPath", excelPath);
            response.put("confidence", result.getConfidence());
            response.put("invoiceData", result.getInvoiceData());

            logger.info("Successfully processed invoice file to Excel: {} -> {}", filename, excelPath);
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error processing invoice file to Excel: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("error", "Processing failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Processes multiple uploaded documents and generates a single consolidated Excel file.
     * 
     * @param files Array of uploaded document files
     * @param language OCR language (optional, defaults to "eng")
     * @param outputPath Output path for the Excel file (optional)
     * @return JSON response with processing result and Excel file path
     */
    @PostMapping(value = "/process-batch-to-excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> processBatchInvoicesToExcel(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam(value = "language", defaultValue = "eng") String language,
            @RequestParam(value = "outputPath", required = false) String outputPath) {
        
        Map<String, Object> response = new HashMap<>();
        List<InvoiceData> allInvoiceData = new ArrayList<>();
        List<String> processedFiles = new ArrayList<>();
        List<String> failedFiles = new ArrayList<>();
        
        try {
            if (files == null || files.length == 0) {
                response.put("success", false);
                response.put("error", "No files uploaded");
                return ResponseEntity.badRequest().body(response);
            }

            logger.info("Processing batch of {} files for consolidated Excel", files.length);

            // Process each file
            for (MultipartFile file : files) {
                if (file.isEmpty()) {
                    failedFiles.add(file.getOriginalFilename() + " (empty file)");
                    continue;
                }

                try {
                    String filename = file.getOriginalFilename();
                    logger.info("Processing file {} in batch", filename);

                    // Step 1: Perform OCR
                    String ocrText = ocrService.performOcr(file, language);
                    
                    if (ocrText == null || ocrText.trim().isEmpty()) {
                        failedFiles.add(filename + " (no text extracted)");
                        continue;
                    }

                    // Step 2: Extract invoice data
                    OllamaService.InvoiceExtractionResult result = ollamaService.processInvoice(ocrText, filename);
                    
                    if (result.isSuccess() && result.getInvoiceData() != null) {
                        // Ensure source filename is set
                        result.getInvoiceData().setSourceFilename(filename);
                        allInvoiceData.add(result.getInvoiceData());
                        processedFiles.add(filename);
                        logger.info("Successfully processed file: {}", filename);
                    } else {
                        failedFiles.add(filename + " (extraction failed: " + result.getErrorMessage() + ")");
                    }

                } catch (Exception e) {
                    logger.error("Error processing file {}: {}", file.getOriginalFilename(), e.getMessage());
                    failedFiles.add(file.getOriginalFilename() + " (processing error: " + e.getMessage() + ")");
                }
            }

            // Step 3: Generate consolidated Excel file
            if (allInvoiceData.isEmpty()) {
                response.put("success", false);
                response.put("error", "No invoices could be processed successfully");
                response.put("failedFiles", failedFiles);
                return ResponseEntity.ok(response);
            }

            String excelPath = outputPath;
            if (excelPath == null || excelPath.trim().isEmpty()) {
                // Generate default path with timestamp
                String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                excelPath = "output/consolidated_invoices_" + timestamp + ".xlsx";
            }
            
            // Ensure output directory exists
            java.nio.file.Files.createDirectories(Paths.get(excelPath).getParent());
            
            // Create consolidated Excel file
            excelService.createEnhancedExcelFile(allInvoiceData, excelPath);
            
            // Build response
            response.put("success", true);
            response.put("excelPath", excelPath);
            response.put("totalProcessed", allInvoiceData.size());
            response.put("totalFiles", files.length);
            response.put("processedFiles", processedFiles);
            
            if (!failedFiles.isEmpty()) {
                response.put("failedFiles", failedFiles);
            }

            // Calculate summary statistics
            Map<String, Object> summary = calculateSummaryStatistics(allInvoiceData);
            response.put("summary", summary);

            logger.info("Successfully created consolidated Excel file with {} invoices: {}", 
                       allInvoiceData.size(), excelPath);
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error processing batch invoices to Excel: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("error", "Batch processing failed: " + e.getMessage());
            if (!processedFiles.isEmpty()) {
                response.put("partiallyProcessedFiles", processedFiles);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Health check endpoint.
     * 
     * @return JSON response with service status
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean ollamaHealthy = ollamaService.isOllamaHealthy();
            
            response.put("status", "ok");
            response.put("ollamaConnected", ollamaHealthy);
            response.put("ollamaUrl", ollamaService.getOllamaApiBaseUrl());
            
            if (ollamaHealthy) {
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "warning");
                response.put("message", "Ollama service is not responding");
                return ResponseEntity.ok(response);
            }
            
        } catch (Exception e) {
            logger.error("Health check error: {}", e.getMessage(), e);
            response.put("status", "error");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Root endpoint that provides information about available endpoints.
     * 
     * @return JSON response with available endpoints and service info
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getApiInfo() {
        Map<String, Object> response = new HashMap<>();
        response.put("service", "DocAssist Invoice Processing API");
        response.put("version", "1.0");
        response.put("status", "running");
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("GET /api/invoice", "This endpoint - API information");
        endpoints.put("GET /api/invoice/health", "Health check");
        endpoints.put("POST /api/invoice/process", "Process invoice from uploaded file");
        endpoints.put("POST /api/invoice/process-to-excel", "Process invoice and generate Excel file");
        endpoints.put("POST /api/invoice/ocr-only", "Extract text using OCR only");
        
        response.put("endpoints", endpoints);
        response.put("timestamp", java.time.Instant.now().toString());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Returns extracted OCR text from a document without invoice processing.
     * 
     * @param file The uploaded document file
     * @param language OCR language (optional, defaults to "eng")
     * @return JSON response with extracted text
     */
    @PostMapping(value = "/ocr-only", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> extractTextOnly(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "language", defaultValue = "eng") String language) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (file.isEmpty()) {
                response.put("success", false);
                response.put("error", "No file uploaded");
                return ResponseEntity.badRequest().body(response);
            }

            String filename = file.getOriginalFilename();
            logger.info("Performing OCR only for file: {}", filename);

            String ocrText = ocrService.performOcr(file, language);
            
            response.put("success", true);
            response.put("filename", filename);
            response.put("text", ocrText);
            response.put("textLength", ocrText != null ? ocrText.length() : 0);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error performing OCR on file: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("error", "OCR failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Calculates summary statistics for a list of invoice data.
     */
    private Map<String, Object> calculateSummaryStatistics(List<InvoiceData> invoiceDataList) {
        Map<String, Object> summary = new HashMap<>();
        
        double totalNet = invoiceDataList.stream()
            .filter(inv -> inv.getNetAmount() != null)
            .mapToDouble(inv -> inv.getNetAmount().doubleValue())
            .sum();
            
        double totalVat = invoiceDataList.stream()
            .filter(inv -> inv.getVatAmount() != null)
            .mapToDouble(inv -> inv.getVatAmount().doubleValue())
            .sum();
            
        double totalAmount = invoiceDataList.stream()
            .filter(inv -> inv.getTotalAmount() != null)
            .mapToDouble(inv -> inv.getTotalAmount().doubleValue())
            .sum();
        
        Map<String, Long> currencyCount = invoiceDataList.stream()
            .filter(inv -> inv.getCurrency() != null && !inv.getCurrency().isEmpty())
            .collect(Collectors.groupingBy(InvoiceData::getCurrency, Collectors.counting()));
            
        Map<String, Long> vendorCount = invoiceDataList.stream()
            .filter(inv -> inv.getVendorName() != null && !inv.getVendorName().isEmpty())
            .collect(Collectors.groupingBy(InvoiceData::getVendorName, Collectors.counting()));
        
        summary.put("totalInvoices", invoiceDataList.size());
        summary.put("totalNetAmount", totalNet);
        summary.put("totalVatAmount", totalVat);
        summary.put("totalAmount", totalAmount);
        summary.put("currencyBreakdown", currencyCount);
        summary.put("vendorBreakdown", vendorCount);
        
        return summary;
    }
}
