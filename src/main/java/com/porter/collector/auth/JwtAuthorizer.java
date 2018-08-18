package com.porter.collector.auth;

import com.porter.collector.model.SimpleUser;
import com.porter.collector.model.UserWithoutPassword;
import io.dropwizard.auth.Authorizer;

public class JwtAuthorizer implements Authorizer<SimpleUser> {
    @Override
    public boolean authorize(SimpleUser user, String role) {
        System.out.println("Attempting to authorize: " + user);
        return user != null;
    }
}
