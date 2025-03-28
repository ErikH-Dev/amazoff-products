package controllers;

import org.eclipse.microprofile.openapi.annotations.Operation;

import entities.Order;
import enums.OrderStatus;
import interfaces.IOrderService;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MediaType;

@Path("/orders")
public class OrderController {
    private IOrderService orderService;

    public OrderController(IOrderService orderService) {
        this.orderService = orderService;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Add an order", description = "Add an order to the database")
    public Response addOrder(@Valid Order order) {
        Order createdOrder = orderService.create(order);
        return Response.ok(createdOrder).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get an order by ID", description = "Get an order from the database by ID")
    public Response getOrderById(@PathParam("id") int id) {
        Order order = orderService.read(id);
        return Response.ok(order).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get all orders", description = "Retrieve all orders from the database")
    public Response getAllOrders() {
        return Response.ok(orderService.readAll()).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update order status", description = "Update the status of an order in the database")
    public Response updateOrder(@PathParam("id") int id, OrderStatus orderStatus) {
        Order updatedOrder = orderService.updateOrderStatus(id, orderStatus);
        return Response.ok(updatedOrder).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete an order", description = "Delete an order from the database")
    public Response deleteOrder(@PathParam("id") int id) {
        orderService.delete(id);
        return Response.noContent().build();
    }
}
