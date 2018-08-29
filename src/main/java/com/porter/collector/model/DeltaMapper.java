package com.porter.collector.model;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DeltaMapper implements RowMapper<ImmutableDelta> {

    private Long getNullable(ResultSet resultSet, String columnLabel) throws SQLException {
        Long value = resultSet.getLong(columnLabel);
        if (resultSet.wasNull()) { return null; }
        return value;
    }

    @Override
    public ImmutableDelta map(ResultSet resultSet, StatementContext statementContext) throws SQLException {
        return ImmutableDelta
                .builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .collectionId(resultSet.getLong("collection_id"))
                .sourceId(resultSet.getLong("source_id"))
                .value(resultSet.getString("value"))
                .categoryId(getNullable(resultSet,"category_id"))
                .valueId(resultSet.getLong("value_id"))
                .build();
    }
}
