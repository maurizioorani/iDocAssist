package com.marsk.docassist.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marsk.docassist.InvoiceExtractionAssistant;
import com.marsk.docassist.config.OllamaConfig;
import com.marsk.docassist.model.InvoiceData;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.Duration;

/**
 * Service for extracting structured invoice data from OCR text using Ollama LLM
 * via the Langchain4j InvoiceExtractionAssistant.
 * Specialized in processing both Italian and English invoices.
 */
@Service
public class OllamaService {
    private static final Logger logger = LoggerFactory.getLogger(OllamaService.class);
    
    private final InvoiceExtractionAssistant invoiceExtractionAssistant;
    private final ObjectMapper objectMapper;
    private final OllamaConfig ollamaConfig;

    public OllamaService(OllamaConfig ollamaConfig) {
        this.ollamaConfig = ollamaConfig;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.findAndRegisterModules(); // For LocalDate support
        
        // Create Ollama chat model
        OllamaChatModel chatModel = OllamaChatModel.builder()
                .baseUrl(ollamaConfig.getOllamaBaseUrl())
                .modelName(ollamaConfig.getOllamaModelName())
                .temperature(0.1)
                .timeout(Duration.ofMinutes(5))
                .build();
        
        // Create AI service
        this.invoiceExtractionAssistant = AiServices.builder(InvoiceExtractionAssistant.class)
                .chatModel(chatModel)
                .build();
    }

    /**
     * Extracts structured invoice data from OCR text using the AI assistant.
     * 
     * @param ocrText The OCR-extracted text from the invoice document
     * @param sourceFilename The filename of the source document for tracking
     * @return InvoiceData object with extracted information
     * @throws InvoiceExtractionException if extraction fails
     */
    public InvoiceData extractInvoiceData(String ocrText, String sourceFilename) throws InvoiceExtractionException {
        if (ocrText == null || ocrText.trim().isEmpty()) {
            logger.warn("Empty OCR text provided for invoice extraction");
            throw new InvoiceExtractionException("Cannot extract invoice data from empty text");
        }

        try {
            logger.info("Extracting invoice data from OCR text (length: {} chars) for file: {}", 
                       ocrText.length(), sourceFilename);
            
            // Use the Langchain4j AI service to extract invoice data
            InvoiceData invoiceData = invoiceExtractionAssistant.extractInvoiceData(ocrText);
            
            // Set the source filename for tracking
            if (invoiceData != null) {
                invoiceData.setSourceFilename(sourceFilename);
                
                // Log extraction summary
                logger.info("Successfully extracted invoice data for file: {}. Invoice number: {}, Vendor: {}, Total: {}", 
                           sourceFilename, 
                           invoiceData.getInvoiceNumber(),
                           invoiceData.getVendorName(),
                           invoiceData.getTotalAmount());
            } else {
                logger.warn("AI service returned null invoice data for file: {}", sourceFilename);
                throw new InvoiceExtractionException("AI service failed to extract invoice data");
            }
            
            return invoiceData;
            
        } catch (Exception e) {
            logger.error("Error extracting invoice data from file {}: {}", sourceFilename, e.getMessage(), e);
            
            // Check for connectivity issues
            if (isConnectivityIssue(e)) {
                throw new InvoiceExtractionException(
                    "Failed to connect to Ollama server. Please ensure Ollama is running and accessible. Original error: " + e.getMessage(), e);
            } else {
                throw new InvoiceExtractionException(
                    "Failed to extract invoice data: " + e.getMessage(), e);
            }
        }
    }

    /**
     * Extracts invoice data and returns it as a JSON string.
     * 
     * @param ocrText The OCR-extracted text from the invoice document
     * @param sourceFilename The filename of the source document for tracking
     * @return JSON string representation of the extracted invoice data
     * @throws InvoiceExtractionException if extraction or JSON conversion fails
     */
    public String extractInvoiceDataAsJson(String ocrText, String sourceFilename) throws InvoiceExtractionException {
        InvoiceData invoiceData = extractInvoiceData(ocrText, sourceFilename);
        
        try {
            return objectMapper.writeValueAsString(invoiceData);
        } catch (JsonProcessingException e) {
            logger.error("Error converting invoice data to JSON for file {}: {}", sourceFilename, e.getMessage(), e);
            throw new InvoiceExtractionException("Failed to convert invoice data to JSON: " + e.getMessage(), e);
        }
    }

