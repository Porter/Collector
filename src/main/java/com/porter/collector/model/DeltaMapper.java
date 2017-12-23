package com.porter.collector.model;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DeltaMapper implements ResultSetMapper<ImmutableDelta> {

    private Long getNullable(ResultSet resultSet, String columnLabel) throws SQLException {
        Long value = resultSet.getLong(columnLabel);
        if (resultSet.wasNull()) { return null; }
        return value;
    }

    @Override
    public ImmutableDelta map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
        return ImmutableDelta
                .builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .collectionId(resultSet.getLong("collection_id"))
                .sourceId(resultSet.getLong("source_id"))
                .amount(resultSet.getLong("amount"))
                .categoryId(getNullable(resultSet,"category_id"))
                .build();
    }
}
