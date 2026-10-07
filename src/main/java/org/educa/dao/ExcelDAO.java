package org.educa.dao;

import org.educa.entity.ProductoEntity;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface ExcelDAO {
    void exportExcel (List<ProductoEntity> productos, File originalFile) throws IOException;
}
