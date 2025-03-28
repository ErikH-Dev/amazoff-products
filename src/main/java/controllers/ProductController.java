package controllers;

import org.eclipse.microprofile.openapi.annotations.Operation;

import entities.Product;
import interfaces.IProductService;
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
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get a product by ID", description = "Get a product from the database by ID")
    public Response getProductById(@PathParam("id") int id) {
        Product product = productService.read(id);
        return Response.ok(product).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get all products", description = "Retrieve all products from the database")
    public Response getAllProducts() {
        return Response.ok(productService.readAll()).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update a product", description = "Update a product in the database")
    public Response updateProduct(@Valid Product product) {
        Product updatedProduct = productService.update(product);
        return Response.ok(updatedProduct).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete a product", description = "Delete a product from the database")
    public Response deleteProduct(@PathParam("id") int id) {
        productService.delete(id);
        return Response.noContent().build();
    }
}