package com.porter.collector.db;

import com.porter.collector.db.ImmutableUser;
import com.porter.collector.errors.EmailExistsException;
import com.porter.collector.errors.UserNameExistsException;
import org.skife.jdbi.v2.exceptions.UnableToExecuteStatementException;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

public abstract class UsersDao {

    @SqlUpdate("INSERT INTO users (email, username, password) VALUES (:email, :username, :pw)")
    @GetGeneratedKeys
    abstract long executeInsert(@Bind("email") String email,
                                @Bind("username") String username,
                                @Bind("pw") String password);

    public User insert(String email, String username, String password) {
        if (password.isEmpty()) {
            throw new IllegalArgumentException("Password can not be empty");
        }

        Long id = null;
        try {
            id = executeInsert(email, username, password);
        }
        catch (UnableToExecuteStatementException e) {
            String message = e.getMessage();
            if (message.contains("duplicate key value violates unique constraint \"users_username_key\"")) {
                throw new UserNameExistsException(e);
            }
            else if (message.contains("duplicate key value violates unique constraint \"users_email_key\"")) {
                throw new EmailExistsException(e);
            }

            throw e;
        }
        return ImmutableUser
                .builder()
                .id(id)
                .userName(username)
                .hashedPassword(password)
                .email(email)
                .build();
    }


    @SqlQuery("SELECT * FROM users WHERE id=:id")
    @Mapper(UsersMapper.class)
    public abstract User findById(@Bind("id") Long id);
}
