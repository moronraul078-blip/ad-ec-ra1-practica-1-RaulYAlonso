package org.educa.dao;

import org.educa.entity.SummaryEntity;

import java.io.File;
import java.io.IOException;

public interface SummaryDAO {

    /**
     * Exports the summary to the given file
     *
     * @param summaryEntity {@link SummaryEntity} with the summary data to export
     * @param f destination {@link File} where the summary will be written
     * @throws IOException if the directories can't be created or the file can't be written
     */
    void exportSummary(SummaryEntity summaryEntity, File f) throws IOException;
}
