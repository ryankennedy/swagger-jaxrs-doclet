package com.hypnoticocelot.jaxrs.doclet.sample.resources;

import com.yammer.dropwizard.auth.Auth;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/Auth")
public class AuthResource {
    /**
     * @status 404 not found
     */
    @GET
    public String authorize(@Auth String user) {
        return "USER = " + user;
    }
}
