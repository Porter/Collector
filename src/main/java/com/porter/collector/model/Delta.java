package com.porter.collector.model;

import org.immutables.value.Value;

import javax.annotation.Nullable;

@Value.Immutable
public abstract class Delta {
    public abstract long id();
    public abstract String name();
    public abstract long collectionId();

    @Nullable
    public abstract Collection collection();
}
