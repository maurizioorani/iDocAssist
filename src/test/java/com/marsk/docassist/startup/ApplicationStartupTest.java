package com.marsk.docassist.startup;

import com.marsk.docassist.service.ExcelService;
import com.marsk.docassist.service.OcrService;
import com.marsk.docassist.service.OllamaService;
import com.marsk.docassist.controller.InvoiceController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test to verify that the Spring Boot application can start up successfully
 * and all required beans are properly configured.
 */
@SpringBootTest
@ActiveProfiles("test")
public class ApplicationStartupTest {

    @Autowired(required = false)
    private ExcelService excelService;

    @Autowired(required = false)
    private OcrService ocrService;

    @Autowired(required = false)
    private OllamaService ollamaService;

    @Autowired(required = false)
    private InvoiceController invoiceController;

    @Test
    public void contextLoads() {
        // Test that Spring context loads successfully
        assertNotNull(excelService, "ExcelService should be autowired");
        assertNotNull(ocrService, "OcrService should be autowired");
        assertNotNull(ollamaService, "OllamaService should be autowired");
        assertNotNull(invoiceController, "InvoiceController should be autowired");
    }

    @Test
    public void servicesAreProperlyConfigured() {
        // Test that services are properly configured
        assertNotNull(excelService);
        assertNotNull(ocrService);
        assertNotNull(ollamaService);
        
        // Test that OllamaService has the expected configuration
        assertEquals("http://localhost:11434", ollamaService.getOllamaApiBaseUrl());
        
        // Test that controller has all required dependencies
        assertNotNull(invoiceController);
    }
}
