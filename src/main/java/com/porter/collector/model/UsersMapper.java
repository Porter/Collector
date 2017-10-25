package com.porter.collector.model;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UsersMapper implements ResultSetMapper<User>{
    @Override
    public User map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
        return ImmutableUser
                .builder()
                .id(resultSet.getLong("id"))
                .email(resultSet.getString("email"))
                .hashedPassword(resultSet.getString("password"))
                .userName(resultSet.getString("username"))
                .build();
    }
}