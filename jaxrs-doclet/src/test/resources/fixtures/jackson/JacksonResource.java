package fixtures.jackson;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/jackson")
public class JacksonResource {
    @GET
    public ResponseModel get() {
        return new ResponseModel();
    }

    @POST
    public Response create(PayloadModel payload) {
        return Response.ok().build();
    }
}
