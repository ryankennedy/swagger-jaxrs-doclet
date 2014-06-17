package fixtures.sample;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/foo")
public class Service {
    /**
     * @errorResponse 404 not found
     * @errorResponse ABC invalid code won't be parsed
     */
    @GET
    public String sayHello(@QueryParam("name") @DefaultValue("World") String name) {
        return "Hello, " + name + "!";
    }

    @POST
    public int createSpeech(String speech) {
        return speech.hashCode();
    }

    @Path("/annotated")
    @POST
    public int createSpeechWithAnnotatedPayload(@Deprecated String speech) {
        return speech.hashCode();
    }

    @Path("{fooId}/sub")
    public SubResource getSubResource(@PathParam("fooId") String fooId) {
        return new SubResource();
    }
    
    @Path("/jaxrsresponse")
    @GET
    public Response getSubResourceWrappedInResponse(@QueryParam("pameter1") @DefaultValue("pameter1") String pameter1, @QueryParam("pameter2") String pameter2) {
       return Response.ok(new SubResource()).build();
    }
}
