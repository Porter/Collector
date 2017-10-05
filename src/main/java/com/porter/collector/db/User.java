package com.porter.collector.db;

import org.immutables.value.Value;

import javax.annotation.Nullable;

@Value.Immutable
public abstract class User {
    abstract long id();
    abstract String userName();
    abstract String hashedPassword();

    public boolean correctPassword(String pass) {
        return pass != null && pass.equals(hashedPassword());
    }
    @Nullable
    abstract String name();
}
