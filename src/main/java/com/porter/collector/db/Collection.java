package com.porter.collector.db;

import org.immutables.value.Value;

import javax.annotation.Nullable;

@Value.Immutable
public abstract class Collection {
    abstract long id();
    abstract String name();
    abstract long userId();

    @Nullable
    abstract User user();
}
