package com.porter.collector.db;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.porter.collector.csv.CsvInfo;
import com.porter.collector.model.*;
import com.porter.collector.values.ValueType;
import io.dropwizard.jackson.Jackson;
import org.jdbi.v3.core.statement.UnableToExecuteStatementException;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.*;

import java.io.IOException;
import java.util.*;
import java.util.Collection;

public interface CsvRowDao {

    @SqlUpdate("INSERT INTO csv (row, row_number, processed, source_id) VALUES (:row, :rowNumber, :processed, :sourceId)")
    @GetGeneratedKeys
    long executeInsert(
            @Bind("row") String row,
            @Bind("rowNumber") int rowNum,
            @Bind("processed") boolean processed,
            @Bind("sourceId") long sourceId);

    @SqlUpdate("INSERT INTO csv (row, row_number, processed, source_id) VALUES (:row, :rowNumber, :processed, :sourceId) " +
            "ON CONFLICT ON CONSTRAINT csv_source_id_row_number_key " +
            "DO UPDATE SET row=:row, row_number=:rowNumber, processed=:processed, source_id=:sourceId")
    @GetGeneratedKeys
    long executeInfoInsert(
            @Bind("row") String row,
            @Bind("rowNumber") int rowNum,
            @Bind("processed") boolean processed,
            @Bind("sourceId") long sourceId);

    @SqlBatch("INSERT INTO csv (row, row_number, processed, source_id) VALUES (:row, :rowNumber, :processed, :sourceId)")
    @GetGeneratedKeys
    List<Long> executeInsert(
            @Bind("row") List<String> row,
            @Bind("rowNumber") List<Integer> rowNum,
            @Bind("processed") List<Boolean> processed,
            @Bind("sourceId") List<Long> sourceId);

    @SqlQuery("SELECT csv.*, custom_types.type AS custom_type FROM csv " +
            "LEFT JOIN sources ON csv.source_id = sources.id " +
            "LEFT JOIN custom_types ON custom_types.id = sources.custom_type_id WHERE source_id=:sourceId AND row_number >= 0")
    @UseRowMapper(CsvRowMapper.class)
    List<CommittedCsvRow> findAllFromSource(@Bind("sourceId") Long id);

    @SqlQuery("SELECT csv.*, custom_types.type AS custom_type FROM csv " +
            "LEFT JOIN sources ON csv.source_id = sources.id " +
            "LEFT JOIN custom_types ON custom_types.id = sources.custom_type_id WHERE csv.id=:id AND row_number >= 0")
    @UseRowMapper(CsvRowMapper.class)
    CommittedCsvRow findById(@Bind("id") Long id);


    default CsvInfo insertRowsInfo(CsvInfo info) throws IllegalAccessException {
        if (info.rowCount() == 0) {
            return info;
        }
        CsvRow row = null;//info.getInfo();
        String json = _getJson(row.row());
        long id = executeInfoInsert(json, row.rowNumber(), row.processed(), row.sourceId());
        return info;
    }

    default String _getJson(Map<String, ValueType> row) {
        Map<String, String> newMap = new HashMap<>();
        row.forEach((key, value) -> newMap.put(key, value.stringify()));

        ObjectMapper mapper = Jackson.newObjectMapper();
        try {
            return mapper.writeValueAsString(newMap);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    default CommittedCsvRow insert(CsvRow row) throws IllegalAccessException {
        return insert(row.row(), row.rowNumber(), row.processed(), row.sourceId());
    }

    default CommittedCsvRow insert(Map<String, ValueType> row, int rowNum, boolean processed, long sourceId)
            throws IllegalAccessException {

        String json = _getJson(row);
        long id;
        try {
            id = executeInsert(json, rowNum, processed, sourceId);
        } catch (UnableToExecuteStatementException e) {
            if (e.getMessage().contains("violates foreign key constraint \"one_source_to_many_csv\"")) {
                throw new IllegalAccessException("You can no longer modify that source");
            }
            throw e;
        }

        return new CsvRowBuilder()
                .id(id)
                .rowNumber(rowNum)
                .row(row)
                .processed(processed)
                .sourceId(sourceId)
                .build();
    }

    default List<CommittedCsvRow> insert(List<CsvRow> rows) throws IllegalAccessException {
        List<CommittedCsvRow> committedCsvRows = new ArrayList<>();
        for (CsvRow row : rows) {
            committedCsvRows.add(insert(row));
        }
        return committedCsvRows;
    }

    default List<CommittedCsvRow> insert(List<Map<String, ValueType>> rows, List<Integer> rowNum,
                                List<Boolean> processed, List<Long> sourceId) throws IllegalAccessException {


        List<String> json = new ArrayList<>();
        rows.forEach(row -> json.add(_getJson(row)));

        int length = json.size();
        List<Collection> collections = ImmutableList.of(rowNum, processed, sourceId);
        for (Collection collection : collections) {
            if (collection.size() != length) {
                throw new IllegalArgumentException("Arguments must be of same size");
            }
        }

        List<Long> ids;
        try {
            ids = executeInsert(json, rowNum, processed, sourceId);
        } catch (UnableToExecuteStatementException e) {
            if (e.getMessage().contains("violates foreign key constraint \"one_source_to_many_csv\"")) {
                throw new IllegalAccessException("You can no longer modify that source");
            }
            throw e;
        }

        List<CommittedCsvRow> created = new ArrayList<>();
        for (int i = 0; i < ids.size(); i++) {
            created.add(new CsvRowBuilder()
                    .id(ids.get(i))
                    .rowNumber(rowNum.get(i))
                    .row(rows.get(i))
                    .processed(processed.get(i))
                    .sourceId(sourceId.get(i))
                    .build()
            );
        }

        return created;
    }
}
