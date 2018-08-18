package com.porter.collector;

import com.porter.collector.auth.JwtAuthenticator;
import com.porter.collector.auth.JwtUnauthorizedHandler;
import com.porter.collector.auth.JwtAuthFilter;
import com.porter.collector.auth.JwtAuthorizer;
import com.porter.collector.db.CollectionDao;
import com.porter.collector.db.UserDao;
import com.porter.collector.health.BasicHealthCheck;
import com.porter.collector.model.SimpleUser;
import com.porter.collector.model.UserWithoutPassword;
import com.porter.collector.resources.CollectionResource;
import com.porter.collector.resources.UserResource;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.skife.jdbi.v2.DBI;

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

        final DBIFactory factory = new DBIFactory();
        final DBI jdbi = factory.build(environment, configuration.getDataSourceFactory(), "connection");

        UserDao userDao =  jdbi.onDemand(UserDao.class);
        CollectionDao collectionDao = jdbi.onDemand(CollectionDao.class);

        environment.jersey().register(new AuthDynamicFeature(
                new JwtAuthFilter.Builder<SimpleUser>()
                        .setAuthenticator(new JwtAuthenticator())
                        .setAuthorizer(new JwtAuthorizer())
                        .setUnauthorizedHandler(new JwtUnauthorizedHandler())
                        .buildAuthFilter()

        ));

        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(SimpleUser.class));
        environment.jersey().register(RolesAllowedDynamicFeature.class);

        environment.jersey().register(new UserResource(userDao));
        environment.jersey().register(new CollectionResource(collectionDao));

    }







}
