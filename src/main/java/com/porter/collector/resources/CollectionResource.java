package com.porter.collector.resources;

import com.porter.collector.db.CollectionDao;
import com.porter.collector.model.Collection;
import com.porter.collector.model.User;
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
public class CollectionResource {

    private CollectionDao collectionDao;

    public CollectionResource(CollectionDao collectionDao) {
        this.collectionDao = collectionDao;
    }


    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response list(@Auth User user) {
        List<Collection> collections = collectionDao.findAllWithUserId(user.id());

        return Response.ok(collections).build();
    }

    @POST
    @Path("/new")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response create(@Auth User user, @FormParam("name") @NotEmpty String name) {
        Collection collection = collectionDao.insert(name, user.id());

        return Response.ok(collection).build();
    }
}
