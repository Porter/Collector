package com.porter.collector.resources;

import com.google.common.collect.ImmutableMap;
import com.porter.collector.controller.SourcesController;
import com.porter.collector.model.*;
import io.dropwizard.auth.Auth;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path(Urls.SOURCE)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SourcesResource {

    private final SourcesController sourcesController;

    public SourcesResource(SourcesController sourcesController) {
        this.sourcesController = sourcesController;
    }


    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(@Auth SimpleUser user) {
        return Response.ok(sourcesController.getAllFromUser(user)).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@Auth SimpleUser user, @PathParam("id") Long id) {
        try {
            Source source = sourcesController.getById(user, id);
            if (source == null) {
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity(ImmutableMap.of("error", "Source with id " + id + " does not exist"))
                        .build();
            }
            return Response.ok(source).build();
        }
        catch (IllegalAccessException e) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(ImmutableMap.of("error", e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/{id}/values/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getValuesById(@Auth SimpleUser user, @PathParam("id") Long id) {
        try {
            return Response.ok(sourcesController.getAllValues(user, id)).build();
        }
        catch (IllegalAccessException e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    @POST
    @Path("/{id}/values/new")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response addValue(
            @Auth SimpleUser user,
            @PathParam("id") Long sourceId,
            @FormParam("amount") String value) {
        return Response
                .ok(sourcesController.addValue(user, sourceId, value))
                .build();
    }


}
