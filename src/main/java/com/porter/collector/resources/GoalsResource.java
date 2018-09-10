package com.porter.collector.resources;

import com.google.common.collect.ImmutableMap;
import com.porter.collector.controller.GoalsController;
import com.porter.collector.controller.SourcesController;
import com.porter.collector.model.Goal;
import com.porter.collector.model.SimpleUser;
import com.porter.collector.model.Source;
import io.dropwizard.auth.Auth;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path(Urls.SOURCE)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GoalsResource {

    private final GoalsController goalsController;

    public GoalsResource(GoalsController goalsController) {
        this.goalsController = goalsController;
    }


    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(@Auth SimpleUser user) {
        return Response.ok(goalsController.getAllFromUser(user)).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@Auth SimpleUser user, @PathParam("id") Long id) {
        try {
            Goal goal = goalsController.getById(user, id);
            if (goal == null) {
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity(ImmutableMap.of("error", "Source with id " + id + " does not exist"))
                        .build();
            }
            return Response.ok(goal).build();
        }
        catch (IllegalAccessException e) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(ImmutableMap.of("error", e.getMessage()))
                    .build();
        }
    }
}
