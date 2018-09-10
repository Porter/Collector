package com.porter.collector.model;

import org.immutables.value.Value;

@Value.Immutable
public abstract class Goal {
    public abstract Long id();
    public abstract String name();
    public abstract Long userId();
    public abstract Long reportId();
    public abstract Float target();
    public abstract Indicator indicator();
}
