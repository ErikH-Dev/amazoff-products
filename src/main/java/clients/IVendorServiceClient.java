package clients;

import dto.VendorDTO;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/vendors")
@RegisterRestClient(configKey = "user-service")
public interface IVendorServiceClient {
    
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    Uni<VendorDTO> getVendorById(@PathParam("id") int id);
}