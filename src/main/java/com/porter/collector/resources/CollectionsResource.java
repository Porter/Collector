package com.porter.collector.resources;

import com.google.common.collect.ImmutableMap;
import com.porter.collector.db.CollectionDao;
import com.porter.collector.db.SourceDao;
import com.porter.collector.errors.CollectionExistsException;
import com.porter.collector.model.Collection;
import com.porter.collector.model.SimpleUser;
import com.porter.collector.model.Source;
import com.porter.collector.model.ValueTypes;
import io.dropwizard.auth.Auth;
import org.hibernate.validator.constraints.NotEmpty;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path(Urls.COLLECTION)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
public class CollectionsResource {

    private final CollectionDao collectionDao;
    private final SourceDao sourceDao;

    public CollectionsResource(CollectionDao collectionDao, SourceDao sourceDao) {
        this.collectionDao = collectionDao;
        this.sourceDao = sourceDao;
    }


    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response list(@Auth SimpleUser user) {
        List<Collection> collections = collectionDao.findAllWithUserId(user.id());

        return Response.ok(collections).build();
    }

    @POST
    @Path("/new")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response create(@Auth SimpleUser user, @FormParam("name") @NotEmpty String name) {
        Collection collection;
        try { collection = collectionDao.insert(name, user.id()); }
        catch (CollectionExistsException e) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(ImmutableMap.of("error", "Collection exists"))
                    .build();
        }

        return Response.ok(collection).build();
    }

    @POST
    @Path("/{id}" + Urls.SOURCE + "/new")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response create(@Auth SimpleUser user,
                           @PathParam("id") Long collectionId,
                           @FormParam("name") @NotEmpty String name,
                           @FormParam("type") int type) {
        Source source;
        try { source = sourceDao.insert(name, user.id(), collectionId, ValueTypes.values()[type]); }
        catch (Exception e) { throw e; }

        return Response.ok(source).build();
    }
}
