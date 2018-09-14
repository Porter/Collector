package com.porter.collector.model;

import com.porter.collector.model.Values.CustomType;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SourcesMapper implements RowMapper<ImmutableSource> {

    private ValueTypes valueType(int result) {
        if (result < 0) { return null; }
        return ValueTypes.values()[result];
    }

    private CustomType customType(String result) {
        if (result == null) { return null; }
        try {
            return new CustomType().parse(result);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public ImmutableSource map(ResultSet resultSet, StatementContext statementContext) throws SQLException {
        return ImmutableSource
                .builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .collectionId(resultSet.getLong("collection_id"))
                .userId(resultSet.getLong("user_id"))
                .type(valueType((int) resultSet.getLong("type")))
                .customType(customType(resultSet.getString("custom_type")))
                .external(resultSet.getBoolean("external"))
                .build();
    }
}
