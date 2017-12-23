package com.porter.collector.model;

import org.immutables.value.Value;

import javax.annotation.Nullable;

@Value.Immutable
public abstract class Delta {
    public abstract long id();
    public abstract String name();
    public abstract long collectionId();
    public abstract long sourceId();
    public abstract Long amount();

    @Nullable
    public abstract Long categoryId();
}
