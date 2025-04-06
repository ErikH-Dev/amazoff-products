package controllers;

import entities.Product;
import interfaces.IProductService;
import io.smallrye.mutiny.Uni;
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
    public Uni<Response> addProduct(@QueryParam("oauthId") int oauthId, @Valid Product product) {
        return productService.create(oauthId, product)
            .onItem().transform(createdProduct -> Response.ok(createdProduct).build());
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> getProductById(@PathParam("id") int id) {
        return productService.read(id)
            .onItem().transform(product -> Response.ok(product).build());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> getAllProducts() {
        return productService.readAll()
            .onItem().transform(products -> Response.ok(products).build());
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> updateProduct(@QueryParam("oauthId") int oauthId, @Valid Product product) {
        return productService.update(oauthId, product)
            .onItem().transform(updatedProduct -> Response.ok(updatedProduct).build());
    }

    @DELETE
    @Path("/{id}")
    public Uni<Response> deleteProduct(@PathParam("id") int id) {
        return productService.delete(id)
            .onItem().transform(v -> Response.noContent().build());
    }
}