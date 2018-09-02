package com.porter.collector.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value.Immutable;

import javax.annotation.Nullable;

@Immutable
@JsonSerialize(as = ImmutableValue.class)
public abstract class Value {
    public abstract long id();
    public abstract long sourceId();
    public abstract String value();

    @Nullable
    public abstract Long categoryId();

}
