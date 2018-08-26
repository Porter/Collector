package com.porter.collector.model;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SourcesMapper implements RowMapper<ImmutableSource> {

    @Override
    public ImmutableSource map(ResultSet resultSet, StatementContext statementContext) throws SQLException {
        return ImmutableSource
                .builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .collectionId(resultSet.getLong("collection_id"))
                .userId(resultSet.getLong("user_id"))
                .type(ValueTypes.values()[(int) resultSet.getLong("type")])
                .build();
    }
}
