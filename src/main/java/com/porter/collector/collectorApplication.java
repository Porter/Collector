package com.porter.collector;

import com.porter.collector.auth.JwtAuthenticator;
import com.porter.collector.auth.JwtUnauthorizedHandler;
import com.porter.collector.auth.JwtAuthFilter;
import com.porter.collector.auth.JwtAuthorizer;
import com.porter.collector.db.*;
import com.porter.collector.health.BasicHealthCheck;
import com.porter.collector.model.SimpleUser;
import com.porter.collector.resources.CollectionsResource;
import com.porter.collector.resources.SourcesResource;
import com.porter.collector.resources.UsersResource;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.db.PooledDataSourceFactory;
import org.jdbi.v3.core.Jdbi;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

public class collectorApplication extends Application<collectorConfiguration> {

    public static void main(final String[] args) throws Exception {
        new collectorApplication().run(args);
    }

    @Override
    public String getName() {
        return "collector";
    }

    @Override
    public void initialize(final Bootstrap<collectorConfiguration> bootstrap) {
        bootstrap.addBundle(new AssetsBundle("/app", "/", "index.html"));

        bootstrap.addBundle(new MigrationsBundle<collectorConfiguration>() {
            @Override
            public PooledDataSourceFactory getDataSourceFactory(collectorConfiguration collectorConfiguration) {
                return collectorConfiguration.getDataSourceFactory();
            }
        });
    }

    @Override
    public void run(final collectorConfiguration configuration, final Environment environment) {
        final BasicHealthCheck healthCheck = new BasicHealthCheck();
        environment.healthChecks().register("basic", healthCheck);

        Jdbi jdbi = Jdbi.create(configuration.getDataSourceFactory().getUrl());
        jdbi.installPlugin(new SqlObjectPlugin());

        UserDao userDao =  jdbi.onDemand(UserDao.class);
        CollectionDao collectionDao = jdbi.onDemand(CollectionDao.class);
        SourceDao sourceDao = jdbi.onDemand(SourceDao.class);
        DeltaDao deltaDao = jdbi.onDemand(DeltaDao.class);
        ValueDao valueDao = jdbi.onDemand(ValueDao.class);

        environment.jersey().register(new AuthDynamicFeature(
                new JwtAuthFilter.Builder<SimpleUser>()
                        .setAuthenticator(new JwtAuthenticator())
                        .setAuthorizer(new JwtAuthorizer())
                        .setUnauthorizedHandler(new JwtUnauthorizedHandler())
                        .buildAuthFilter()

        ));

        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(SimpleUser.class));
        environment.jersey().register(RolesAllowedDynamicFeature.class);

        environment.jersey().register(new UsersResource(userDao));
        environment.jersey().register(new CollectionsResource(collectionDao, sourceDao));
        environment.jersey().register(new SourcesResource(sourceDao, deltaDao));

    }
}
