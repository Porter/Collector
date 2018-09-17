package com.porter.collector.csv;

import com.porter.collector.model.CsvRow;
import com.porter.collector.model.ImmutableCsvRow;
import com.porter.collector.values.ValueType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CsvUpdater {

    public List<CsvRow> newRows(CsvRowsInfo old, List<CsvRow> current) {
        return current.subList(old.rowCount(), current.size());
    }

    public boolean isConsistent(CsvRowsInfo old, List<CsvRow> current) {
        if (current.size() < old.rowCount()) { return false; }

        CsvRowsInfo info = getInfo(current.subList(0, old.rowCount()));
        return old.equals(info);
    }

    public CsvRowsInfo getInfo(List<CsvRow> rows) {
        if (rows.isEmpty()) {
            return null;
        }

        Set<String> keys = rows.get(0).row().keySet();
        boolean allSame = rows.stream().allMatch(r -> r.row().keySet().equals(keys));

        if (!allSame) {
            throw new IllegalArgumentException("All rows must have the same keyset");
        }

        Map<String, ValueType> map = new HashMap<>(rows.get(0).row());
        for (int i = 1; i < rows.size(); i++) {
            for (String key : keys) {
                ValueType curr = rows.get(i).row().get(key);
                ValueType combined = map.get(key).combine(curr);
                map.put(key, combined);
            }
        }

        CsvRow infoRow = ImmutableCsvRow.builder()
                .row(map)
                .rowNumber(-1)
                .processed(true)
                .sourceId(-1)
                .id(-1)
                .build();

        return new CsvRowsInfo(infoRow, rows.size());
    }
}
