package com.yammer.dropwizard.apidocs.sample.resources;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/greetings/{name}")
public class GreetingsResource {
    @GET
    public String getGreeting(@PathParam("name") @DefaultValue("World") String name) {
        return "Hello, " + name + "!";
    }
}
