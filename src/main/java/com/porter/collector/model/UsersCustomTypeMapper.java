package com.porter.collector.model;


import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UsersCustomTypeMapper implements RowMapper<UsersCustomType> {

    @Override
    public UsersCustomType map(ResultSet resultSet, StatementContext statementContext) throws SQLException {
        return ImmutableUsersCustomType
                .builder()
                .id(resultSet.getLong("id"))
                .userId(resultSet.getLong("user_id"))
                .name(resultSet.getString("name"))
                .type(resultSet.getString("type"))
                .build();
    }
}
