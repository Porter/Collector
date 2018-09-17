package com.porter.collector.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CsvColumnMappingMapper implements RowMapper<ImmutableCsvColumnMapping> {

    private Map<String, String> getMap(String mapping) {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<HashMap<String,String>> typeRef
                = new TypeReference<HashMap<String,String>>() {};


        try {
            return mapper.readValue(mapping, typeRef);
        } catch (IOException e) {
            return Collections.emptyMap();
        }
    }

    @Override
    public ImmutableCsvColumnMapping map(ResultSet resultSet, StatementContext ctx) throws SQLException {
        return ImmutableCsvColumnMapping
                .builder()
                .id(resultSet.getLong("id"))
                .sourceId(resultSet.getLong("source_id"))
                .mapping(getMap(resultSet.getString("mapping")))
                .build();
    }
}