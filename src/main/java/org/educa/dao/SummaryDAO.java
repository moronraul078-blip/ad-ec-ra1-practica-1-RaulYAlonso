package org.educa.dao;

import org.educa.entity.SummaryEntity;

import java.io.File;
import java.io.IOException;

public interface SummaryDAO {
    void exportSummary(SummaryEntity summaryEntity, File f) throws IOException;
}
