package com.porter.collector.model;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SourcesMapper implements ResultSetMapper<ImmutableSource> {
    @Override
    public ImmutableSource map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
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
