package fixtures.primitives;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.Date;

@Path("/primitives/dates")
public class DatesResource {
    @GET
    public Date get() {
        return new Date();
    }

    @POST
    public Response create(Date value) {
        return Response.ok().build();
    }
}
