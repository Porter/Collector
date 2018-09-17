package com.porter.collector.model;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.porter.collector.values.CustomType;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableUsersCustomType.class)
public abstract class UsersCustomType {

    public abstract long id();
    public abstract CustomType type();
    public abstract String name();
    public abstract long userId();
}
