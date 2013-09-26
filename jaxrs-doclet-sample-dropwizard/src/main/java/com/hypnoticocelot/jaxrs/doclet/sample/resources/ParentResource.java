package com.hypnoticocelot.jaxrs.doclet.sample.resources;

import com.sun.jersey.api.core.ResourceContext;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

@Path("parent")
public class ParentResource {
    @Context
    private ResourceContext resourceContext;

    @Path("sub/{subId}")
    public SubResource subResource(@PathParam("subId") String subId) {
        return resourceContext.getResource(SubResource.class);
    }
}
