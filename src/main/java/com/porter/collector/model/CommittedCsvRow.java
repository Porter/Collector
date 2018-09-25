package com.porter.collector.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.porter.collector.values.ValueType;
import org.immutables.builder.Builder;
import org.immutables.value.Value;

import java.util.Map;


@Value.Immutable
@JsonSerialize(as = ImmutableCommittedCsvRow.class)
public abstract class CommittedCsvRow {
    public abstract long id();
    public abstract CsvRow csvRow();

    @Builder.Factory
    public static ImmutableCommittedCsvRow csvRow(long id, long sourceId, Map<String, ValueType> row,
                                                   int rowNumber, boolean processed) {
        return ImmutableCommittedCsvRow.builder()
                .id(id)
                .csvRow(ImmutableCsvRow.builder()
                        .row(row)
                        .rowNumber(rowNumber)
                        .processed(processed)
                        .sourceId(sourceId)
                        .build()
                )
                .build();
    }
}
