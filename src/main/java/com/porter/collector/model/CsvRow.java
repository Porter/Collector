package com.porter.collector.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.porter.collector.values.ValueType;
import org.immutables.value.Value;

import java.util.Map;


@Value.Immutable
@JsonSerialize(as = ImmutableCsvRow.class)
public abstract class CsvRow {
    public abstract long sourceId();
    public abstract Map<String, ValueType> row();
    public abstract int rowNumber();
    public abstract boolean processed();
}
