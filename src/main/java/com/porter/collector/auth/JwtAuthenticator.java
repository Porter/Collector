package com.porter.collector.auth;

import com.porter.collector.model.JWTUser;
import com.porter.collector.model.UserWithoutPassword;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;

import java.util.Optional;

public class JwtAuthenticator implements Authenticator<String, UserWithoutPassword> {
    @Override
    public Optional<UserWithoutPassword> authenticate(String jwt) throws AuthenticationException {
        UserWithoutPassword user = JWTUser.fromJWT(jwt);
        return Optional.ofNullable(user);
    }
}
