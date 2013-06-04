package fixtures.jaxb;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/jaxb")
public class JaxbResource {
    @GET
    public ResponseModel get() {
        return new ResponseModel();
    }

    @POST
    public javax.xml.ws.Response create(PayloadModel payload) {
        return new ResponseModel();
    }
}
