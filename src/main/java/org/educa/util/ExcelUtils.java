package org.educa.util;

import org.apache.poi.ss.usermodel.*;

public class ExcelUtils {

    /**
     * Private constructor to prevent instantiation of this utility class
     */
    private ExcelUtils() {
    }

    /**
     * Creates the style used for header cells: grey background, bold text, centered and with text wrapping
     *
     * @param workbook {@link Workbook} the style will belong to
     * @return header {@link CellStyle} ready to be applied to a cell
     */
    public static CellStyle createHeaderStyle(Workbook workbook) {
        Font font = workbook.createFont();
        font.setBold(true);
        CellStyle style = baseStyle(workbook, IndexedColors.WHITE, HorizontalAlignment.CENTER);
        style.setFont(font);
        style.setWrapText(true);
        return style;
    }

    /**
     * Creates the style used for data cells with a configurable background, data format, bold text and alignment
     *
     * @param workbook   {@link Workbook} the style will belong to
     * @param background {@link IndexedColors} background color of the cell
     * @param format     Excel data format, or null to apply no format
     * @param bold       true to display the text in bold
     * @param alignment  {@link HorizontalAlignment} of the cell content
     * @return data {@link CellStyle} ready to be applied to a cell
     */
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

    /**
     * Creates the base style shared by all cells: solid background, vertical centering and thin borders on all sides
     *
     * @param workbook   {@link Workbook} the style will belong to
     * @param background {@link IndexedColors} background color of the cell
     * @param alignment  {@link HorizontalAlignment} of the cell content
     * @return base {@link CellStyle} already configured
     */
    private static CellStyle baseStyle(Workbook workbook, IndexedColors background, HorizontalAlignment alignment) {
        CellStyle style = workbook.createCellStyle();
        if (background != null) {
            style.setFillForegroundColor(background.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        style.setAlignment(alignment);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        // Bordes verdes finos (según la captura del examen)
        short greenColor = IndexedColors.GREEN.getIndex();

        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(greenColor);

        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(greenColor);

        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(greenColor);

        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(greenColor);

        return style;
    }
}