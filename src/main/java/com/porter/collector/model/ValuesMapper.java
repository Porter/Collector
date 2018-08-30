package com.porter.collector.model;


import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ValuesMapper implements RowMapper<Value> {

    @Override
    public Value map(ResultSet resultSet, StatementContext statementContext) throws SQLException {
        return ImmutableValue
                .builder()
                .id(resultSet.getLong("id"))
                .value(resultSet.getString("value"))
                .type(ValueTypes.values()[resultSet.getInt("type")])
                .build();
    }
}
