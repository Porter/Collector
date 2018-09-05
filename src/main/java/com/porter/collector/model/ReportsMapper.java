package com.porter.collector.model;


import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ReportsMapper implements RowMapper<Report> {

    @Override
    public Report map(ResultSet resultSet, StatementContext statementContext) throws SQLException {
        return ImmutableReport
                .builder()
                .id(resultSet.getLong("id"))
                .userId(resultSet.getLong("user_id"))
                .name(resultSet.getString("name"))
                .formula(resultSet.getString("formula"))
                .value(resultSet.getString("value"))
                .build();
    }
}
