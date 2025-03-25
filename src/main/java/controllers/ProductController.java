package controllers;

import org.eclipse.microprofile.openapi.annotations.Operation;

import entities.Product;
import interfaces.IProductService;
import io.smallrye.common.constraint.NotNull;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MediaType;

@Path("/products")
public class ProductController {
    private IProductService productService;

    public ProductController(IProductService productService) {
        this.productService = productService;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Add a product", description = "Add a product to the database")
    public Response addProduct(@Valid Product product) {
        Product createdProduct = productService.create(product);
        return Response.ok(createdProduct).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get a product by ID", description = "Get a product from the database by ID")
    public Response getProductById(@NotNull @QueryParam("id") int id) {
        Product product = productService.read(id);
        return Response.ok(product).build();
    }
}