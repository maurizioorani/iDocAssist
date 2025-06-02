package com.marsk.docassist.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Model class for storing structured invoice data extracted from documents.
 * This class is designed to be easily serializable to/from JSON and can be
 * mapped to Excel columns.
 */
public class InvoiceData {
    private String invoiceNumber;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate invoiceDate;
    
    private String vendorName;
    private String vendorVatNumber;
    private String clientName;
    private String clientVatNumber;
    private BigDecimal netAmount;
    private BigDecimal vatAmount;
    private BigDecimal totalAmount;
    private String currency;
    private String description;
    private String sourceFilename;
    private String processingNotes;

    // Default constructor
    public InvoiceData() {
    }

    // Getters and setters
    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(LocalDate invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getVendorVatNumber() {
        return vendorVatNumber;
    }

    public void setVendorVatNumber(String vendorVatNumber) {
        this.vendorVatNumber = vendorVatNumber;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientVatNumber() {
        return clientVatNumber;
    }

    public void setClientVatNumber(String clientVatNumber) {
        this.clientVatNumber = clientVatNumber;
    }

    public BigDecimal getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(BigDecimal netAmount) {
        this.netAmount = netAmount;
    }

    public BigDecimal getVatAmount() {
        return vatAmount;
    }

    public void setVatAmount(BigDecimal vatAmount) {
        this.vatAmount = vatAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSourceFilename() {
        return sourceFilename;
    }

    public void setSourceFilename(String sourceFilename) {
        this.sourceFilename = sourceFilename;
    }

    public String getProcessingNotes() {
        return processingNotes;
    }

    public void setProcessingNotes(String processingNotes) {
        this.processingNotes = processingNotes;
    }

    @Override
    public String toString() {
        return "InvoiceData{" +
                "invoiceNumber='" + invoiceNumber + '\'' +
                ", invoiceDate=" + invoiceDate +
                ", vendorName='" + vendorName + '\'' +
                ", vendorVatNumber='" + vendorVatNumber + '\'' +
                ", clientName='" + clientName + '\'' +
                ", clientVatNumber='" + clientVatNumber + '\'' +
                ", netAmount=" + netAmount +
                ", vatAmount=" + vatAmount +
                ", totalAmount=" + totalAmount +
                ", currency='" + currency + '\'' +
                ", description='" + description + '\'' +
                ", sourceFilename='" + sourceFilename + '\'' +
                '}';
    }
}
