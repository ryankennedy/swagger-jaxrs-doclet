package fixtures.primitives;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/primitives/bytes")
public class BytesResource {
    @GET
    public byte get() {
        return 0;
    }

    @POST
    public Response create(byte value) {
        return Response.ok().build();
    }
}
