package com.porter.collector.auth;

import com.porter.collector.model.UserWithoutPassword;
import io.dropwizard.auth.Authorizer;

public class JwtAuthorizer implements Authorizer<UserWithoutPassword> {
    @Override
    public boolean authorize(UserWithoutPassword user, String role) {
        System.out.println("Attempting to authorize: " + user);
        return user != null;
    }
}
