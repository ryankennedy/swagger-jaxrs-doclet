package fixtures.sample;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

@Path("/foo")
public class Service {
    @GET
    public String sayHello(@QueryParam("name") @DefaultValue("World") String name) {
        return "Hello, " + name + "!";
    }
}