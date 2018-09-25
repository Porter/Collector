package com.porter.collector.resources;

import com.google.common.collect.ImmutableMap;
import com.porter.collector.controller.SourcesController;
import com.porter.collector.exception.InconsistentDataException;
import com.porter.collector.exception.UnableToProcessCsvException;
import com.porter.collector.exception.CsvMappingNotSetException;
import com.porter.collector.model.*;
import io.dropwizard.auth.Auth;
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
        try {
            return Response
                    .ok(sourcesController.addValue(user, sourceId, params))
                    .build();
        } catch (IllegalAccessException e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    @POST
    @Path("/{id}/values/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadValues(@Auth SimpleUser user,
                                 @PathParam("id") long sourceId,
                                 @FormDataParam("upload") InputStream uploadedInputStream,
                                 @FormDataParam("file") FormDataContentDisposition fileDetail) {

        Reader reader = new InputStreamReader(uploadedInputStream);
        try {
            List<Value> values = sourcesController.uploadCSV(user, sourceId, uploadedInputStream);
            return Response.ok(values).build();
        } catch (IOException e) {
            return Response
                    .status(500)
                    .entity(ImmutableMap.of("error", "Upload interrupted. Please try again"))
                    .build();
        } catch (IllegalAccessException e) {
            return Response
                    .status(401)
                    .entity(ImmutableMap.of("error", "Upload interrupted. Please try again"))
                    .build();
        } catch (CsvMappingNotSetException e) {
            return setMapping(e.getHeaders());
        } catch (InconsistentDataException e) {
            return Response.status(500).entity(
                    ImmutableMap.of("error", "Csv is inconsistent")
            ).build();
        } catch (UnableToProcessCsvException e) {
            e.printStackTrace();
            return Response.status(500).entity(
                    ImmutableMap.of("error", "Unable to process CSV")
            ).build();
        }
    }

    private Response setMapping(List<String> headers) {
        return Response.status(200).header("status", "needHeaders").entity(headers).build();
    }

    @POST
    @Path("/{id}/mapping/set")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response setMapping(@Auth SimpleUser user,
                               @PathParam("id") long sourceId,
                               MultivaluedMap<String, String> map) {

        try {
            List<String> values = sourcesController.setMapping(user, sourceId, map);
            return Response.ok(values).build();
        } catch (IllegalAccessException e) {
            return Response
                    .status(401)
                    .entity(ImmutableMap.of("error", "Upload interrupted. Please try again"))
                    .build();
        }
    }
}
