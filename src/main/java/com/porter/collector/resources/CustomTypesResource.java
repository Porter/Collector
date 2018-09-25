package com.porter.collector.resources;

import com.google.common.collect.ImmutableMap;
import com.porter.collector.controller.CustomTypesController;
import com.porter.collector.model.SimpleUser;
import com.porter.collector.model.UsersCustomType;
import io.dropwizard.auth.Auth;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.ParseException;
import java.util.List;

@Path(Urls.CUSTOM_TYPES)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomTypesResource {

    private final CustomTypesController customTypesController;

    public CustomTypesResource(CustomTypesController CustomTypesController) {
        this.customTypesController = CustomTypesController;
    }


    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(@Auth SimpleUser user) {
        return Response.ok(customTypesController.getAllFromUser(user)).build();
    }

    @POST
    @Path("/new")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response create(@Auth SimpleUser user,
                           @FormParam("name") String name,
                           @FormParam("key[]") List<String> keys,
                           @FormParam("value[]") List<Integer> values) {
        try {
            return Response.ok(customTypesController.create(user, name, keys, values)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(400).entity(ImmutableMap.of("error", e.getMessage())).build();
        }
    }


    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@Auth SimpleUser user, @PathParam("id") Long id) {
        try {
            UsersCustomType customType = customTypesController.getById(user, id);
            if (customType == null) {
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity(ImmutableMap.of("error", "Custom type with id " + id + " does not exist"))
                        .build();
            }
            return Response.ok(customType).build();
        }
        catch (IllegalAccessException e) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(ImmutableMap.of("error", e.getMessage()))
                    .build();
        }
    }
}
