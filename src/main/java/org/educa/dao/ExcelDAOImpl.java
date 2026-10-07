package org.educa.dao;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.educa.entity.ProductoEntity;

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

    @Override
    public void exportExcel(List<ProductoEntity> productos, File targetFile) throws IOException {
        ensureDirectoryExists(targetFile);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Inventario");

            createHeaders(sheet);
            populateDataRows(sheet, productos);
            autoSizeColumns(sheet);

            try (FileOutputStream fos = new FileOutputStream(targetFile)) {
                workbook.write(fos);
            }
        }
    }

    private void ensureDirectoryExists(File targetFile) throws IOException {
        if (targetFile.getParentFile() != null) {
            //We use the modern API to ensure we manage exceptions not made by mkdirs()
            Files.createDirectories(targetFile.getParentFile().toPath());
        }
    }

    private void createHeaders(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < HEADERS.length; i++) {
            createCell(headerRow, i, HEADERS[i]);
        }
    }

    private void populateDataRows(Sheet sheet, List<ProductoEntity> productos) {
        int rowIndex = 1;
        for (ProductoEntity pe : productos) {
            Row row = sheet.createRow(rowIndex++);
            createProductRow(row, pe);
        }
    }

    private void createProductRow(Row row, ProductoEntity pe) {
        createCell(row, 0, pe.getProducto().getCodigo());
        createCell(row, 1, pe.getProducto().getNumeroSerie());
        createCell(row, 2, pe.getProducto().getPrecio());

        BigDecimal descuento = pe.getProducto().getDescuento();
        if (descuento != null) {
            createCell(row, 3, descuento.doubleValue() / 100.0);
        } else {
            createCell(row, 3, (Double) null);
        }

        createCell(row, 4, pe.getPrecioFinal());

        BigDecimal costesEnvio = (pe.getProducto().getCostes() != null) ? pe.getProducto().getCostes().getCostesEnvio() : null;
        BigDecimal costesAlmacen = (pe.getProducto().getCostes() != null) ? pe.getProducto().getCostes().getCostesAlmacenaje() : null;
        createCell(row, 5, costesEnvio);
        createCell(row, 6, costesAlmacen);

        createCell(row, 7, pe.getProfit());
    }

    private void autoSizeColumns(Sheet sheet) {
        for (int i = 0; i < HEADERS.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }


    private void createCell(Row row, int colIndex, String value) {
        Cell cell = row.createCell(colIndex);
        if (value != null) {
            cell.setCellValue(value);
        }
    }

    private void createCell(Row row, int colIndex, BigDecimal value) {
        Cell cell = row.createCell(colIndex);
        if (value != null) {
            cell.setCellValue(value.doubleValue());
        }
    }

    private void createCell(Row row, int colIndex, Double value) {
        Cell cell = row.createCell(colIndex);
        if (value != null) {
            cell.setCellValue(value);
        }
    }
}