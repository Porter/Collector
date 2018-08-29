package com.porter.collector.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import javax.annotation.Nullable;

@Value.Immutable
@JsonSerialize(as = ImmutableDelta.class)
public abstract class Delta {
    public abstract long id();
    public abstract String name();
    public abstract long collectionId();
    public abstract long sourceId();
    public abstract String value();

    @Nullable
    public abstract Long categoryId();

    @Nullable
    public abstract Long valueId();
}
