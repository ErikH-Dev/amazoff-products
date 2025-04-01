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
    public Response addAddress(@Valid Address address) {
        Address createdAddress = addressService.create(address);
        return Response.ok(createdAddress).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get all addresses", description = "Retrieve all addresses from the database")
    public Response getAllAddressesByUser(@PathParam("id") int oauthId) {
        return Response.ok(addressService.readAllByUser(oauthId)).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update an address", description = "Update an address in the database")
    public Response updateAddress(@Valid Address address) {
        Address updatedAddress = addressService.update(address);
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
