package com.marsk.docassist.service;

import com.marsk.docassist.model.InvoiceData;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for creating Excel files from invoice data.
 * Uses Apache POI to generate XLSX files with extracted invoice information.
 */
@Service
public class ExcelService {
    private static final Logger logger = LoggerFactory.getLogger(ExcelService.class);
    
    private static final String[] HEADERS = {
        "Source File", "Invoice Number", "Invoice Date", "Vendor Name", "Vendor VAT Number",
        "Client Name", "Client VAT Number", "Net Amount", "VAT Amount", "Total Amount",
        "Currency", "Description", "Processing Notes"
    };

    /**
     * Creates an Excel file with invoice data.
     * 
     * @param invoiceDataList List of invoice data to write to Excel
     * @param outputFilePath Path where the Excel file should be created
     * @throws ExcelExportException if there's an error creating the Excel file
     */
    public void createExcelFile(List<InvoiceData> invoiceDataList, String outputFilePath) throws ExcelExportException {
        if (invoiceDataList == null || invoiceDataList.isEmpty()) {
            logger.warn("No invoice data provided for Excel export");
            throw new ExcelExportException("Cannot create Excel file with empty invoice data");
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Invoice Data");
            
            // Create header row
            createHeaderRow(sheet);
            
            // Create data rows
            int rowNum = 1;
            for (InvoiceData invoiceData : invoiceDataList) {
                createDataRow(sheet, rowNum++, invoiceData);
            }
            
            // Auto-size columns for better readability
            autoSizeColumns(sheet);
            
            // Write to file
            try (FileOutputStream fileOut = new FileOutputStream(outputFilePath)) {
                workbook.write(fileOut);
            }
            
            logger.info("Successfully created Excel file with {} invoice records at: {}", 
                       invoiceDataList.size(), outputFilePath);
            
        } catch (IOException e) {
            logger.error("Error creating Excel file at {}: {}", outputFilePath, e.getMessage(), e);
            throw new ExcelExportException("Failed to create Excel file: " + e.getMessage(), e);
        }
    }

    /**
     * Creates an Excel file with a single invoice data record.
     * 
     * @param invoiceData Single invoice data to write to Excel
     * @param outputFilePath Path where the Excel file should be created
     * @throws ExcelExportException if there's an error creating the Excel file
     */
    public void createExcelFile(InvoiceData invoiceData, String outputFilePath) throws ExcelExportException {
        createExcelFile(List.of(invoiceData), outputFilePath);
    }

    /**
     * Appends invoice data to an existing Excel file, or creates a new one if it doesn't exist.
     * 
     * @param invoiceData Invoice data to append
     * @param excelFilePath Path to the Excel file
     * @throws ExcelExportException if there's an error updating the Excel file
     */
    public void appendToExcelFile(InvoiceData invoiceData, String excelFilePath) throws ExcelExportException {
        // For simplicity, we'll implement this as reading existing data and rewriting the file
        // In a production environment, you might want to use a more efficient approach
        logger.info("Appending invoice data to Excel file: {}", excelFilePath);
        
        try {
            // For now, just create a new file with the single record
            // TODO: Implement proper append functionality by reading existing file first
            createExcelFile(invoiceData, excelFilePath);
        } catch (Exception e) {
            logger.error("Error appending to Excel file {}: {}", excelFilePath, e.getMessage(), e);
            throw new ExcelExportException("Failed to append to Excel file: " + e.getMessage(), e);
        }
    }

