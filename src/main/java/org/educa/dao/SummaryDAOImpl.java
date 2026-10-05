package org.educa.dao;

import org.educa.entity.SummaryEntity;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class SummaryDAOImpl implements SummaryDAO {

    @Override
    public void exportSummary(SummaryEntity summaryEntity, File f) throws IOException {
        String nameSummary = "result_"+summaryEntity.getName()+".txt";
        if (!f.exists()) {
            f.mkdirs();
        }
        File summaryFile = new File(f, nameSummary);
        Files.writeString(summaryFile.toPath(), summaryEntity.toPrint());
    }
}
