package controllers;

import entities.Vendor;
import interfaces.IVendorService;
import io.smallrye.mutiny.Uni;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MediaType;

@Path("/vendors")
public class VendorController {
    private IVendorService vendorService;

    public VendorController(IVendorService vendorService) {
        this.vendorService = vendorService;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> addVendor(@Valid Vendor vendor) {
        return vendorService.create(vendor)
            .onItem().transform(createdVendor -> Response.ok(createdVendor).build());
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> getVendorById(@PathParam("id") int oauthId) {
        return vendorService.read(oauthId)
            .onItem().transform(vendor -> Response.ok(vendor).build());
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> updateVendor(@Valid Vendor vendor) {
        return vendorService.update(vendor)
            .onItem().transform(updatedVendor -> Response.ok(updatedVendor).build());
    }

    @DELETE
    @Path("/{id}")
    public Uni<Response> deleteVendor(@PathParam("id") int oauthId) {
        return vendorService.delete(oauthId)
            .onItem().transform(v -> Response.noContent().build());
    }
}