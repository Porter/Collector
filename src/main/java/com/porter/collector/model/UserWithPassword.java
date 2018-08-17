package com.porter.collector.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;
import org.mindrot.jbcrypt.BCrypt;

@Value.Immutable
@JsonSerialize(as = ImmutableUserWithPassword.class)
@JsonDeserialize(as = ImmutableUserWithPassword.class)
public abstract class UserWithPassword extends SimpleUser {

    public abstract String hashedPassword();

    public boolean correctPassword(String pass) {
        return BCrypt.checkpw(pass, hashedPassword());
    }
}
