package com.porter.collector.db;

import com.porter.collector.errors.*;
import com.porter.collector.model.ImmutableUser;
import com.porter.collector.model.ImmutableUserWithPassword;
import com.porter.collector.model.UserWithPassword;
import com.porter.collector.model.UsersMapper;
import com.porter.collector.util.Email;
import org.mindrot.jbcrypt.BCrypt;
import org.skife.jdbi.v2.exceptions.UnableToExecuteStatementException;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

public abstract class UserDao {

    @SqlUpdate("INSERT INTO users (email, username, password) VALUES (:email, :username, :pw)")
    @GetGeneratedKeys
    abstract long executeInsert(@Bind("email") String email,
                                @Bind("username") String username,
                                @Bind("pw") String password);

    public UserWithPassword insert(String email, String username, String plainTextPassword) throws SignUpException {
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
    @Mapper(UsersMapper.class)
    public abstract UserWithPassword findById(@Bind("id") Long id);

    @SqlQuery("SELECT * FROM users WHERE username=:login OR email=:login LIMIT 1;")
    @Mapper(UsersMapper.class)
    public abstract UserWithPassword findByLogin(@Bind("login") String login);
}
