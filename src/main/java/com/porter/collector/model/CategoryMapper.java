package com.porter.collector.model;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CategoryMapper implements ResultSetMapper<ImmutableCategory> {
    @Override
    public ImmutableCategory map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
        return ImmutableCategory
                .builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .collectionId(resultSet.getLong("collection_id"))
                .build();
    }
}
