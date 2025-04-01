package controllers;

import org.eclipse.microprofile.openapi.annotations.Operation;

import entities.Buyer;
import interfaces.IBuyerService;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MediaType;

@Path("/buyers")
public class BuyerController {
    private IBuyerService buyerService;

    public BuyerController(IBuyerService buyerService) {
        this.buyerService = buyerService;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Add a buyer", description = "Add a buyer to the database")
    public Response addBuyer(@Valid Buyer buyer) {
        Buyer createdBuyer = buyerService.create(buyer);
        return Response.ok(createdBuyer).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get a buyer by oauthId", description = "Retrieve a buyer from the database by oauthId")
    public Response getBuyerById(@PathParam("id") int oauthId) {
        Buyer buyer = buyerService.read(oauthId);
        return Response.ok(buyer).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update a buyer", description = "Update a buyer in the database")
    public Response updateBuyer(@Valid Buyer buyer) {
        Buyer updatedBuyer = buyerService.update(buyer);
        return Response.ok(updatedBuyer).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete a buyer", description = "Delete a buyer from the database")
    public Response deleteBuyer(@PathParam("id") int oauthId) {
        buyerService.delete(oauthId);
        return Response.noContent().build();
    }
    
}
