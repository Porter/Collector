package com.porter.collector.model;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CsvRowMapper implements RowMapper<ImmutableCsvRow> {

    @Override
    public ImmutableCsvRow map(ResultSet resultSet, StatementContext ctx) throws SQLException {
        return ImmutableCsvRow
                .builder()
                .id(resultSet.getLong("id"))
                .row(resultSet.getString("row"))
                .rowNumber(resultSet.getInt("rowNumber"))
                .processed(resultSet.getBoolean("processed"))
                .sourceId(resultSet.getLong("source_id"))
                .build();
    }
}