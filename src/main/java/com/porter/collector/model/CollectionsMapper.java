package com.porter.collector.model;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.porter.collector.model.ImmutableCollection;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CollectionsMapper implements ResultSetMapper<ImmutableCollection> {
    @Override
    public ImmutableCollection map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
        return ImmutableCollection
                .builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .userId(resultSet.getLong("user_id"))
                .build();
    }
}
