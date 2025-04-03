package controllers;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.Operation;

import dto.OrderRequest;
import entities.Order;
import enums.OrderStatus;
import interfaces.IOrderService;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import utils.OrderStatusUtils;
import jakarta.ws.rs.core.MediaType;

@Path("/orders")
public class OrderController {
    private final IOrderService orderService;

    public OrderController(IOrderService orderService) {
        this.orderService = orderService;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Create an order", description = "Create a new order for a buyer")
    public Response createOrder(@Valid OrderRequest orderRequest) {
        Order createdOrder = orderService.create(orderRequest);
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
    @Path("/user/{oauthId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get all orders by user ID", description = "Retrieve all buyer orders from the database")
    public Response getAllOrdersByUser(@PathParam("oauthId") int oauthId) {
        List<Order> orders = orderService.readAllByUser(oauthId);
        return Response.ok(orders).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update order status", description = "Update the status of an order in the database")
    public Response updateOrderStatus(@PathParam("id") int id, String orderStatus) {
        OrderStatus status = OrderStatusUtils.fromString(orderStatus);
        Order updatedOrder = orderService.updateOrderStatus(id, status);
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