package org.educa.dao;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.educa.entity.ProductoEntity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelDAOImpl implements ExcelDAO {

    // Nombres exactos de las columnas según la captura del ejercicio
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

        if (targetFile.getParentFile() != null) {
            targetFile.getParentFile().mkdirs();
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Inventario");

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < HEADERS.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(HEADERS[i]);
            }

            int rowIndex = 1;
            for (ProductoEntity pe : productos) {
                Row row = sheet.createRow(rowIndex++);

                Cell cellCodigo = row.createCell(0);
                cellCodigo.setCellValue(pe.getProducto().getCodigo());

                Cell cellSerie = row.createCell(1);
                cellSerie.setCellValue(pe.getProducto().getNumeroSerie());

                Cell cellPrecio = row.createCell(2);
                if (pe.getProducto().getPrecio() != null) {
                    cellPrecio.setCellValue(pe.getProducto().getPrecio().doubleValue());
                }

                Cell cellDescuento = row.createCell(3);
                if (pe.getProducto().getDescuento() != null) {
                    cellDescuento.setCellValue(pe.getProducto().getDescuento().doubleValue() / 100.0);
                }

                Cell cellPrecioFinal = row.createCell(4);
                if (pe.getPrecioFinal() != null) {
                    cellPrecioFinal.setCellValue(pe.getPrecioFinal().doubleValue());
                }

                Cell cellCostesEnvio = row.createCell(5);
                if (pe.getProducto().getCostes() != null && pe.getProducto().getCostes().getCostesEnvio() != null) {
                    cellCostesEnvio.setCellValue(pe.getProducto().getCostes().getCostesEnvio().doubleValue());
                }

                Cell cellCostesAlmacen = row.createCell(6);
                if (pe.getProducto().getCostes() != null && pe.getProducto().getCostes().getCostesAlmacenaje() != null) {
                    cellCostesAlmacen.setCellValue(pe.getProducto().getCostes().getCostesAlmacenaje().doubleValue());
                }

                Cell cellProfit = row.createCell(7);
                if (pe.getProfit() != null) {
                    cellProfit.setCellValue(pe.getProfit().doubleValue());
                }
            }

            for (int i = 0; i < HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream fos = new FileOutputStream(targetFile)) {
                workbook.write(fos);
            }
        }
    }
}