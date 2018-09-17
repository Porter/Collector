package com.porter.collector.csv;

import com.porter.collector.model.CsvRow;

import java.util.List;

public class CsvRows {

    private List<CsvRow> rows;

    public CsvRows(List<CsvRow> rows) {
        this.rows = rows;
    }


    public int rowCount() {
        return rows.size();
    }


}