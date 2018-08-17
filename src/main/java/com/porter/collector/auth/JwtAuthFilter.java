package com.porter.collector.auth;

import io.dropwizard.auth.AuthFilter;
import io.dropwizard.auth.AuthenticationException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.SecurityContext;
import java.io.IOException;
import java.security.Principal;
import java.util.Optional;

public class JwtAuthFilter<P extends Principal> extends AuthFilter<String, P> {

    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {

        String jwt = containerRequestContext.getHeaderString("jwt");

        Optional<P> p = Optional.empty();
        try { p = authenticator.authenticate(jwt); }
        catch (AuthenticationException ignored) { }


        if (p.isPresent()) {
            final P p2 = p.get();
            containerRequestContext.setSecurityContext(new SecurityContext() {
                @Override
                public Principal getUserPrincipal() {
                    return p2;
                }

                @Override
                public boolean isUserInRole(String role) {
                    return authorizer.authorize(p2, role);
                }

                @Override
                public boolean isSecure() {
                    return containerRequestContext.getSecurityContext().isSecure();
                }

                @Override
                public String getAuthenticationScheme() {
                    return SecurityContext.CLIENT_CERT_AUTH;
                }
            });
        }

        throw new WebApplicationException(unauthorizedHandler.buildResponse(prefix, realm));
    }

    public static class Builder<P extends Principal> extends AuthFilterBuilder<String, P, JwtAuthFilter<P>> {
        @Override
        protected JwtAuthFilter<P> newInstance() {
            return new JwtAuthFilter<P>();
        }
    }
}
