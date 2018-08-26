package com.porter.collector.model;


import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CategoryMapper implements RowMapper<ImmutableCategory> {

    @Override
    public ImmutableCategory map(ResultSet resultSet, StatementContext statementContext) throws SQLException {
        return ImmutableCategory
                .builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .collectionId(resultSet.getLong("collection_id"))
                .build();
    }
}
