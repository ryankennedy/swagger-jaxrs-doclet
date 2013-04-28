package com.hypnoticocelot.jaxrs.doclet.sample.resources;

import com.hypnoticocelot.jaxrs.doclet.sample.api.Recursive;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/Recursive")
public class RecursiveResource {
    @POST
    public Recursive recurse(Recursive recursive) {
        return recursive;
    }
}
