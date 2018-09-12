package com.porter.collector.db;

import com.porter.collector.model.*;
import org.jdbi.v3.core.statement.UnableToExecuteStatementException;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.statement.UseRowMapper;

import java.util.List;

public interface CustomTypeDao {

    @SqlUpdate("INSERT INTO custom_types (user_id, name, type) VALUES (:userId, :name, :type);")
    @GetGeneratedKeys
    long executeInsert(@Bind("userId") long userId,
                       @Bind("name") String name,
                       @Bind("type") String type);


    default UsersCustomType insert(long userId, String name, String type) {
        Long id;
        try {
            id = executeInsert(userId, name, type);
        } catch (UnableToExecuteStatementException e) {
            if (e.getMessage().contains("violates foreign key constraint \"one_user_to_many_custom_types\"")) {
                throw new IllegalStateException("user id does not exist: " + userId);
            }
            throw e;
        }
        return ImmutableUsersCustomType.builder()
                .id(id)
                .userId(userId)
                .type(type)
                .name(name)
                .build();
    }

    @SqlQuery("SELECT * FROM custom_types WHERE id=:id")
    @UseRowMapper(UsersCustomTypeMapper.class)
    UsersCustomType findById(@Bind("id") Long id);

    @SqlQuery("SELECT * FROM custom_types WHERE user_id=:user_id")
    @UseRowMapper(UsersCustomTypeMapper.class)
    List<UsersCustomType> findAllFromUser(@Bind("user_id") Long user_id);
}
