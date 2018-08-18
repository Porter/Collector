package com.porter.collector.auth;

import com.porter.collector.model.JWTUser;
import com.porter.collector.model.SimpleUser;
import com.porter.collector.model.UserWithoutPassword;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;

import java.util.Optional;

public class JwtAuthenticator implements Authenticator<String, SimpleUser> {
    @Override
    public Optional<SimpleUser> authenticate(String jwt) {
        SimpleUser user = JWTUser.fromJWT(jwt);
        System.out.println("Got user: " + user);
        return Optional.ofNullable(user);
    }
}
