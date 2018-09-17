package com.porter.collector.csv;

import com.porter.collector.model.CsvRow;

public class CsvRowsInfo {

    private CsvRow infoRow;
    private int rowCount;

    public CsvRowsInfo(CsvRow infoRow, int count) {
        this.infoRow = infoRow;
        this.rowCount = count;
    }

    public int rowCount() {
        return rowCount;
    }

}