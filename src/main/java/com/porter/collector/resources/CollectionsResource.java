package com.porter.collector.resources;

import com.google.common.collect.ImmutableMap;
import com.porter.collector.controller.CollectionsController;
import com.porter.collector.db.CollectionDao;
import com.porter.collector.db.SourceDao;
import com.porter.collector.errors.CollectionExistsException;
import com.porter.collector.model.Collection;
import com.porter.collector.model.SimpleUser;
import com.porter.collector.model.Source;
import com.porter.collector.model.ValueTypes;
import io.dropwizard.auth.Auth;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path(Urls.COLLECTION)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
public class CollectionsResource {

    private final CollectionsController collectionsController;

    public CollectionsResource(CollectionsController collectionsController) {
        this.collectionsController = collectionsController;
    }


    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response list(@Auth SimpleUser user) {
        return Response
                .ok(collectionsController.allFromUser(user))
                .build();
    }

    @POST
    @Path("/new")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response create(@Auth SimpleUser user, @FormParam("name") @NotEmpty String name) {
        try {
            return Response
                    .ok(collectionsController.create(name, user))
                    .build();
        }
        catch (CollectionExistsException e) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(ImmutableMap.of("error", "Collection exists"))
                    .build();
        }

    }

    @POST
    @Path("/{id}" + Urls.SOURCE + "/new")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response create(@Auth SimpleUser user,
                           @PathParam("id") @NotNull Long collectionId,
                           @FormParam("name") @NotEmpty String name,
                           @FormParam("type") @NotNull Integer type,
                           @FormParam("customType") @NotNull Long customType,
                           @FormParam("external") boolean external) {
        try {
            return Response
                    .ok(collectionsController.addSource(name, user, collectionId, type, customType, external))
                    .build();
        }
        catch (IllegalAccessException e) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(ImmutableMap.of("error", e.getMessage()))
                    .build();
        }

    }
}
