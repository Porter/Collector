package com.porter.collector.model;

import org.immutables.value.Value.Immutable;

@Immutable
public abstract class Value {
    public abstract long id();
    public abstract String value();

}
