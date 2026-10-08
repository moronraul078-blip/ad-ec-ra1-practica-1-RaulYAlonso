package org.educa.dao;

import org.educa.entity.SummaryEntity;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class SummaryDAOImpl implements SummaryDAO {

    /**
     * Writes the text generated in the SummaryEntity.toPrint to the file, creating the parent directories if
     * they don't exist and overwriting the file if it already exists
     *
     * @param summaryEntity {@link SummaryEntity} whose text representation will be saved
     * @param f destination {@link File}
     * @throws IOException if the directories can't be created or the file can't be written
     */
    @Override
    public void exportSummary(SummaryEntity summaryEntity, File f) throws IOException {
        File parentDir = f.getParentFile();

        if (parentDir != null) {
            Files.createDirectories(parentDir.toPath());
        }

        Files.writeString(f.toPath(), summaryEntity.toPrint());
    }
}
