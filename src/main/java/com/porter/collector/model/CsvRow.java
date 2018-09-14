package com.porter.collector.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import javax.annotation.Nullable;


@Value.Immutable
@JsonSerialize(as = ImmutableCsvRow.class)
public abstract class CsvRow {
    public abstract long id();
    public abstract long sourceId();
    public abstract String row();
    public abstract int rowNumber();
    public abstract boolean processed();
}
