package com.porter.collector.resources;

import com.porter.collector.db.CollectionDao;
import com.porter.collector.model.Collection;
import com.porter.collector.model.UserWithPassword;
import com.porter.collector.model.UserWithoutPassword;
import io.dropwizard.auth.Auth;
import org.hibernate.validator.constraints.NotEmpty;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.security.Principal;
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
    public Response list(@Auth UserWithoutPassword user) {
        List<Collection> collections = collectionDao.findAllWithUserId(user.id());

        return Response.ok(collections).build();
    }

    @POST
    @Path("/new")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response create(@Auth UserWithoutPassword user, @FormParam("name") @NotEmpty String name) {
        Collection collection = collectionDao.insert(name, user.id());

        return Response.ok(collection).build();
    }
}
