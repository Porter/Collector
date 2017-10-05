package com.porter.collector;

import com.bazaarvoice.dropwizard.assets.AssetsBundleConfiguration;
import com.bazaarvoice.dropwizard.assets.AssetsConfiguration;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class collectorConfiguration extends Configuration implements AssetsBundleConfiguration {
    @Valid
    @NotNull
    @JsonProperty
    private final DataSourceFactory dataSource = new DataSourceFactory();

    @Valid
    @NotNull
    @JsonProperty
    private final AssetsConfiguration assets = new AssetsConfiguration();

    public DataSourceFactory getDataSourceFactory() {
        return dataSource;
    }

    @Override
    public AssetsConfiguration getAssetsConfiguration() {
        return assets;
    }
}
