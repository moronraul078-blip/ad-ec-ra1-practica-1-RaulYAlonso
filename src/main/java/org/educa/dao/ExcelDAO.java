package org.educa.dao;

import org.educa.entity.ProductoEntity;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface ExcelDAO {
    /**
     * Exports a list of {@link ProductoEntity} to a .xslx file.
     *
     * @param productos list of processed products
     * @param targetFile destination file
     * @throws IOException for mistakes while writing the file
     */
    void exportExcel (List<ProductoEntity> productos, File targetFile) throws IOException;
}
