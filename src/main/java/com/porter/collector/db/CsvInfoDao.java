package com.porter.collector.db;

import com.porter.collector.csv.CsvInfo;
import com.porter.collector.csv.ImmutableCsvInfo;
import com.porter.collector.model.CsvInfoMapper;
import com.porter.collector.util.ValueValidator;
import com.porter.collector.values.ValueType;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.UnableToExecuteStatementException;

import java.util.Map;

public class CsvInfoDao {

    private final Jdbi jdbi;
    private final CsvInfoMapper csvInfoMapper;
    private final ValueValidator validator;

    public CsvInfoDao(Jdbi jdbi, CsvInfoMapper csvInfoMapper, ValueValidator validator) {
        this.jdbi = jdbi;
        this.csvInfoMapper = csvInfoMapper;
        this.validator = validator;
    }

    public CsvInfo insert(long sourceId, Map<String, String> mapping, Map<String, ValueType> info, int rowCount)
            throws IllegalAccessException {
        String mappingJson = validator.toJson(mapping);
        String infoJson = validator.stringRepr(info);

        try {
            jdbi.withHandle(handle ->
                    handle.createUpdate("INSERT INTO csv_info (source_id, mapping, info, row_count) " +
                            "VALUES (:sourceId, :mapping, :info, :rowCount)")
                            .bind("sourceId", sourceId)
                            .bind("mapping", mappingJson)
                            .bind("info", infoJson)
                            .bind("rowCount", rowCount)
                            .executeAndReturnGeneratedKeys("id")
                            .mapTo(Long.class)
                            .findOnly()
            );
        } catch (UnableToExecuteStatementException e) {
            if (e.getMessage().contains("violates foreign key constraint \"one_csv_column_mapping_to_one_source\"")) {
                throw new IllegalAccessException("You can no longer access that source");
            }
            throw e;
        }

        return ImmutableCsvInfo.builder()
                .columnMapping(mapping)
                .info(info)
                .sourceId(sourceId)
                .rowCount(rowCount)
                .build();
    }

    public CsvInfo insert(CsvInfo info) throws IllegalAccessException {
        return insert(info.sourceId(), info.columnMapping(), info.info(), info.rowCount());
    }

    public CsvInfo findBySourceId(long sourceId) {
        return jdbi.withHandle(handle -> handle.createQuery("SELECT csv_info.*, custom_types.type FROM csv_info " +
                "LEFT JOIN sources ON sources.id=csv_info.source_id " +
                "LEFT JOIN custom_types ON custom_types.id=sources.custom_type_id WHERE source_id=:sourceId")

                .bind("sourceId", sourceId)
                .map(csvInfoMapper)
                .findOnly());
    }

    public void updateCsvInfo(long sourceId, Map<String, ValueType> newInfo, int rowCount) {
        String json = validator.stringRepr(newInfo);

        jdbi.withHandle(handle -> handle
                .createUpdate("UPDATE csv_info SET info=:info, row_count=:rowCount WHERE source_id=:sourceId")
                .bind("info", json)
                .bind("rowCount", rowCount)
                .bind("sourceId", sourceId)
                .execute()
        );
    }
}
