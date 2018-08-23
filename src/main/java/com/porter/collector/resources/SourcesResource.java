package com.porter.collector.resources;

import com.google.common.collect.ImmutableMap;
import com.porter.collector.db.SourceDao;
import com.porter.collector.model.SimpleUser;
import com.porter.collector.model.Source;
import com.porter.collector.model.ValueTypes;
import io.dropwizard.auth.Auth;
import org.hibernate.validator.constraints.NotEmpty;

import javax.annotation.PostConstruct;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path(Urls.SOURCE)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SourcesResource {

    public SourcesResource(SourceDao sourceDao) {
        this.sourceDao = sourceDao;
    }

    private final SourceDao sourceDao;

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


}