    /**
     * Processes invoice text with enhanced error handling and confidence scoring.
     * 
     * @param ocrText The OCR-extracted text
     * @param sourceFilename The source file name
     * @return InvoiceExtractionResult with data and confidence information
     */
    public InvoiceExtractionResult processInvoice(String ocrText, String sourceFilename) {
        try {
            InvoiceData invoiceData = extractInvoiceData(ocrText, sourceFilename);
            
            // Calculate confidence score based on extracted fields
            double confidence = calculateConfidenceScore(invoiceData);
            
            return new InvoiceExtractionResult(invoiceData, true, confidence, null);
            
        } catch (InvoiceExtractionException e) {
            logger.error("Invoice processing failed for file {}: {}", sourceFilename, e.getMessage());
            
            // Return a result with error information
            InvoiceData emptyData = new InvoiceData();
            emptyData.setSourceFilename(sourceFilename);
            emptyData.setProcessingNotes("Extraction failed: " + e.getMessage());
            
            return new InvoiceExtractionResult(emptyData, false, 0.0, e.getMessage());
        }
    }

    /**
     * Calculates a confidence score for the extracted invoice data based on
     * the number and quality of extracted fields.
     */
    private double calculateConfidenceScore(InvoiceData invoiceData) {
        int totalFields = 11; // Total number of key fields
        int extractedFields = 0;
        
        if (invoiceData.getInvoiceNumber() != null && !invoiceData.getInvoiceNumber().trim().isEmpty()) extractedFields++;
        if (invoiceData.getInvoiceDate() != null) extractedFields++;
        if (invoiceData.getVendorName() != null && !invoiceData.getVendorName().trim().isEmpty()) extractedFields++;
        if (invoiceData.getVendorVatNumber() != null && !invoiceData.getVendorVatNumber().trim().isEmpty()) extractedFields++;
        if (invoiceData.getClientName() != null && !invoiceData.getClientName().trim().isEmpty()) extractedFields++;
        if (invoiceData.getClientVatNumber() != null && !invoiceData.getClientVatNumber().trim().isEmpty()) extractedFields++;
        if (invoiceData.getNetAmount() != null && invoiceData.getNetAmount().compareTo(java.math.BigDecimal.ZERO) > 0) extractedFields++;
        if (invoiceData.getVatAmount() != null && invoiceData.getVatAmount().compareTo(java.math.BigDecimal.ZERO) >= 0) extractedFields++;
        if (invoiceData.getTotalAmount() != null && invoiceData.getTotalAmount().compareTo(java.math.BigDecimal.ZERO) > 0) extractedFields++;
        if (invoiceData.getCurrency() != null && !invoiceData.getCurrency().trim().isEmpty()) extractedFields++;
        if (invoiceData.getDescription() != null && !invoiceData.getDescription().trim().isEmpty()) extractedFields++;
        
        return (double) extractedFields / totalFields;
    }

    /**
     * Checks if an exception indicates a connectivity issue with the Ollama server.
     */
    private boolean isConnectivityIssue(Exception e) {
        String errorMessage = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
        Throwable cause = e.getCause();
        String causeMessage = cause != null && cause.getMessage() != null ? cause.getMessage().toLowerCase() : "";

        return errorMessage.contains("connection refused") ||
               errorMessage.contains("failed to connect") ||
               errorMessage.contains("timeout") ||
               errorMessage.contains("service unavailable") ||
               errorMessage.contains("network is unreachable") ||
               errorMessage.contains("connection timed out") ||
               causeMessage.contains("connection refused") ||
               causeMessage.contains("failed to connect") ||
               causeMessage.contains("timeout");
    }    /**
     * Gets the configured Ollama API base URL.
     */
    public String getOllamaApiBaseUrl() {
        return ollamaConfig.getOllamaBaseUrl();
    }

    /**
     * Simple health check to verify Ollama connectivity.
     * 
     * @return true if service is accessible, false otherwise
     */
    public boolean isOllamaHealthy() {
        try {
            // Simple test with minimal text
            InvoiceData testResult = invoiceExtractionAssistant.extractInvoiceData("Test connection");
            return true; // If we get here, the service is responding
        } catch (Exception e) {
            logger.warn("Ollama health check failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Result class for invoice extraction operations.
     */
    public static class InvoiceExtractionResult {
        private final InvoiceData invoiceData;
        private final boolean success;
        private final double confidence;
        private final String errorMessage;

        public InvoiceExtractionResult(InvoiceData invoiceData, boolean success, double confidence, String errorMessage) {
            this.invoiceData = invoiceData;
            this.success = success;
            this.confidence = confidence;
            this.errorMessage = errorMessage;
        }

        public InvoiceData getInvoiceData() { return invoiceData; }
        public boolean isSuccess() { return success; }
        public double getConfidence() { return confidence; }
        public String getErrorMessage() { return errorMessage; }
    }

    /**
     * Custom exception for invoice extraction errors.
     */
    public static class InvoiceExtractionException extends Exception {
        public InvoiceExtractionException(String message) {
            super(message);
        }

        public InvoiceExtractionException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
