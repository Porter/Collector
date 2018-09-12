package com.porter.collector.model;


import org.immutables.value.Value;

@Value.Immutable
public abstract class UsersCustomType {

    public abstract long id();
    public abstract String type();
    public abstract long userId();
}
