package com.porter.collector.db;

import com.porter.collector.db.ImmutableUser;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

public abstract class UsersDao {

    @SqlUpdate("INSERT INTO users (username, password, name) VALUES (:username, :pw, :name)")
    @GetGeneratedKeys
    abstract long executeInsert(@Bind("username") String username, @Bind("pw") String password, @Bind("name") String name);

    public User insert(String username, String password, String name) {
        if (password.isEmpty()) {
            throw new IllegalArgumentException("Password can not be empty");
        }

        Long id = executeInsert(username, password, name);
        return ImmutableUser
                .builder()
                .id(id)
                .userName(username)
                .hashedPassword(password)
                .name(name)
                .build();
    }


    @SqlQuery("SELECT * FROM users WHERE id=:id")
    @Mapper(UsersMapper.class)
    public abstract User findById(@Bind("id") Long id);
}
