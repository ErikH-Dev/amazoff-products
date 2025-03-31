package controllers;

import org.eclipse.microprofile.openapi.annotations.Operation;

import entities.User;
import interfaces.IUserService;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MediaType;

@Path("/users")
public class UserController {
    private IUserService userService;
    
    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Add a user", description = "Add a user to the database")
    public Response addUser(@Valid User user) {
        User createdUser = userService.create(user);
        return Response.ok(createdUser).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get a user by ID", description = "Get a user from the database by ID")
    public Response getUserById(@PathParam("id") int oauthId) {
        User user = userService.read(oauthId);
        return Response.ok(user).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update a user", description = "Update a user in the database")
    public Response updateUser(@Valid User user) {
        User updatedUser = userService.update(user);
        return Response.ok(updatedUser).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete a user", description = "Delete a user from the database")
    public Response deleteUser(@PathParam("id") int id) {
        userService.delete(id);
        return Response.noContent().build();
    }
}
