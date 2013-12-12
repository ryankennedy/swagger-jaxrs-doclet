package com.hypnoticocelot.jaxrs.doclet.sample.resources;

import com.sun.jersey.api.core.ResourceContext;

import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

public class SubResource {
    @Context
    private ResourceContext resourceContext;

    @Path("greetings")
    public GreetingsResource subResource() {
        return resourceContext.getResource(GreetingsResource.class);
    }

    @Path("sub")
    public SubResource recursiveSubResource() {
        return resourceContext.getResource(SubResource.class);
    }
}
