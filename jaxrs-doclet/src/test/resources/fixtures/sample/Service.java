package fixtures.sample;

import javax.ws.rs.*;

@Path("/foo")
public class Service {
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
}
