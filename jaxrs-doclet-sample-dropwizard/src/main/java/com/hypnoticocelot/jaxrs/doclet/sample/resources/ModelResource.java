package com.hypnoticocelot.jaxrs.doclet.sample.resources;

import com.hypnoticocelot.jaxrs.doclet.sample.api.ModelResourceModel;
import org.joda.time.DateTime;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/ModelResource/{modelid}")
@Produces(MediaType.APPLICATION_JSON)
public class ModelResource {
    @GET
    public ModelResourceModel getModel(@PathParam("modelid") long modelId) {
        return new ModelResourceModel(modelId, "Model Title", "Model Description", new DateTime());
    }
}
