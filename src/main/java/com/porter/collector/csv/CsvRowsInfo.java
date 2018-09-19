package com.porter.collector.csv;

import com.porter.collector.model.CsvRow;

import java.util.Objects;

public class CsvRowsInfo {

    private CsvRow infoRow;
    private int rowCount;

    public CsvRowsInfo(CsvRow infoRow, int count) {
        if (count > 0) {
            Objects.requireNonNull(infoRow);
        }
        this.infoRow = infoRow;
        this.rowCount = count;
    }

    public int rowCount() {
        return rowCount;
    }

    public CsvRow getInfoRow() {
        return infoRow;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CsvRowsInfo)) return false;
        CsvRowsInfo info = (CsvRowsInfo) o;
        return rowCount == info.rowCount && Objects.equals(infoRow, info.infoRow);
    }

    @Override
    public int hashCode() {
        return Objects.hash(infoRow, rowCount);
    }
}