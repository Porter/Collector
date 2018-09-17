package com.porter.collector.db;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.porter.collector.model.*;
import com.porter.collector.parser.Parser;
import com.porter.collector.values.ValueType;
import io.dropwizard.jackson.Jackson;
import org.jdbi.v3.core.statement.UnableToExecuteStatementException;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.statement.UseRowMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface CsvRowDao {

    @SqlUpdate("INSERT INTO csv (row, row_number, processed, source_id) VALUES (:row, :rowNumber, :processed, :sourceId)")
    @GetGeneratedKeys
    long executeInsert(
            @Bind("row") String row,
            @Bind("rowNumber") int rowNum,
            @Bind("processed") boolean processed,
            @Bind("sourceId") long sourceId);

    @SqlQuery("SELECT csv.*, sources.type AS source_type, custom_types.type AS custom_type FROM csv " +
            "LEFT JOIN sources ON csv.source_id = sources.id " +
            "LEFT JOIN custom_types ON custom_types.id = sources.custom_type_id WHERE source_id=:sourceId")
    @UseRowMapper(CsvRowMapper.class)
    List<CsvRow> findAllFromSource(@Bind("sourceId") Long id);

    @SqlQuery("SELECT csv.*, sources.type AS source_type, custom_types.type AS custom_type FROM csv " +
            "LEFT JOIN sources ON csv.source_id = sources.id " +
            "LEFT JOIN custom_types ON custom_types.id = sources.custom_type_id WHERE csv.id=:id")
    @UseRowMapper(CsvRowMapper.class)
    CsvRow findById(@Bind("id") Long id);

    default CsvRow insert(Map<String, ValueType> row, int rowNum, boolean processed, long sourceId)
            throws IllegalAccessException {

        Map<String, String> newMap = new HashMap<>();
        row.forEach((key, value) -> newMap.put(key, value.stringify()));

        ObjectMapper mapper = Jackson.newObjectMapper();
        String json;
        try {
            json = mapper.writeValueAsString(newMap);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        long id;
        try {
            id = executeInsert(json, rowNum, processed, sourceId);
        } catch (UnableToExecuteStatementException e) {
            if (e.getMessage().contains("violates foreign key constraint \"one_source_to_many_csv\"")) {
                throw new IllegalAccessException("You can no longer modify that source");
            }
            throw e;
        }

        return ImmutableCsvRow
                .builder()
                .id(id)
                .rowNumber(rowNum)
                .row(row)
                .processed(processed)
                .sourceId(sourceId)
                .build();
    }
}
