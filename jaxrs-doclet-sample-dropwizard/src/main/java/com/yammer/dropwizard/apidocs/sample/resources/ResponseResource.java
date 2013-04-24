package com.yammer.dropwizard.apidocs.sample.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/Response")
public class ResponseResource {
    @GET
    public Response respond() {
        return Response.ok("Hello, World!").build();
    }
}
