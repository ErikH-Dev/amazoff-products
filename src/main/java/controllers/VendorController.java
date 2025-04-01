package controllers;

import org.eclipse.microprofile.openapi.annotations.Operation;

import entities.Vendor;
import interfaces.IVendorService;
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
    @Operation(summary = "Add a vendor", description = "Add a vendor to the database")
    public Response addVendor(@Valid Vendor vendor) {
        Vendor createdVendor = vendorService.create(vendor);
        return Response.ok(createdVendor).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get a vendor by oauthId", description = "Retrieve a vendor from the database by oauthId")
    public Response getVendorById(@PathParam("id") int oauthId) {
        Vendor vendor = vendorService.read(oauthId);
        return Response.ok(vendor).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update a vendor", description = "Update a vendor in the database")
    public Response updateVendor(@Valid Vendor vendor) {
        Vendor updatedVendor = vendorService.update(vendor);
        return Response.ok(updatedVendor).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete a vendor", description = "Delete a vendor from the database")
    public Response deleteVendor(@PathParam("id") int oauthId) {
        vendorService.delete(oauthId);
        return Response.noContent().build();
    }
}
