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

public class ExcelDAOImpl implements ExcelDAO {

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
    private static final String CURRENCY_FORMAT = "#,##0.00 €";
    private static final String PERCENT_FORMAT = "0.00%";

    @Override
    public void exportExcel(List<ProductoEntity> productos, File targetFile) throws IOException {
        ensureDirectoryExists(targetFile);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Inventario");
            ExcelStyles styles = new ExcelStyles(workbook);

            createHeaders(sheet, styles.header);
            populateDataRows(sheet, productos, styles);
            autoSizeColumns(sheet);

            try (FileOutputStream fos = new FileOutputStream(targetFile)) {
                workbook.write(fos);
            }
        }
    }

    private void ensureDirectoryExists(File targetFile) throws IOException {
        if (targetFile.getParentFile() != null) {
            Files.createDirectories(targetFile.getParentFile().toPath());
        }
    }

    private void createHeaders(Sheet sheet, CellStyle headerStyle) {
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < HEADERS.length; i++) {
            createCell(headerRow, i, HEADERS[i], headerStyle);
        }
    }

    private void populateDataRows(Sheet sheet, List<ProductoEntity> productos, ExcelStyles styles) {
        int rowIndex = 1;
        for (ProductoEntity pe : productos) {
            Row row = sheet.createRow(rowIndex);
            createProductRow(row, pe, styles, rowIndex);
            rowIndex++;
        }
    }

    private void createProductRow(Row row, ProductoEntity pe, ExcelStyles styles, int rowIndex) {
        boolean isGreen = (rowIndex % 2 != 0);
        CellStyle currencyStyle = isGreen ? styles.currencyGreen : styles.currencyWhite;
        CellStyle percentStyle = isGreen ? styles.percentGreen : styles.percentWhite;

        createCell(row, 0, pe.getProducto().getCodigo(), styles.code);
        createCell(row, 1, pe.getProducto().getNumeroSerie(), styles.serie);
        createCell(row, 2, pe.getProducto().getPrecio(), currencyStyle);

        BigDecimal descuento = pe.getProducto().getDescuento();
        Double descValue = (descuento != null) ? descuento.doubleValue() / 100.0 : null;
        createCell(row, 3, descValue, percentStyle);

        createCell(row, 4, pe.getPrecioFinal(), currencyStyle);

        BigDecimal costesEnvio = (pe.getProducto().getCostes() != null) ? pe.getProducto().getCostes().getCostesEnvio() : null;
        BigDecimal costesAlmacen = (pe.getProducto().getCostes() != null) ? pe.getProducto().getCostes().getCostesAlmacenaje() : null;
        createCell(row, 5, costesEnvio, currencyStyle);
        createCell(row, 6, costesAlmacen, currencyStyle);

        createCell(row, 7, pe.getProfit(), currencyStyle);
    }

    private void autoSizeColumns(Sheet sheet) {
        for (int i = 0; i < HEADERS.length; i++) {
            sheet.autoSizeColumn(i);
            // Margen extra de 4 caracteres para evitar saltos indeseados
            sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 1024);
        }
    }

    private void createCell(Row row, int colIndex, String value, CellStyle style) {
        Cell cell = row.createCell(colIndex);
        if (value != null) {
            cell.setCellValue(value);
        }
        cell.setCellStyle(style);
    }

    private void createCell(Row row, int colIndex, BigDecimal value, CellStyle style) {
        Cell cell = row.createCell(colIndex);
        if (value != null) {
            cell.setCellValue(value.doubleValue());
        }
        cell.setCellStyle(style);
    }

    private void createCell(Row row, int colIndex, Double value, CellStyle style) {
        Cell cell = row.createCell(colIndex);
        if (value != null) {
            cell.setCellValue(value);
        }
        cell.setCellStyle(style);
    }

    private static class ExcelStyles {
        final CellStyle header;
        final CellStyle code;
        final CellStyle serie;
        final CellStyle currencyGreen;
        final CellStyle percentGreen;
        final CellStyle currencyWhite;
        final CellStyle percentWhite;

        ExcelStyles(Workbook workbook) {
            this.header = ExcelUtils.createHeaderStyle(workbook);
            this.code = ExcelUtils.createDataStyle(workbook, IndexedColors.LIGHT_GREEN, null, true, HorizontalAlignment.CENTER);
            this.serie = ExcelUtils.createDataStyle(workbook, IndexedColors.LIGHT_GREEN, null, false, HorizontalAlignment.LEFT);
            this.currencyGreen = ExcelUtils.createDataStyle(workbook, IndexedColors.LIGHT_GREEN, CURRENCY_FORMAT, false, HorizontalAlignment.RIGHT);
            this.percentGreen = ExcelUtils.createDataStyle(workbook, IndexedColors.LIGHT_GREEN, PERCENT_FORMAT, false, HorizontalAlignment.RIGHT);
            this.currencyWhite = ExcelUtils.createDataStyle(workbook, IndexedColors.WHITE, CURRENCY_FORMAT, false, HorizontalAlignment.RIGHT);
            this.percentWhite = ExcelUtils.createDataStyle(workbook, IndexedColors.WHITE, PERCENT_FORMAT, false, HorizontalAlignment.RIGHT);
        }
    }
}