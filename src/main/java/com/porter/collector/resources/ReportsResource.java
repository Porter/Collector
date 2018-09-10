package com.porter.collector.resources;


import com.porter.collector.controller.ReportsController;
import com.porter.collector.model.SimpleUser;
import io.dropwizard.auth.Auth;
import org.hibernate.validator.constraints.NotEmpty;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.ParseException;

@Path(Urls.REPORT)
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
public class ReportsResource {

    private final ReportsController reportsController;

    public ReportsResource(ReportsController reportsController) {
        this.reportsController = reportsController;
    }

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(@Auth SimpleUser user) {
        return Response.ok(reportsController.getAll(user)).build();
    }

    @POST
    @Path("/new")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@Auth SimpleUser user,
                           @FormParam("name") @NotEmpty String name,
                           @FormParam("formula") @NotEmpty String formula) {
        try {
            return Response.ok(reportsController.create(user, name, formula)).build();
        } catch (ParseException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
}
