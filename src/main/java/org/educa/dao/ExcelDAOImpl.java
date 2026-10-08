package org.educa.dao;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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

            CellStyle headerStyle = ExcelUtils.createHeaderStyle(workbook);

            CellStyle codeStyle = ExcelUtils.createDataStyle(
                    workbook, IndexedColors.LIGHT_GREEN, null, true, HorizontalAlignment.CENTER);
            CellStyle serieStyle = ExcelUtils.createDataStyle(
                    workbook, IndexedColors.LIGHT_GREEN, null, false, HorizontalAlignment.LEFT);

            CellStyle currencyGreen = ExcelUtils.createDataStyle(
                    workbook, IndexedColors.LIGHT_GREEN, CURRENCY_FORMAT, false, HorizontalAlignment.RIGHT);
            CellStyle percentGreen = ExcelUtils.createDataStyle(
                    workbook, IndexedColors.LIGHT_GREEN, PERCENT_FORMAT, false, HorizontalAlignment.RIGHT);

            CellStyle currencyWhite = ExcelUtils.createDataStyle(
                    workbook, IndexedColors.WHITE, CURRENCY_FORMAT, false, HorizontalAlignment.RIGHT);
            CellStyle percentWhite = ExcelUtils.createDataStyle(
                    workbook, IndexedColors.WHITE, PERCENT_FORMAT, false, HorizontalAlignment.RIGHT);

            createHeaders(sheet, headerStyle);
            populateDataRows(sheet, productos, codeStyle, serieStyle,
                    currencyGreen, percentGreen, currencyWhite, percentWhite);

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

    private void populateDataRows(Sheet sheet, List<ProductoEntity> productos,
                                  CellStyle codeStyle, CellStyle serieStyle,
                                  CellStyle currencyGreen, CellStyle percentGreen,
                                  CellStyle currencyWhite, CellStyle percentWhite) {
        int rowIndex = 1;
        for (ProductoEntity pe : productos) {
            Row row = sheet.createRow(rowIndex);

            boolean isGreenRow = (rowIndex % 2 != 0);
            CellStyle currentCurrencyStyle = isGreenRow ? currencyGreen : currencyWhite;
            CellStyle currentPercentStyle = isGreenRow ? percentGreen : percentWhite;

            createProductRow(row, pe, codeStyle, serieStyle, currentCurrencyStyle, currentPercentStyle);
            rowIndex++;
        }
    }

    private void createProductRow(Row row, ProductoEntity pe,
                                  CellStyle codeStyle, CellStyle serieStyle,
                                  CellStyle currencyStyle, CellStyle percentStyle) {

        createCell(row, 0, pe.getProducto().getCodigo(), codeStyle);

        createCell(row, 1, pe.getProducto().getNumeroSerie(), serieStyle);

        createCell(row, 2, pe.getProducto().getPrecio(), currencyStyle);

        BigDecimal descuento = pe.getProducto().getDescuento();
        if (descuento != null) {
            createCell(row, 3, descuento.doubleValue() / 100.0, percentStyle);
        } else {
            createCell(row, 3, (Double) null, percentStyle);
        }

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
}