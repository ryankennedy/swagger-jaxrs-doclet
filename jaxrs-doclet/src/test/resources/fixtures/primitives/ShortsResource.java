package fixtures.primitives;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/primitives/shorts")
public class ShortsResource {
    @GET
    public short get() {
        return 0;
    }

    @POST
    public Response create(short value) {
        return Response.ok().build();
    }
}
