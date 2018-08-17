package com.porter.collector;

import com.bazaarvoice.dropwizard.assets.ConfiguredAssetsBundle;
import com.porter.collector.db.CollectionDao;
import com.porter.collector.db.UserDao;
import com.porter.collector.health.BasicHealthCheck;
import com.porter.collector.model.ImmutableUser;
import com.porter.collector.model.ImmutableUserWithPassword;
import com.porter.collector.model.UserWithPassword;
import com.porter.collector.resources.CollectionResource;
import com.porter.collector.resources.UserResource;
import io.dropwizard.Application;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.skife.jdbi.v2.DBI;

import java.util.Optional;

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
        bootstrap.addBundle(new ConfiguredAssetsBundle("/app", "/", "index.html"));

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

        environment.jersey().register(new UserResource(userDao));
        environment.jersey().register(new CollectionResource(collectionDao));

    }

    public class ExampleAuthenticator implements Authenticator<BasicCredentials, UserWithPassword> {
        @Override
        public Optional<UserWithPassword> authenticate(BasicCredentials credentials) throws AuthenticationException {
            if ("secret".equals(credentials.getPassword())) {
                UserWithPassword user = ImmutableUserWithPassword.builder()
                        .userName("fake")
                        .email("fake@fake.com")
                        .id(0L)
                        .hashedPassword("")
                        .build();
                return Optional.of(user);
            }
            return Optional.empty();
        }
    }

}
