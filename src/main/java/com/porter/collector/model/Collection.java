package com.porter.collector.model;

import com.porter.collector.model.User;
import org.immutables.value.Value;

import javax.annotation.Nullable;

@Value.Immutable
public abstract class Collection {
    public abstract long id();
    public abstract String name();
    public abstract long userId();

    @Nullable
    public abstract User user();
}
