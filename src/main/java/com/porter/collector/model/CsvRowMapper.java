package com.porter.collector.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.porter.collector.values.ValueType;
import io.dropwizard.jackson.Jackson;
import org.apache.commons.csv.CSVParser;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CsvRowMapper implements RowMapper<ImmutableCsvRow> {

    private Map<String, ValueType> getMap(String row, String customType) {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<HashMap<String, String>> typeRefRow = new TypeReference<HashMap<String, String>>() {};
        TypeReference<HashMap<String, Integer>> typeRefType = new TypeReference<HashMap<String, Integer>>() {};


        Map<String, String> obj;
        Map<String, Integer> type;

        try {
            obj = mapper.readValue(row, typeRefRow);
            type = mapper.readValue(customType, typeRefType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Map<String, ValueType> map = new HashMap<>();
        type.forEach((key, val) -> {
            ValueTypes types = ValueTypes.values()[val];
            try {
                map.put(key, ValueTypes.getMap().get(types).parse(obj.get(key)));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });


        return map;
    }

    @Override
    public ImmutableCsvRow map(ResultSet resultSet, StatementContext ctx) throws SQLException {
        int type = resultSet.getInt("source_type");
        String customType = resultSet.getString("custom_type");
        return ImmutableCsvRow
                .builder()
                .id(resultSet.getLong("id"))
                .row(getMap(resultSet.getString("row"), customType))
                .rowNumber(resultSet.getInt("row_number"))
                .processed(resultSet.getBoolean("processed"))
                .sourceId(resultSet.getLong("source_id"))
                .build();
    }
}