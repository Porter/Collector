package com.porter.collector.csv;

import com.google.common.collect.ImmutableMap;
import com.porter.collector.model.CsvRow;
import com.porter.collector.model.ImmutableCsvRow;
import com.porter.collector.values.MyInteger;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CsvUpdaterTest {

    private CsvRow getFakeRow(int rowNumber) {
        return ImmutableCsvRow.builder()
                .row(ImmutableMap.of("int", new MyInteger(rowNumber * 3)))
                .rowNumber(rowNumber)
                .id(0)
                .processed(false)
                .sourceId(0)
                .build();
    }

    @Test
    public void isConsistent() throws Exception {
        CsvUpdater updater = new CsvUpdater();
        List<CsvRow> rows = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            rows.add(getFakeRow(i));
        }

        CsvRowsInfo info = updater.getInfo(rows);
        assertTrue(updater.isConsistent(info, rows));
    }

    @Test
    public void isConsistent2() throws Exception {
        CsvUpdater updater = new CsvUpdater();
        List<CsvRow> rows = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            rows.add(getFakeRow(i));
        }

        CsvRow infoRow = updater.getInfo(rows).getInfoRow();
        MyInteger changed  = ((MyInteger) infoRow.row().get("int")).add(new MyInteger(1));
        CsvRow modified = ImmutableCsvRow.copyOf(infoRow).withRow(ImmutableMap.of("int", changed));

        CsvRowsInfo info = new CsvRowsInfo(modified, rows.size());

        assertFalse(updater.isConsistent(info, rows));
    }

    @Test
    public void newRows() throws Exception {
        CsvUpdater updater = new CsvUpdater();
        List<CsvRow> rows1 = new ArrayList<>();
        List<CsvRow> rows2 = new ArrayList<>();
        List<CsvRow> extra = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            rows1.add(getFakeRow(i));
            rows2.add(getFakeRow(i));
        }
        for (int i = 10; i < 15; i++) {
            rows2.add(getFakeRow(i));
            extra.add(getFakeRow(i));
        }

        CsvRowsInfo info = updater.getInfo(rows1);
        assertEquals(extra, updater.newRows(info, rows2));
    }

}