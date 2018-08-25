package com.porter.collector.resources;

import com.google.common.collect.ImmutableMap;
import com.porter.collector.db.DeltaDao;
import com.porter.collector.db.SourceDao;
import com.porter.collector.model.*;
import io.dropwizard.auth.Auth;
import org.hibernate.validator.constraints.NotEmpty;

import javax.annotation.PostConstruct;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path(Urls.SOURCE)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SourcesResource {

    public SourcesResource(SourceDao sourceDao, DeltaDao deltaDao) {
        this.sourceDao = sourceDao;
        this.deltaDao = deltaDao;
    }

    private final SourceDao sourceDao;
    private final DeltaDao deltaDao;

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(@Auth SimpleUser user) {
        return Response.ok(sourceDao.findAllFromUser(user.id())).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@Auth SimpleUser user, @PathParam("id") Long id) {
        Source source = sourceDao.findById(id);
        if (source == null) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(ImmutableMap.of("error", "Source with id " + id + " does not exist"))
                    .build();
        }
        return Response.ok(sourceDao.findById(id)).build();
    }

    @GET
    @Path("/{id}/values/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getValuesById(@Auth SimpleUser user, @PathParam("id") Long id) {
        Source source = sourceDao.findById(id);
        if (source == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        if (source.userId() != user.id()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        List<Delta> deltas = deltaDao.findBySourceId(id);
        return Response.ok(deltas).build();
    }

    @POST
    @Path("/{id}/values/new")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response addValue(
            @Auth SimpleUser user,
            @PathParam("id") Long sourceId,
            @FormParam("collectionId") Long collectionId,
            @FormParam("name") String name,
            @FormParam("amount") Long amount
    ) {
        Delta delta = deltaDao.insert(name, amount, collectionId, sourceId);
        return Response.ok(delta).build();
    }


}
