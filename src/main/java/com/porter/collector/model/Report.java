package com.porter.collector.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import javax.annotation.Nullable;


@Value.Immutable
@JsonSerialize(as = ImmutableReport.class)
public abstract class Report {
    public abstract long id();
    public abstract long userId();
    public abstract String name();
    public abstract String formula();

    @Nullable
    public abstract String value();
}
