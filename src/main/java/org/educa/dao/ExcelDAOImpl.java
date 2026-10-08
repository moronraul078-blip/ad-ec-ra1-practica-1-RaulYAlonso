package org.educa.dao;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.educa.entity.ProductoEntity;
import org.educa.util.ExcelUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.List;

/**
 * Implementation of {@link ExcelDAO} using Apache POI to export product data into Excel files.
 */
public class ExcelDAOImpl implements ExcelDAO {

    // Column headers for the Excel table
    private static final String[] HEADERS = {
            "Codigo",
            "Número de\nSerie",
            "Precio",
            "Descuento",
            "Precio\nFinal",
            "Costes\nEnvío",
            "Costes\nAlmacenaje",
            "Beneficio"
    };

    // Number formats requested in the template
    private static final String CURRENCY_FORMAT = "#,##0.00 €";
    private static final String PERCENT_FORMAT = "0.00%";

    /**
     * Creates an Excel file with the list of processed products and applies formatting.
     *
     * @param productos list of {@link ProductoEntity} to write
     * @param targetFile destination .xlsx file
     * @throws IOException if there are errors creating directories or saving the workbook
     */
    @Override
    public void exportExcel(List<ProductoEntity> productos, File targetFile) throws IOException {
        // Ensure destination folder exists before writing
        ensureDirectoryExists(targetFile);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Inventario");

            // Initialize all styles once to avoid hitting the POI style limit
            ExcelStyles styles = new ExcelStyles(workbook);

            // Write header and data rows
            createHeaders(sheet, styles.header);
            populateDataRows(sheet, productos, styles);

            // Auto fit column widths so text is not cut
            autoSizeColumns(sheet);

            // Write output to the destination file
            try (FileOutputStream fos = new FileOutputStream(targetFile)) {
                workbook.write(fos);
            }
        }
    }

    /**
     * Checks if the parent directory exists and creates it if needed.
     *
     * @param targetFile destination file
     * @throws IOException if directories cannot be created
     */
    private void ensureDirectoryExists(File targetFile) throws IOException {
        if (targetFile.getParentFile() != null) {
            Files.createDirectories(targetFile.getParentFile().toPath());
        }
    }

    /**
     * Creates the first row with the column names and the header style.
     *
     * @param sheet the active sheet
     * @param headerStyle style for header cells
     */
    private void createHeaders(Sheet sheet, CellStyle headerStyle) {
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < HEADERS.length; i++) {
            createCell(headerRow, i, HEADERS[i], headerStyle);
        }
    }

    /**
     * Loops the product list and creates a row for each item.
     *
     * @param sheet the active sheet
     * @param productos data list to populate
     * @param styles container with all the predefined styles
     */
    private void populateDataRows(Sheet sheet, List<ProductoEntity> productos, ExcelStyles styles) {
        int rowIndex = 1;
        for (ProductoEntity pe : productos) {
            Row row = sheet.createRow(rowIndex);
            createProductRow(row, pe, styles, rowIndex);
            rowIndex++;
        }
    }

    /**
     * Fills a single row with all data fields from one ProductoEntity.
     * Alternates row background (odd rows green, even rows white) across the whole row.
     *
     * @param row target row in the sheet
     * @param pe entity with calculated product data
     * @param styles style container
     * @param rowIndex current row number to alternate colors
     */
    private void createProductRow(Row row, ProductoEntity pe, ExcelStyles styles, int rowIndex) {
        // Par is white impar is green
        boolean isGreen = (rowIndex % 2 != 0);

        CellStyle codeStyle = isGreen ? styles.codeGreen : styles.codeWhite;
        CellStyle serieStyle = isGreen ? styles.serieGreen : styles.serieWhite;
        CellStyle currencyStyle = isGreen ? styles.currencyGreen : styles.currencyWhite;
        CellStyle percentStyle = isGreen ? styles.percentGreen : styles.percentWhite;

        // Col 0: Codigo
        createCell(row, 0, pe.getProducto().getCodigo(), codeStyle);

        // Col 1: Numero de Serie
        createCell(row, 1, pe.getProducto().getNumeroSerie(), serieStyle);

        // Col 2: Precio base
        createCell(row, 2, pe.getProducto().getPrecio(), currencyStyle);

        // Col 3: Descuento (divided by 100 for Excel percent formatting)
        BigDecimal descuento = pe.getProducto().getDescuento();
        Double descValue = (descuento != null) ? descuento.doubleValue() / 100.0 : null;
        createCell(row, 3, descValue, percentStyle);

        // Col 4: Precio Final
        createCell(row, 4, pe.getPrecioFinal(), currencyStyle);

        // Col 5 and 6: Shipping and storage costs (checking if costes object is not null)
        BigDecimal costesEnvio = (pe.getProducto().getCostes() != null) ? pe.getProducto().getCostes().getCostesEnvio() : null;
        BigDecimal costesAlmacen = (pe.getProducto().getCostes() != null) ? pe.getProducto().getCostes().getCostesAlmacenaje() : null;
        createCell(row, 5, costesEnvio, currencyStyle);
        createCell(row, 6, costesAlmacen, currencyStyle);

        // Col 7: Profit
        createCell(row, 7, pe.getProfit(), currencyStyle);
    }

    /**
     * Adjusts the column width according to text length and adds extra padding.
     *
     * @param sheet the active sheet
     */
    private void autoSizeColumns(Sheet sheet) {
        for (int i = 0; i < HEADERS.length; i++) {
            sheet.autoSizeColumn(i);
            // Extra padding to make sure multiline text is not written over
            sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 1024);
        }
    }

    // =========================================================================
    // Overloaded helper methods to handle types when creating cells
    // =========================================================================

    /**
     * Creates a cell with a String value.
     */
    private void createCell(Row row, int colIndex, String value, CellStyle style) {
        Cell cell = row.createCell(colIndex);
        if (value != null) {
            cell.setCellValue(value);
        }
        cell.setCellStyle(style);
    }

    /**
     * Overload for BigDecimal values converting to double for POI.
     */
    private void createCell(Row row, int colIndex, BigDecimal value, CellStyle style) {
        Cell cell = row.createCell(colIndex);
        if (value != null) {
            cell.setCellValue(value.doubleValue());
        }
        cell.setCellStyle(style);
    }

    /**
     * Overload for Double values.
     */
    private void createCell(Row row, int colIndex, Double value, CellStyle style) {
        Cell cell = row.createCell(colIndex);
        if (value != null) {
            cell.setCellValue(value);
        }
        cell.setCellStyle(style);
    }

    /**
     * Helper class to group and initialize all cell styles at once.
     */
    private static class ExcelStyles {
        final CellStyle header;
        final CellStyle codeGreen;
        final CellStyle codeWhite;
        final CellStyle serieGreen;
        final CellStyle serieWhite;
        final CellStyle currencyGreen;
        final CellStyle currencyWhite;
        final CellStyle percentGreen;
        final CellStyle percentWhite;

        ExcelStyles(Workbook workbook) {
            this.header = ExcelUtils.createHeaderStyle(workbook);

            // Col 0: Codigo (bold and centered)
            this.codeGreen = ExcelUtils.createDataStyle(workbook, IndexedColors.LIGHT_GREEN, null, true, HorizontalAlignment.CENTER);
            this.codeWhite = ExcelUtils.createDataStyle(workbook, IndexedColors.WHITE, null, true, HorizontalAlignment.CENTER);

            // Col 1: Numero de serie (left aligned)
            this.serieGreen = ExcelUtils.createDataStyle(workbook, IndexedColors.LIGHT_GREEN, null, false, HorizontalAlignment.LEFT);
            this.serieWhite = ExcelUtils.createDataStyle(workbook, IndexedColors.WHITE, null, false, HorizontalAlignment.LEFT);

            // Currency columns (€)
            this.currencyGreen = ExcelUtils.createDataStyle(workbook, IndexedColors.LIGHT_GREEN, CURRENCY_FORMAT, false, HorizontalAlignment.RIGHT);
            this.currencyWhite = ExcelUtils.createDataStyle(workbook, IndexedColors.WHITE, CURRENCY_FORMAT, false, HorizontalAlignment.RIGHT);

            // Discount column (%)
            this.percentGreen = ExcelUtils.createDataStyle(workbook, IndexedColors.LIGHT_GREEN, PERCENT_FORMAT, false, HorizontalAlignment.RIGHT);
            this.percentWhite = ExcelUtils.createDataStyle(workbook, IndexedColors.WHITE, PERCENT_FORMAT, false, HorizontalAlignment.RIGHT);
        }
    }
}