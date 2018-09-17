package com.porter.collector.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.Map;


@Value.Immutable
@JsonSerialize(as = ImmutableCsvColumnMapping.class)
public abstract class CsvColumnMapping {
    public abstract long id();
    public abstract long sourceId();
    public abstract Map<String, String> mapping();
}
