package com.marsk.docassist.integration;

import com.marsk.docassist.model.OcrTextDocument;
import com.marsk.docassist.repository.OcrTextDocumentRepository;
import com.marsk.docassist.service.OcrService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive integration test for database functionality in the DocAssist system.
 * Tests document saving, retrieval, and complete database workflow.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class DatabaseFunctionalityTest {

    @Autowired
    private OcrTextDocumentRepository ocrTextDocumentRepository;

    @Autowired
    private OcrService ocrService;

    @BeforeEach
    void setUp() {
        // Clean the database before each test
        ocrTextDocumentRepository.deleteAll();
    }

    @Test
    @DisplayName("Test saving a document to database")
    void testSaveDocumentToDatabase() {
        // Given
        String filename = "test-invoice.pdf";
        String extractedText = "Invoice Number: INV-2025-001\nVendor: Acme Corp\nAmount: $250.50";
        String language = "eng";
        String documentType = "invoice";

        OcrTextDocument document = new OcrTextDocument(filename, extractedText, language, documentType);

        // When
        OcrTextDocument savedDocument = ocrTextDocumentRepository.save(document);

        // Then
        assertNotNull(savedDocument);
        assertNotNull(savedDocument.getId());
        assertEquals(filename, savedDocument.getOriginalFilename());
        assertEquals(extractedText, savedDocument.getExtractedText());
        assertEquals(language, savedDocument.getLanguageUsed());
        assertEquals(documentType, savedDocument.getDocumentType());
        assertNotNull(savedDocument.getCreatedAt());
        assertTrue(savedDocument.getCreatedAt().isBefore(LocalDateTime.now().plusMinutes(1)));
    }

    @Test
    @DisplayName("Test retrieving document by ID")
    void testRetrieveDocumentById() {
        // Given
        OcrTextDocument document = createTestDocument("receipt.jpg", "Receipt text content", "eng");
        OcrTextDocument savedDocument = ocrTextDocumentRepository.save(document);

        // When
        Optional<OcrTextDocument> retrievedDocument = ocrTextDocumentRepository.findById(savedDocument.getId());

        // Then
        assertTrue(retrievedDocument.isPresent());
        assertEquals(savedDocument.getId(), retrievedDocument.get().getId());
        assertEquals("receipt.jpg", retrievedDocument.get().getOriginalFilename());
        assertEquals("Receipt text content", retrievedDocument.get().getExtractedText());
        assertEquals("eng", retrievedDocument.get().getLanguageUsed());
    }

    @Test
    @DisplayName("Test retrieving all documents")
    void testRetrieveAllDocuments() {
        // Given
        ocrTextDocumentRepository.save(createTestDocument("doc1.pdf", "Document 1 content", "eng"));
        ocrTextDocumentRepository.save(createTestDocument("doc2.pdf", "Document 2 content", "ita"));
        ocrTextDocumentRepository.save(createTestDocument("doc3.jpg", "Document 3 content", "fra"));

        // When
        List<OcrTextDocument> allDocuments = ocrTextDocumentRepository.findAll();

        // Then
        assertEquals(3, allDocuments.size());
        
        // Verify all documents are present
        List<String> filenames = allDocuments.stream()
                .map(OcrTextDocument::getOriginalFilename)
                .toList();
        assertTrue(filenames.contains("doc1.pdf"));
        assertTrue(filenames.contains("doc2.pdf"));
        assertTrue(filenames.contains("doc3.jpg"));
    }

    @Test
    @DisplayName("Test finding documents by filename")
    void testFindDocumentsByFilename() {
        // Given
        ocrTextDocumentRepository.save(createTestDocument("invoice_2025.pdf", "Invoice content", "eng"));
        ocrTextDocumentRepository.save(createTestDocument("receipt_2025.jpg", "Receipt content", "eng"));
        ocrTextDocumentRepository.save(createTestDocument("contract.docx", "Contract content", "eng"));

        // When
        List<OcrTextDocument> invoiceDocuments = ocrTextDocumentRepository
                .findByOriginalFilenameContainingIgnoreCase("invoice");
        List<OcrTextDocument> receiptDocuments = ocrTextDocumentRepository
                .findByOriginalFilenameContainingIgnoreCase("RECEIPT");

        // Then
        assertEquals(1, invoiceDocuments.size());
        assertEquals("invoice_2025.pdf", invoiceDocuments.get(0).getOriginalFilename());
        
        assertEquals(1, receiptDocuments.size());
        assertEquals("receipt_2025.jpg", receiptDocuments.get(0).getOriginalFilename());
    }

    @Test
    @DisplayName("Test finding documents ordered by creation date")
    void testFindDocumentsOrderedByCreationDate() throws InterruptedException {
        // Given - Create documents with slight time differences
        OcrTextDocument doc1 = ocrTextDocumentRepository.save(createTestDocument("first.pdf", "First document", "eng"));
        Thread.sleep(10); // Small delay to ensure different timestamps
        
        OcrTextDocument doc2 = ocrTextDocumentRepository.save(createTestDocument("second.pdf", "Second document", "eng"));
        Thread.sleep(10);
        
        OcrTextDocument doc3 = ocrTextDocumentRepository.save(createTestDocument("third.pdf", "Third document", "eng"));

        // When
        List<OcrTextDocument> documentsOrderedByDate = ocrTextDocumentRepository.findAllByOrderByCreatedAtDesc();

        // Then
        assertEquals(3, documentsOrderedByDate.size());
        
        // Verify they are ordered by creation date (newest first)
        assertEquals("third.pdf", documentsOrderedByDate.get(0).getOriginalFilename());
        assertEquals("second.pdf", documentsOrderedByDate.get(1).getOriginalFilename());
        assertEquals("first.pdf", documentsOrderedByDate.get(2).getOriginalFilename());
        
        // Verify the timestamps are in descending order
        assertTrue(documentsOrderedByDate.get(0).getCreatedAt()
                .isAfter(documentsOrderedByDate.get(1).getCreatedAt()));
        assertTrue(documentsOrderedByDate.get(1).getCreatedAt()
                .isAfter(documentsOrderedByDate.get(2).getCreatedAt()));
    }

    @Test
    @DisplayName("Test database persistence across multiple operations")
    void testDatabasePersistenceAcrossOperations() {
        // Given - Save a document
        String originalText = "Original invoice content with important data";
        OcrTextDocument document = createTestDocument("persistent-invoice.pdf", originalText, "eng");
        OcrTextDocument savedDocument = ocrTextDocumentRepository.save(document);
        Long documentId = savedDocument.getId();

        // When - Perform multiple operations
        // 1. Update the document
        savedDocument.setDocumentType("updated-invoice");
        ocrTextDocumentRepository.save(savedDocument);

        // 2. Save another document
        ocrTextDocumentRepository.save(createTestDocument("another-doc.pdf", "Another document", "ita"));

        // 3. Retrieve the original document
        Optional<OcrTextDocument> retrievedDocument = ocrTextDocumentRepository.findById(documentId);

        // Then - Verify persistence and updates
        assertTrue(retrievedDocument.isPresent());
        assertEquals(documentId, retrievedDocument.get().getId());
        assertEquals("persistent-invoice.pdf", retrievedDocument.get().getOriginalFilename());
        assertEquals(originalText, retrievedDocument.get().getExtractedText());
        assertEquals("updated-invoice", retrievedDocument.get().getDocumentType());
        assertEquals("eng", retrievedDocument.get().getLanguageUsed());

        // Verify total count
        assertEquals(2, ocrTextDocumentRepository.count());
    }

    @Test
    @DisplayName("Test data integrity validation")
    void testDataIntegrityValidation() {
        // Test that validation constraints are enforced
        
        // Test 1: Empty filename should fail
        assertThrows(Exception.class, () -> {
            OcrTextDocument invalidDoc = new OcrTextDocument("", "Some text", "eng");
            ocrTextDocumentRepository.saveAndFlush(invalidDoc);
        });

        // Test 2: Empty extracted text should fail
        assertThrows(Exception.class, () -> {
            OcrTextDocument invalidDoc = new OcrTextDocument("file.pdf", "", "eng");
            ocrTextDocumentRepository.saveAndFlush(invalidDoc);
        });

        // Test 3: Null language should fail
        assertThrows(Exception.class, () -> {
            OcrTextDocument invalidDoc = new OcrTextDocument("file.pdf", "Some text", null);
            ocrTextDocumentRepository.saveAndFlush(invalidDoc);
        });
    }

    @Test
    @DisplayName("Test large text content handling")
    void testLargeTextContentHandling() {
        // Given - Create a document with large text content
        StringBuilder largeText = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            largeText.append("Line ").append(i).append(": This is a long line of text content that simulates OCR output from a large document. ");
        }
        
        String filename = "large-document.pdf";
        OcrTextDocument document = createTestDocument(filename, largeText.toString(), "eng");

        // When
        OcrTextDocument savedDocument = ocrTextDocumentRepository.save(document);
        Optional<OcrTextDocument> retrievedDocument = ocrTextDocumentRepository.findById(savedDocument.getId());

        // Then
        assertTrue(retrievedDocument.isPresent());
        assertEquals(filename, retrievedDocument.get().getOriginalFilename());
        assertEquals(largeText.toString(), retrievedDocument.get().getExtractedText());
        assertTrue(retrievedDocument.get().getExtractedText().length() > 50000); // Verify it's actually large
    }

    @Test
    @DisplayName("Test complete OCR service database integration")
    void testOcrServiceDatabaseIntegration() {
        // Given
        String filename = "service-test.pdf";
        String extractedText = "This text was extracted via OCR service";
        String language = "eng";

        // When - Use the OCR service to save OCR result
        OcrTextDocument savedDocument = ocrService.saveOcrResult(filename, extractedText, language);

        // Then
        assertNotNull(savedDocument);
        assertNotNull(savedDocument.getId());
        assertEquals(filename, savedDocument.getOriginalFilename());
        assertEquals(extractedText, savedDocument.getExtractedText());
        assertEquals(language, savedDocument.getLanguageUsed());

        // Verify it's actually persisted in the database
        Optional<OcrTextDocument> retrievedDocument = ocrTextDocumentRepository.findById(savedDocument.getId());
        assertTrue(retrievedDocument.isPresent());
        assertEquals(extractedText, retrievedDocument.get().getExtractedText());

        // Verify it appears in the list of all documents
        List<OcrTextDocument> allDocuments = ocrTextDocumentRepository.findAll();
        assertEquals(1, allDocuments.size());
        assertEquals(filename, allDocuments.get(0).getOriginalFilename());
    }

    @Test
    @DisplayName("Test database cleanup and isolation")
    void testDatabaseCleanupAndIsolation() {
        // Given - Start with empty database
        assertEquals(0, ocrTextDocumentRepository.count());

        // When - Add some documents
        ocrTextDocumentRepository.save(createTestDocument("test1.pdf", "Content 1", "eng"));
        ocrTextDocumentRepository.save(createTestDocument("test2.pdf", "Content 2", "ita"));

        // Then - Verify they exist
        assertEquals(2, ocrTextDocumentRepository.count());

        // When - Clear all
        ocrTextDocumentRepository.deleteAll();

        // Then - Verify database is clean
        assertEquals(0, ocrTextDocumentRepository.count());
        assertTrue(ocrTextDocumentRepository.findAll().isEmpty());
    }

    /**
     * Helper method to create a test document
     */
    private OcrTextDocument createTestDocument(String filename, String text, String language) {
        return new OcrTextDocument(filename, text, language);
    }
}
