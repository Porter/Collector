package com.porter.collector.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import javax.annotation.Nullable;

@Value.Immutable
@JsonSerialize(as = ImmutableCollection.class)
public abstract class Collection {
    public abstract long id();
    public abstract String name();
    public abstract long userId();

    @Nullable
    public abstract UserWithPassword user();
}
