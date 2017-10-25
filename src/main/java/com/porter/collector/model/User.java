package com.porter.collector.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;
import org.mindrot.jbcrypt.BCrypt;

@Value.Immutable
@JsonSerialize(as = ImmutableUser.class)
@JsonDeserialize(as = ImmutableUser.class)
public abstract class User {

    public abstract long id();
    public abstract String userName();
    public abstract String hashedPassword();
    public abstract String email();

    public boolean correctPassword(String pass) {
        return BCrypt.checkpw(pass, hashedPassword());
    }
}
