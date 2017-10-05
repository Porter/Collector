package com.porter.collector.db;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableUser.class)
@JsonDeserialize(as = ImmutableUser.class)
public abstract class User {

    abstract long id();
    abstract String userName();
    abstract String hashedPassword();
    abstract String email();

    public boolean correctPassword(String pass) {
        return pass != null && pass.equals(hashedPassword());
    }
}
