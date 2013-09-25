package fixtures.sample;

import javax.ws.rs.*;

public class SubResource {
    @GET
    @Path("subAnnotated")
    public String sayHello(@QueryParam("name") @DefaultValue("World") String name) {
        return "Hello, " + name + "!";
    }

    @POST
    public int createSub() {
        return 0;
    }
}
