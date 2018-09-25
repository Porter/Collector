package com.porter.collector.db;

import com.porter.collector.csv.CsvInfo;
import com.porter.collector.csv.ImmutableCsvInfo;
import com.porter.collector.model.*;
import com.porter.collector.util.ValueUtil;
import com.porter.collector.values.ValueType;
import org.jdbi.v3.core.statement.UnableToExecuteStatementException;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.statement.UseRowMapper;

import java.util.Map;

public interface CsvInfoDao {

    @SqlUpdate("INSERT INTO csv_info (source_id, mapping, info, row_count) " +
            "VALUES (:sourceId, :mapping, :info, :rowCount)")
    @GetGeneratedKeys
    long executeInsert(
            @Bind("sourceId") long sourceId,
            @Bind("mapping") String mapping,
            @Bind("info") String info,
            @Bind("rowCount") long rowCount);

    @SqlUpdate("UPDATE csv_info SET info=:info, row_count=:rowCount WHERE source_id=:sourceId")
    void executeUpdate(@Bind("sourceId") long sourceId, @Bind("info") String info, @Bind("rowCount") int rowCount);

    default CsvInfo insert(long sourceId, Map<String, String> mapping, Map<String, ValueType> info, int rowCount)
            throws IllegalAccessException {
        String mappingJson = ValueUtil.toJson(mapping);
        String infoJson = ValueUtil.stringifyValues(info);

        long id;
        try {
            id = executeInsert(sourceId, mappingJson, infoJson, rowCount);
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

    default CsvInfo insert(CsvInfo info) throws IllegalAccessException {
        String mappingJson = ValueUtil.toJson(info.columnMapping());
        String infoJson = ValueUtil.stringifyValues(info.info());

        long id;
        try {
            id = executeInsert(info.sourceId(), mappingJson, infoJson, info.rowCount());
        } catch (UnableToExecuteStatementException e) {
            if (e.getMessage().contains("violates foreign key constraint \"one_csv_column_mapping_to_one_source\"")) {
                throw new IllegalAccessException("You can no longer access that source");
            }
            throw e;
        }

        return info;
    }

    @SqlQuery("SELECT csv_info.*, custom_types.type FROM csv_info " +
            "LEFT JOIN sources ON sources.id=csv_info.source_id " +
            "LEFT JOIN custom_types ON custom_types.id=sources.custom_type_id WHERE source_id=:sourceId")
    @UseRowMapper(CsvInfoMapper.class)
    CsvInfo findBySourceId(@Bind("sourceId") long sourceId);

    default void updateCsvInfo(long sourceId, Map<String, ValueType> newInfo, int rowCount) {
        String json = ValueUtil.stringifyValues(newInfo);
        executeUpdate(sourceId, json, rowCount);
    }
}
