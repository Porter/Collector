package com.porter.collector;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class collectorConfiguration extends Configuration {
    @Valid
    @NotNull
    @JsonProperty
    private DataSourceFactory dataSource = new DataSourceFactory();

    public DataSourceFactory getDataSourceFactory() {
        return dataSource;
    }
}
