package fixtures.primitives;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/primitives/ints")
public class IntsResource {
    @GET
    public int get() {
        return 0;
    }

    @POST
    public Response create(int value) {
        return Response.ok().build();
    }
}
