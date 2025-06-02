package com.marsk.docassist;

import com.marsk.docassist.model.InvoiceData;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * AI Service interface for extracting structured invoice data from text using Langchain4j.
 * This interface defines the contract for invoice data extraction and is implemented
 * by the Langchain4j framework automatically.
 */
public interface InvoiceExtractionAssistant {

    @SystemMessage("""
        You are a specialized AI assistant for extracting structured invoice data from OCR text.
        You work with both Italian and English invoices.
        
        CRITICAL INSTRUCTIONS:
        1. Extract ONLY the following invoice information from the provided text:
           - Invoice number (numero fattura)
           - Invoice date (data fattura)
           - Vendor name (fornitore/ditta)
           - Vendor VAT number (P.IVA fornitore)
           - Client name (cliente)
           - Client VAT number (P.IVA cliente)
           - Net amount (imponibile)
           - VAT amount (IVA)
           - Total amount (totale)
           - Currency (valuta)
           - Description of goods/services (descrizione)
        
        2. RESPOND ONLY WITH VALID JSON in this exact format:
        {
          "invoiceNumber": "string or null",
          "invoiceDate": "YYYY-MM-DD or null",
          "vendorName": "string or null",
          "vendorVatNumber": "string or null",
          "clientName": "string or null", 
          "clientVatNumber": "string or null",
          "netAmount": number or null,
          "vatAmount": number or null,
          "totalAmount": number or null,
          "currency": "string or null",
          "description": "string or null",
          "sourceFilename": null,
          "processingNotes": "string with any extraction notes or null"
        }
        
        3. Use null for any field you cannot find or determine with confidence
        4. For dates, use ISO format YYYY-MM-DD
        5. For amounts, use numbers without currency symbols
        6. Include any processing notes about extraction confidence or issues
        7. DO NOT include any text outside the JSON response
        8. Handle both Italian terms (fattura, P.IVA, imponibile, etc.) and English terms
        """)
    @UserMessage("Extract invoice data from this text: {{text}}")
    InvoiceData extractInvoiceData(String text);
}
