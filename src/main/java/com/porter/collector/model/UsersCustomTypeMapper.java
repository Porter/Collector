package com.porter.collector.model;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.porter.collector.values.CustomType;
import io.dropwizard.jackson.Jackson;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsersCustomTypeMapper implements RowMapper<UsersCustomType> {

    private CustomType getCustomType(String json) {
        ObjectMapper objectMapper = Jackson.newObjectMapper();
        try {
            return objectMapper.readValue(json, CustomType.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UsersCustomType map(ResultSet resultSet, StatementContext statementContext) throws SQLException {
        return ImmutableUsersCustomType
                .builder()
                .id(resultSet.getLong("id"))
                .userId(resultSet.getLong("user_id"))
                .name(resultSet.getString("name"))
                .type(getCustomType(resultSet.getString("type")))
                .build();
    }
}
