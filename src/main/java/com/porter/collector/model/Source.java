package com.porter.collector.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.porter.collector.model.Values.CustomType;
import org.immutables.value.Value;

import javax.annotation.Nullable;


@Value.Immutable
@JsonSerialize(as = ImmutableSource.class)
public abstract class Source {
    public abstract long id();
    public abstract String name();
    public abstract long collectionId();
    public abstract long userId();

    @Nullable
    public abstract ValueTypes type();
    @Nullable
    public abstract CustomType customType();
}
