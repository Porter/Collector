package com.porter.collector.model;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GoalsMapper implements RowMapper<ImmutableGoal> {

    @Override
    public ImmutableGoal map(ResultSet resultSet, StatementContext statementContext) throws SQLException {
        return ImmutableGoal
                .builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .reportId(resultSet.getLong("report_id"))
                .userId(resultSet.getLong("user_id"))
                .target(resultSet.getFloat("target"))
                .indicator(Indicator.values()[resultSet.getInt("indicator")])
                .build();
    }
}
