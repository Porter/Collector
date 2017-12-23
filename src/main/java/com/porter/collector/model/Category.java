package com.porter.collector.model;

import org.immutables.value.Value;

import javax.annotation.Nullable;

@Value.Immutable
public abstract class Category {
    public abstract long id();
    public abstract String name();
    public abstract Long collectionId();
}
