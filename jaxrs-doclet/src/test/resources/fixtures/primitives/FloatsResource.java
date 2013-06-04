package fixtures.primitives;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/primitives/floats")
public class FloatsResource {
    @GET
    public float get() {
        return 0;
    }

    @POST
    public Response create(float value) {
        return Response.ok().build();
    }
}
