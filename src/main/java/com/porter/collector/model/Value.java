package com.porter.collector.model;

import org.immutables.value.Value.Immutable;

import javax.annotation.Nullable;

@Immutable
public abstract class Value {
    public abstract long id();
    public abstract long sourceId();
    public abstract String value();

    @Nullable
    public abstract Long categoryId();

}
