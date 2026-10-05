package org.educa.dao;

import org.educa.entity.SummaryEntity;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class SummaryDAOImpl implements SummaryDAO {

    @Override
    public void exportSummary(SummaryEntity summaryEntity, File f) throws IOException {
        File parentDir = f.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        Files.writeString(f.toPath(), summaryEntity.toPrint());
    }
}