    /**
     * Creates the header row with column names.
     */
    private void createHeaderRow(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        
        // Create header style
        CellStyle headerStyle = sheet.getWorkbook().createCellStyle();
        Font headerFont = sheet.getWorkbook().createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        for (int i = 0; i < HEADERS.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(HEADERS[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    /**
     * Creates a data row with invoice information.
     */
    private void createDataRow(Sheet sheet, int rowNum, InvoiceData invoiceData) {
        Row row = sheet.createRow(rowNum);
        
        // Create date format style
        CellStyle dateStyle = sheet.getWorkbook().createCellStyle();
        CreationHelper createHelper = sheet.getWorkbook().getCreationHelper();
        dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-mm-dd"));
        
        // Create currency format style
        CellStyle currencyStyle = sheet.getWorkbook().createCellStyle();
        currencyStyle.setDataFormat(createHelper.createDataFormat().getFormat("#,##0.00"));
        
        int colNum = 0;
        
        // Source File
        createCell(row, colNum++, invoiceData.getSourceFilename());
        
        // Invoice Number
        createCell(row, colNum++, invoiceData.getInvoiceNumber());
        
        // Invoice Date
        if (invoiceData.getInvoiceDate() != null) {
            Cell dateCell = row.createCell(colNum++);
            dateCell.setCellValue(invoiceData.getInvoiceDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
            dateCell.setCellStyle(dateStyle);
        } else {
            createCell(row, colNum++, "");
        }
        
        // Vendor Name
        createCell(row, colNum++, invoiceData.getVendorName());
        
        // Vendor VAT Number
        createCell(row, colNum++, invoiceData.getVendorVatNumber());
        
        // Client Name
        createCell(row, colNum++, invoiceData.getClientName());
        
        // Client VAT Number
        createCell(row, colNum++, invoiceData.getClientVatNumber());
        
        // Net Amount
        if (invoiceData.getNetAmount() != null) {
            Cell amountCell = row.createCell(colNum++);
            amountCell.setCellValue(invoiceData.getNetAmount().doubleValue());
            amountCell.setCellStyle(currencyStyle);
        } else {
            createCell(row, colNum++, "");
        }
        
        // VAT Amount
        if (invoiceData.getVatAmount() != null) {
            Cell vatCell = row.createCell(colNum++);
            vatCell.setCellValue(invoiceData.getVatAmount().doubleValue());
            vatCell.setCellStyle(currencyStyle);
        } else {
            createCell(row, colNum++, "");
        }
        
        // Total Amount
        if (invoiceData.getTotalAmount() != null) {
            Cell totalCell = row.createCell(colNum++);
            totalCell.setCellValue(invoiceData.getTotalAmount().doubleValue());
            totalCell.setCellStyle(currencyStyle);
        } else {
            createCell(row, colNum++, "");
        }
        
        // Currency
        createCell(row, colNum++, invoiceData.getCurrency());
        
        // Description
        createCell(row, colNum++, invoiceData.getDescription());
        
        // Processing Notes
        createCell(row, colNum++, invoiceData.getProcessingNotes());
    }

    /**
     * Helper method to create a cell with string value.
     */
    private void createCell(Row row, int colNum, String value) {
        Cell cell = row.createCell(colNum);
        cell.setCellValue(value != null ? value : "");
    }

    /**
     * Auto-sizes all columns for better readability.
     */
    private void autoSizeColumns(Sheet sheet) {
        for (int i = 0; i < HEADERS.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    /**
     * Custom exception for Excel export errors.
     */
    public static class ExcelExportException extends Exception {
        public ExcelExportException(String message) {
            super(message);
        }

        public ExcelExportException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Creates an enhanced Excel file with invoice data and summary calculations.
     * 
     * @param invoiceDataList List of invoice data to write to Excel
     * @param outputFilePath Path where the Excel file should be created
     * @throws ExcelExportException if there's an error creating the Excel file
     */
    public void createEnhancedExcelFile(List<InvoiceData> invoiceDataList, String outputFilePath) throws ExcelExportException {
        if (invoiceDataList == null || invoiceDataList.isEmpty()) {
            logger.warn("No invoice data provided for enhanced Excel export");
            throw new ExcelExportException("Cannot create Excel file with empty invoice data");
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            // Create main data sheet
            Sheet dataSheet = workbook.createSheet("Invoice Data");
            createEnhancedDataSheet(workbook, dataSheet, invoiceDataList);
            
            // Create summary sheet
            Sheet summarySheet = workbook.createSheet("Summary");
            createSummarySheet(workbook, summarySheet, invoiceDataList);
            
            // Write to file
            try (FileOutputStream fileOut = new FileOutputStream(outputFilePath)) {
                workbook.write(fileOut);
            }
            
            logger.info("Successfully created enhanced Excel file with {} invoice records and summary at: {}", 
                       invoiceDataList.size(), outputFilePath);
            
        } catch (IOException e) {
            logger.error("Error creating enhanced Excel file at {}: {}", outputFilePath, e.getMessage(), e);
            throw new ExcelExportException("Failed to create enhanced Excel file: " + e.getMessage(), e);
        }
    }

    /**
     * Creates the enhanced data sheet with improved formatting and totals.
     */
    private void createEnhancedDataSheet(Workbook workbook, Sheet sheet, List<InvoiceData> invoiceDataList) {
        // Create enhanced header row
        createEnhancedHeaderRow(workbook, sheet);
        
        // Create data rows
        int rowNum = 1;
        for (InvoiceData invoiceData : invoiceDataList) {
            createEnhancedDataRow(workbook, sheet, rowNum++, invoiceData);
        }
        
        // Add totals row
        createTotalsRow(workbook, sheet, rowNum, invoiceDataList.size());
        
        // Auto-size columns
        autoSizeColumns(sheet);
        
        // Freeze the header row
        sheet.createFreezePane(0, 1);
    }

    /**
     * Creates the summary sheet with aggregate data.
     */
    private void createSummarySheet(Workbook workbook, Sheet sheet, List<InvoiceData> invoiceDataList) {
        // Create title
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("Invoice Processing Summary");
        
        // Title style
        CellStyle titleStyle = workbook.createCellStyle();
        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeight((short) 280);
        titleStyle.setFont(titleFont);
        titleCell.setCellStyle(titleStyle);
        
        int rowNum = 2;
        
        // Summary statistics
        createSummaryRow(workbook, sheet, rowNum++, "Total Invoices:", invoiceDataList.size());
        
        // Calculate totals
        double totalNetAmount = invoiceDataList.stream()
            .filter(inv -> inv.getNetAmount() != null)
            .mapToDouble(inv -> inv.getNetAmount().doubleValue())
            .sum();
            
        double totalVatAmount = invoiceDataList.stream()
            .filter(inv -> inv.getVatAmount() != null)
            .mapToDouble(inv -> inv.getVatAmount().doubleValue())
            .sum();
            
        double totalGrossAmount = invoiceDataList.stream()
            .filter(inv -> inv.getTotalAmount() != null)
            .mapToDouble(inv -> inv.getTotalAmount().doubleValue())
            .sum();
        
        // Currency format for summary
        CellStyle currencyStyle = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        currencyStyle.setDataFormat(createHelper.createDataFormat().getFormat("#,##0.00"));
        
        createSummaryRowWithCurrency(workbook, sheet, rowNum++, "Total Net Amount:", totalNetAmount, currencyStyle);
        createSummaryRowWithCurrency(workbook, sheet, rowNum++, "Total VAT Amount:", totalVatAmount, currencyStyle);
        createSummaryRowWithCurrency(workbook, sheet, rowNum++, "Total Gross Amount:", totalGrossAmount, currencyStyle);
        
        rowNum++; // Empty row
        
        // Vendor breakdown
        createSummaryRow(workbook, sheet, rowNum++, "Vendor Breakdown:", "");
        
        Map<String, Double> vendorTotals = invoiceDataList.stream()
            .filter(inv -> inv.getVendorName() != null && inv.getTotalAmount() != null)
            .collect(Collectors.groupingBy(
                InvoiceData::getVendorName,
                Collectors.summingDouble(inv -> inv.getTotalAmount().doubleValue())
            ));
        
        for (Map.Entry<String, Double> entry : vendorTotals.entrySet()) {
            createSummaryRowWithCurrency(workbook, sheet, rowNum++, 
                "  " + entry.getKey() + ":", entry.getValue(), currencyStyle);
        }
        
        // Auto-size columns
        for (int i = 0; i < 3; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    /**
     * Creates enhanced header row with better styling.
     */
    private void createEnhancedHeaderRow(Workbook workbook, Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        
        // Enhanced header style
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THICK);
        headerStyle.setBorderTop(BorderStyle.THICK);
        headerStyle.setBorderLeft(BorderStyle.THICK);
        headerStyle.setBorderRight(BorderStyle.THICK);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        
        for (int i = 0; i < HEADERS.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(HEADERS[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    /**
     * Creates enhanced data row with better formatting.
     */
    private void createEnhancedDataRow(Workbook workbook, Sheet sheet, int rowNum, InvoiceData invoiceData) {
        Row row = sheet.createRow(rowNum);
        
        // Create styles
        CellStyle dateStyle = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-mm-dd"));
        
        CellStyle currencyStyle = workbook.createCellStyle();
        currencyStyle.setDataFormat(createHelper.createDataFormat().getFormat("#,##0.00"));
        
        // Alternating row colors
        CellStyle alternateStyle = workbook.createCellStyle();
        if (rowNum % 2 == 0) {
            alternateStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            alternateStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        
        int colNum = 0;
        
        // Create cells with enhanced formatting
        createStyledCell(row, colNum++, invoiceData.getSourceFilename(), alternateStyle);
        createStyledCell(row, colNum++, invoiceData.getInvoiceNumber(), alternateStyle);
        
        // Invoice Date with date formatting
        if (invoiceData.getInvoiceDate() != null) {
            Cell dateCell = row.createCell(colNum++);
            dateCell.setCellValue(invoiceData.getInvoiceDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
            CellStyle combinedDateStyle = workbook.createCellStyle();
            combinedDateStyle.cloneStyleFrom(dateStyle);
            if (rowNum % 2 == 0) {
                combinedDateStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                combinedDateStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            }
            dateCell.setCellStyle(combinedDateStyle);
        } else {
            createStyledCell(row, colNum++, "", alternateStyle);
        }
        
        createStyledCell(row, colNum++, invoiceData.getVendorName(), alternateStyle);
        createStyledCell(row, colNum++, invoiceData.getVendorVatNumber(), alternateStyle);
        createStyledCell(row, colNum++, invoiceData.getClientName(), alternateStyle);
        createStyledCell(row, colNum++, invoiceData.getClientVatNumber(), alternateStyle);
        
        // Currency amounts with proper formatting
        createCurrencyCell(row, colNum++, invoiceData.getNetAmount(), currencyStyle, alternateStyle, workbook);
        createCurrencyCell(row, colNum++, invoiceData.getVatAmount(), currencyStyle, alternateStyle, workbook);
        createCurrencyCell(row, colNum++, invoiceData.getTotalAmount(), currencyStyle, alternateStyle, workbook);
        
        createStyledCell(row, colNum++, invoiceData.getCurrency(), alternateStyle);
        createStyledCell(row, colNum++, invoiceData.getDescription(), alternateStyle);
        createStyledCell(row, colNum++, invoiceData.getProcessingNotes(), alternateStyle);
    }

    /**
     * Creates a totals row at the bottom of the data.
     */
    private void createTotalsRow(Workbook workbook, Sheet sheet, int rowNum, int dataRowCount) {
        Row totalsRow = sheet.createRow(rowNum);
        
        // Totals row style
        CellStyle totalsStyle = workbook.createCellStyle();
        Font totalsFont = workbook.createFont();
        totalsFont.setBold(true);
        totalsStyle.setFont(totalsFont);
        totalsStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        totalsStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        totalsStyle.setBorderTop(BorderStyle.THICK);
        
        // Currency totals style
        CellStyle totalsCurrencyStyle = workbook.createCellStyle();
        totalsCurrencyStyle.cloneStyleFrom(totalsStyle);
        CreationHelper createHelper = workbook.getCreationHelper();
        totalsCurrencyStyle.setDataFormat(createHelper.createDataFormat().getFormat("#,##0.00"));
        
        // "TOTALS" label
        Cell totalsLabel = totalsRow.createCell(6);
        totalsLabel.setCellValue("TOTALS:");
        totalsLabel.setCellStyle(totalsStyle);
        
        // Sum formulas for currency columns (Net Amount, VAT Amount, Total Amount)
        String netAmountFormula = String.format("SUM(H2:H%d)", rowNum);
        String vatAmountFormula = String.format("SUM(I2:I%d)", rowNum);
        String totalAmountFormula = String.format("SUM(J2:J%d)", rowNum);
        
        Cell netTotalCell = totalsRow.createCell(7);
        netTotalCell.setCellFormula(netAmountFormula);
        netTotalCell.setCellStyle(totalsCurrencyStyle);
        
        Cell vatTotalCell = totalsRow.createCell(8);
        vatTotalCell.setCellFormula(vatAmountFormula);
        vatTotalCell.setCellStyle(totalsCurrencyStyle);
        
        Cell grandTotalCell = totalsRow.createCell(9);
        grandTotalCell.setCellFormula(totalAmountFormula);
        grandTotalCell.setCellStyle(totalsCurrencyStyle);
    }

    /**
     * Helper method to create styled cells.
     */
    private void createStyledCell(Row row, int colNum, String value, CellStyle style) {
        Cell cell = row.createCell(colNum);
        cell.setCellValue(value != null ? value : "");
        if (style != null) {
            cell.setCellStyle(style);
        }
    }

    /**
     * Helper method to create currency cells with proper formatting.
     */
    private void createCurrencyCell(Row row, int colNum, java.math.BigDecimal value, 
                                   CellStyle currencyStyle, CellStyle alternateStyle, Workbook workbook) {
        Cell cell = row.createCell(colNum);
        if (value != null) {
            cell.setCellValue(value.doubleValue());
            CellStyle combinedStyle = workbook.createCellStyle();
            combinedStyle.cloneStyleFrom(currencyStyle);
            if (alternateStyle != null && row.getRowNum() % 2 == 0) {
                combinedStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                combinedStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            }
            cell.setCellStyle(combinedStyle);
        } else {
            createStyledCell(row, colNum, "", alternateStyle);
        }
    }

    /**
     * Helper method to create summary rows.
     */
    private void createSummaryRow(Workbook workbook, Sheet sheet, int rowNum, String label, Object value) {
        Row row = sheet.createRow(rowNum);
        
        CellStyle labelStyle = workbook.createCellStyle();
        Font labelFont = workbook.createFont();
        labelFont.setBold(true);
        labelStyle.setFont(labelFont);
        
        Cell labelCell = row.createCell(0);
        labelCell.setCellValue(label);
        labelCell.setCellStyle(labelStyle);
        
        Cell valueCell = row.createCell(1);
        if (value instanceof Number) {
            valueCell.setCellValue(((Number) value).doubleValue());
        } else {
            valueCell.setCellValue(value.toString());
        }
    }

    /**
     * Helper method to create summary rows with currency formatting.
     */
    private void createSummaryRowWithCurrency(Workbook workbook, Sheet sheet, int rowNum, 
                                            String label, double value, CellStyle currencyStyle) {
        Row row = sheet.createRow(rowNum);
        
        CellStyle labelStyle = workbook.createCellStyle();
        Font labelFont = workbook.createFont();
        labelFont.setBold(true);
        labelStyle.setFont(labelFont);
        
        Cell labelCell = row.createCell(0);
        labelCell.setCellValue(label);
        labelCell.setCellStyle(labelStyle);
        
        Cell valueCell = row.createCell(1);
        valueCell.setCellValue(value);
        valueCell.setCellStyle(currencyStyle);
    }
}
