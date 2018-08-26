package com.porter.collector.model;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CollectionMapper implements RowMapper<ImmutableCollection> {

    @Override
    public ImmutableCollection map(ResultSet resultSet, StatementContext ctx) throws SQLException {
        return ImmutableCollection
                .builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .userId(resultSet.getLong("user_id"))
                .build();
    }
}