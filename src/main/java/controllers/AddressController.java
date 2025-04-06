package controllers;

import entities.Address;
import interfaces.IAddressService;
import io.smallrye.mutiny.Uni;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MediaType;

@Path("/addresses")
public class AddressController {
    private IAddressService addressService;

    public AddressController(IAddressService addressService) {
        this.addressService = addressService;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> addAddress(@QueryParam("oauthId") int oauthId, @Valid Address address) {
        return addressService.create(oauthId, address)
            .onItem().transform(createdAddress -> Response.ok(createdAddress).build());
    }

    @GET
    @Path("/{oauthId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> getAllAddressesByUser(@PathParam("oauthId") int oauthId) {
        return addressService.readAllByUser(oauthId)
            .onItem().transform(addresses -> Response.ok(addresses).build());
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> updateAddress(@QueryParam("oauthId") int oauthId, @Valid Address address) {
        return addressService.update(oauthId, address)
            .onItem().transform(updatedAddress -> Response.ok(updatedAddress).build());
    }

    @DELETE
    @Path("/{id}")
    public Uni<Response> deleteAddress(@PathParam("id") int id) {
        return addressService.delete(id)
            .onItem().transform(v -> Response.noContent().build());
    }
}