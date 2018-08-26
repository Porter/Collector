package com.porter.collector.db;

import com.porter.collector.errors.*;
import com.porter.collector.model.ImmutableUserWithPassword;
import com.porter.collector.model.UserWithPassword;
import com.porter.collector.model.UsersMapper;
import com.porter.collector.util.Email;
import org.jdbi.v3.core.statement.UnableToExecuteStatementException;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.statement.UseRowMapper;
import org.mindrot.jbcrypt.BCrypt;

import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;

public interface UserDao {

    @SqlUpdate("INSERT INTO users (email, username, password) VALUES (:email, :username, :pw)")
    @GetGeneratedKeys
    long executeInsert(@Bind("email") String email,
                                @Bind("username") String username,
                                @Bind("pw") String password);

    default UserWithPassword insert(String email, String username, String plainTextPassword) throws SignUpException {
        if (plainTextPassword.isEmpty()) {
            throw new SignUpException(new IllegalArgumentException("Password can not be empty"));
        }
        if (!Email.isValidAddress(email)) {
            throw new InvalidEmailException();
        }
        if (username.isEmpty() || Email.isValidAddress(username)) {
            throw new InvalidUserNameException();
        }

        String hashedPassword = BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());

        Long id;
        try {
            id = executeInsert(email, username, hashedPassword);
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
        return ImmutableUserWithPassword
                .builder()
                .id(id)
                .userName(username)
                .hashedPassword(hashedPassword)
                .email(email)
                .build();
    }


    @SqlQuery("SELECT * FROM users WHERE id=:id")
    @UseRowMapper(UsersMapper.class)
    UserWithPassword findById(@Bind("id") Long id);

    @SqlQuery("SELECT * FROM users WHERE username=:login OR email=:login LIMIT 1;")
    @UseRowMapper(UsersMapper.class)
    UserWithPassword findByLogin(@Bind("login") String login);
}
