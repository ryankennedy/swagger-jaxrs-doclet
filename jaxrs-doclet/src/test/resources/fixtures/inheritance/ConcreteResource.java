package fixtures.inheritance;

import javax.ws.rs.GET;
import javax.ws.rs.Path;


@Path("/foo")
public class ConcreteResource extends AbstractResource {

    @GET
    @Path("bar")
    public String bar() {
        return "bar";
    }

    @Override
    protected String getResourceById(String id) {
        return "Concrete Resource with id " + id;
    }
}