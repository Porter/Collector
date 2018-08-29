package com.porter.collector.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;


@Value.Immutable
@JsonSerialize(as = ImmutableSource.class)
public abstract class Source {
    public abstract long id();
    public abstract String name();
    public abstract long collectionId();
    public abstract long userId();
    public abstract ValueTypes type();
}
