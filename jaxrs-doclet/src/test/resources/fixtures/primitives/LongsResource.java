package fixtures.primitives;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/primitives/longs")
public class LongsResource {
    @GET
    public long get() {
        return 0;
    }

    @POST
    public Response create(long value) {
        return Response.ok().build();
    }
}
