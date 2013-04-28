package com.hypnoticocelot.jaxrs.doclet.sample.resources;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

@Path("/HttpServletRequest")
public class HttpServletRequestResource {
    @GET
    public String injectServletRequest(@Context HttpServletRequest request) {
        return request.getQueryString();
    }
}
