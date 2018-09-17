package com.porter.collector.csv;

import com.porter.collector.model.CsvRow;

import java.util.List;

public class CsvUpdater {

    public List<CsvRow> newRows(CsvRowsInfo old, List<CsvRow> current) {
        return current.subList(old.rowCount(), current.size());
    }

    public boolean isConsistent(CsvRows old, List<CsvRow> current) {
        if (current.size() < old.rowCount()) { return false; }

        return true;
    }

    public CsvRow getInfo(List<CsvRow> rows) {
        return null;
    }
}
