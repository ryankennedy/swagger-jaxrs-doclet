package com.yammer.dropwizard.apidocs.sample.resources;

import com.yammer.dropwizard.apidocs.sample.api.Recursive;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/Recursive")
public class RecursiveResource {
    @POST
    public Recursive recurse(Recursive recursive) {
        return recursive;
    }
}
