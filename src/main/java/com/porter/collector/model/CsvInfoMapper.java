package com.porter.collector.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.porter.collector.csv.CsvInfo;
import com.porter.collector.csv.ImmutableCsvInfo;
import com.porter.collector.util.ValueValidator;
import com.porter.collector.values.CustomType;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CsvInfoMapper implements RowMapper<CsvInfo> {


    private final ValueValidator validator;

    public CsvInfoMapper(ValueValidator validator) {
        this.validator = validator;
    }

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

    private CustomType customType(String result) {
        if (result == null) { return null; }
        try {
            return new CustomType().parse(result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CsvInfo map(ResultSet resultSet, StatementContext ctx) throws SQLException {
        CustomType customType = customType(resultSet.getString("type"));
        return ImmutableCsvInfo.builder()
                .columnMapping(getMap(resultSet.getString("mapping")))
                .rowCount(resultSet.getInt("row_count"))
                .sourceId(resultSet.getLong("source_id"))
                .info(validator.parseValues(getMap(resultSet.getString("info")), customType))
                .build();
    }
}