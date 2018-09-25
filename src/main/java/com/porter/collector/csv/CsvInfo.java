package com.porter.collector.csv;

import com.porter.collector.values.ValueType;
import org.immutables.value.Value;

import java.util.Map;
import java.util.Objects;

@Value.Immutable
public abstract class CsvInfo {
    public abstract Map<String, ValueType> info();
    public abstract int rowCount();
    public abstract Map<String, String> columnMapping();
    public abstract long sourceId();
}