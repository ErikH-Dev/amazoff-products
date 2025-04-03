package controllers;

import org.eclipse.microprofile.openapi.annotations.Operation;

import entities.Address;
import interfaces.IAddressService;
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
    @Operation(summary = "Add an address", description = "Add an address to the database")
    public Response addAddress(@QueryParam("oauthId") int oauthId, @Valid Address address) {
        Address createdAddress = addressService.create(oauthId, address);
        return Response.ok(createdAddress).build();
    }

    @GET
    @Path("/{oauthId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get all addresses", description = "Retrieve all addresses for a user")
    public Response getAllAddressesByUser(@PathParam("oauthId") int oauthId) {
        return Response.ok(addressService.readAllByUser(oauthId)).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update an address", description = "Update an address in the database")
    public Response updateAddress(@QueryParam("oauthId") int oauthId, @Valid Address address) {
        Address updatedAddress = addressService.update(oauthId, address);
        return Response.ok(updatedAddress).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete an address", description = "Delete an address from the database")
    public Response deleteAddress(@PathParam("id") int id) {
        addressService.delete(id);
        return Response.noContent().build();
    }
    
}
