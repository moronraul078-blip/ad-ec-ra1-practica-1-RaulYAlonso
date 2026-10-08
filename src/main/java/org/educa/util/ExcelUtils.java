package org.educa.util;

import org.apache.poi.ss.usermodel.*;

public class ExcelUtils {

    private ExcelUtils() {
    }

    public static CellStyle createHeaderStyle(Workbook workbook) {
        Font font = workbook.createFont();
        font.setBold(true);

        CellStyle style = baseStyle(workbook, IndexedColors.GREY_25_PERCENT, HorizontalAlignment.CENTER);
        style.setFont(font);
        style.setWrapText(true); // necesario para los saltos de línea de la cabecera
        return style;
    }

    public static CellStyle createDataStyle(Workbook workbook, IndexedColors background, String format,
                                            boolean bold, HorizontalAlignment alignment) {
        CellStyle style = baseStyle(workbook, background, alignment);

        if (bold) {
            Font font = workbook.createFont();
            font.setBold(true);
            style.setFont(font);
        }
        if (format != null) {
            style.setDataFormat(workbook.createDataFormat().getFormat(format));
        }
        return style;
    }

    private static CellStyle baseStyle(Workbook workbook, IndexedColors background, HorizontalAlignment alignment) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(background.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(alignment);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
}
