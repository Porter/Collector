package com.porter.collector.auth;

import io.dropwizard.auth.UnauthorizedHandler;

import javax.ws.rs.core.Response;

public class JwtUnauthorizedHandler implements UnauthorizedHandler {
    @Override
    public Response buildResponse(String s, String s1) {
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }
}
