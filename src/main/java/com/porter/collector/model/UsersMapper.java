package com.porter.collector.model;


import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UsersMapper implements RowMapper<UserWithPassword> {

    @Override
    public UserWithPassword map(ResultSet resultSet, StatementContext statementContext) throws SQLException {
        return ImmutableUserWithPassword
                .builder()
                .id(resultSet.getLong("id"))
                .email(resultSet.getString("email"))
                .hashedPassword(resultSet.getString("password"))
                .userName(resultSet.getString("username"))
                .build();
    }
}
