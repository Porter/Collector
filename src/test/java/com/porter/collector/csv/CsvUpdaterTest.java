package com.porter.collector.csv;

import com.google.common.collect.ImmutableMap;
import com.porter.collector.model.CsvRow;
import com.porter.collector.model.ImmutableCsvRow;
import org.junit.Test;

import java.util.List;

public class CsvUpdaterTest {

    private CsvRow getFakeRow(int rowNumber) {
        return ImmutableCsvRow.builder()
                .row(ImmutableMap.of())
                .rowNumber(rowNumber)
                .id(0)
                .processed(false)
                .sourceId(0)
                .build();
    }

    @Test
    public void newRows() throws Exception {
        CsvUpdater updater = new CsvUpdater();
        CsvRow infoRow = getFakeRow(-1);
        CsvRowsInfo info = new CsvRowsInfo(infoRow, 2);
        List<CsvRow> rows;

    }

}