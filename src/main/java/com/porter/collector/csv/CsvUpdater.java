package com.porter.collector.csv;

import com.porter.collector.model.CsvRow;
import com.porter.collector.model.ImmutableCsvRow;
import com.porter.collector.values.ValueType;

import java.util.*;

public class CsvUpdater {

    public List<CsvRow> newRows(Map<String, ValueType> oldInfo, int oldRowCount, List<CsvRow> current) {
        return current.subList(oldRowCount, current.size());
    }

    public List<CsvRow> newRows(CsvInfo info, List<CsvRow> parsedRows) {
        return newRows(info.info(), info.rowCount(), parsedRows);
    }

    public boolean isConsistent(Map<String, ValueType> oldInfo, int oldRowCount, List<CsvRow> current) {
        if (current.size() < oldRowCount) { return false; }
        if (oldRowCount == 0) { return true; }

        Map<String, ValueType> info = getInfo(current.subList(0, oldRowCount));
        return oldInfo.equals(info);
    }

    public boolean isConsistent(CsvInfo info, List<CsvRow> parsedRows) {
        return isConsistent(info.info(), info.rowCount(), parsedRows);
    }

    public Map<String, ValueType> getInfo(List<CsvRow> rows) {
        if (rows.isEmpty()) {
            return Collections.emptyMap();
        }

        Set<String> keys = rows.get(0).row().keySet();
        boolean allSame = rows.stream().allMatch(r -> r.row().keySet().equals(keys));
        if (!allSame) {
            throw new IllegalArgumentException("All rows must have the same keyset");
        }

        long sourceId = rows.get(0).sourceId();
        boolean consistentSourceId = rows.stream().allMatch(r -> r.sourceId() == sourceId);
        if (!consistentSourceId) {
            throw new IllegalArgumentException("All of the rows must have a single source id");
        }

        Map<String, ValueType> map = new HashMap<>(rows.get(0).row());
        for (int i = 1; i < rows.size(); i++) {
            for (String key : keys) {
                ValueType curr = rows.get(i).row().get(key);
                ValueType combined = map.get(key).combine(curr);
                map.put(key, combined);
            }
        }

        return map;
    }
}
