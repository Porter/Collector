package com.porter.collector.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.porter.collector.model.*;
import io.dropwizard.jackson.Jackson;
import org.jdbi.v3.core.statement.UnableToExecuteStatementException;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.statement.UseRowMapper;

import java.util.List;
import java.util.Map;

public interface CsvColumnMappingDao {

    @SqlUpdate("INSERT INTO csv_column_mapping (source_id, mapping) VALUES (:sourceId, :mapping)")
    @GetGeneratedKeys
    long executeInsert(@Bind("sourceId") long sourceId, @Bind("mapping") String mapping);

    @SqlQuery("SELECT * FROM csv_column_mapping WHERE id=:id")
    @UseRowMapper(CsvColumnMappingMapper.class)
    CsvColumnMapping findById(@Bind("id") Long id);

    default CsvColumnMapping insert(long sourceId, Map<String, String> mapping) throws IllegalAccessException {
        ObjectMapper mapper = Jackson.newObjectMapper();
        String json = "{}";
        try {
            json = mapper.writeValueAsString(mapping);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        long id;
        try {
            id = executeInsert(sourceId, json);
        } catch (UnableToExecuteStatementException e) {
            if (e.getMessage().contains("violates foreign key constraint \"one_csv_column_mapping_to_one_source\"")) {
                throw new IllegalAccessException("You can no longer access that source");
            }
            throw e;
        }

        return ImmutableCsvColumnMapping
                .builder()
                .id(id)
                .sourceId(sourceId)
                .mapping(mapping)
                .build();
    }

    @SqlQuery("SELECT * FROM csv_column_mapping WHERE source_id=:sourceId")
    @UseRowMapper(CsvColumnMappingMapper.class)
    CsvColumnMapping findBySourceId(@Bind("sourceId") long sourceId);
}
