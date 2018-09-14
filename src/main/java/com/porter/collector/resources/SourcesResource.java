package com.porter.collector.resources;

import com.google.common.collect.ImmutableMap;
import com.porter.collector.controller.SourcesController;
import com.porter.collector.model.*;
import io.dropwizard.auth.Auth;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
            MultivaluedMap<String, String> params) {
        return Response
                .ok(sourcesController.addValue(user, sourceId, params))
                .build();
    }

    @POST
    @Path("/{id}/values/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadValues(@Auth SimpleUser user,
                                 @FormDataParam("upload") InputStream uploadedInputStream,
                                 @FormDataParam("file") FormDataContentDisposition fileDetail) {

        Reader reader = new InputStreamReader(uploadedInputStream);
        try {
            CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT);
            Iterator<CSVRecord> itr = parser.iterator();
            if (itr.hasNext()) {
                CSVRecord record = itr.next();
                List<String> header = new ArrayList<>();
                record.iterator().forEachRemaining(header::add);

                return Response.ok(header).build();
            }

            return null;
        } catch (IOException e) {
            return Response
                    .status(500)
                    .entity(ImmutableMap.of("error", "Upload interrupted. Please try again"))
                    .build();
        }
    }
}
