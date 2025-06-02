package com.marsk.docassist.integration;

import com.marsk.docassist.config.OllamaConfig;
import com.marsk.docassist.service.ExcelService;
import com.marsk.docassist.service.OllamaService;
import com.marsk.docassist.model.InvoiceData;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for the complete invoice processing pipeline.
 * This test verifies that the main components can be instantiated and work together.
 */
@SpringBootTest
@ActiveProfiles("test")
public class InvoiceProcessingIntegrationTest {

    @Test
    public void testOllamaServiceCreation() {
        // Test that OllamaService can be created with manually configured OllamaConfig
        // Note: Since we're not using Spring context here, we need to manually set values
        OllamaConfig config = new OllamaConfig() {
            @Override
            public String getOllamaBaseUrl() {
                return "http://localhost:11434";
            }
            
            @Override
            public String getOllamaModelName() {
                return "llama3.2";
            }
        };
        assertNotNull(config);
        
        // Note: OllamaService creation will work, but actual AI calls require Ollama to be running
        OllamaService ollamaService = new OllamaService(config);
        assertNotNull(ollamaService);
        assertEquals("http://localhost:11434", ollamaService.getOllamaApiBaseUrl());
    }

    @Test
    public void testExcelServiceCreation() {
        // Test that ExcelService can be created and used
        ExcelService excelService = new ExcelService();
        assertNotNull(excelService);
        
        // Create a test invoice data
        InvoiceData testData = new InvoiceData();
        testData.setInvoiceNumber("TEST-001");
        testData.setInvoiceDate(LocalDate.now());
        testData.setVendorName("Test Vendor");
        testData.setTotalAmount(new BigDecimal("100.0"));
        testData.setCurrency("EUR");
        
        // Test that we can create an Excel file (will create in test environment)
        String testPath = "target/test-invoice.xlsx";
        
        assertDoesNotThrow(() -> {
            excelService.createExcelFile(testData, testPath);
        });
    }

    @Test
    public void testInvoiceDataModel() {
        // Test the InvoiceData model
        InvoiceData invoice = new InvoiceData();
        
        // Test setters and getters
        invoice.setInvoiceNumber("INV-2025-001");
        invoice.setVendorName("Acme Corp");
        invoice.setTotalAmount(new BigDecimal("250.50"));
        invoice.setCurrency("USD");
        invoice.setInvoiceDate(LocalDate.of(2025, 6, 1));
        
        assertEquals("INV-2025-001", invoice.getInvoiceNumber());
        assertEquals("Acme Corp", invoice.getVendorName());
        assertEquals(new BigDecimal("250.50"), invoice.getTotalAmount());
        assertEquals("USD", invoice.getCurrency());
        assertEquals(LocalDate.of(2025, 6, 1), invoice.getInvoiceDate());
    }
}
