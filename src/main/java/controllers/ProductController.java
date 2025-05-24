package controllers;

import java.util.List;

import dto.CreateProductRequest;
import dto.UpdateProductRequest;
import interfaces.IProductService;
import io.smallrye.mutiny.Uni;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MediaType;
import org.jboss.logging.Logger;
import org.jboss.logging.MDC;

@Path("/products")
public class ProductController {
    private static final Logger LOG = Logger.getLogger(ProductController.class);
    private IProductService productService;

    public ProductController(IProductService productService) {
        this.productService = productService;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> addProduct(@Valid CreateProductRequest productRequest) {
        LOG.infof("Received addProduct request: name=%s, oauth_id=%d", productRequest.name, productRequest.oauth_id);
        return productService.create(productRequest)
            .onItem().invoke(product -> {
                MDC.put("productId", product.getId());
                LOG.infof("Product created: productId=%d", product.getId());
                MDC.remove("productId");
            })
            .onItem().transform(createdProduct -> Response.ok(createdProduct).build())
            .onFailure().invoke(e -> LOG.errorf("Failed to create product: %s", e.getMessage()));
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> getProductById(@PathParam("id") int id) {
        MDC.put("productId", id);
        LOG.infof("Received getProductById request: productId=%d", id);
        return productService.read(id)
            .onItem().invoke(product -> LOG.infof("Product retrieved: productId=%d", product.getId()))
            .onItem().transform(product -> Response.ok(product).build())
            .onFailure().invoke(e -> LOG.errorf("Failed to get product: %s", e.getMessage()))
            .eventually(() -> {
                MDC.remove("productId");
                return Uni.createFrom().voidItem();
            });
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> getAllProducts() {
        LOG.info("Received getAllProducts request");
        return productService.readAll()
            .onItem().invoke(products -> LOG.infof("Products retrieved: count=%d", products.size()))
            .onItem().transform(products -> Response.ok(products).build())
            .onFailure().invoke(e -> LOG.errorf("Failed to get all products: %s", e.getMessage()));
    }

    @GET
    @Path("/batch")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> getProductsByIds(@QueryParam("ids") List<Integer> ids) {
        LOG.infof("Received getProductsByIds request: ids=%s", ids);
        return productService.readByIds(ids)
            .onItem().invoke(products -> LOG.infof("Products batch retrieved: count=%d", products.size()))
            .onItem().transform(products -> Response.ok(products).build())
            .onFailure().invoke(e -> LOG.errorf("Failed to get products by ids: %s", e.getMessage()));
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> updateProduct(@Valid UpdateProductRequest productRequest) {
        MDC.put("productId", productRequest.id);
        LOG.infof("Received updateProduct request: productId=%d", productRequest.id);
        return productService.update(productRequest)
            .onItem().invoke(updatedProduct -> LOG.infof("Product updated: productId=%d", updatedProduct.getId()))
            .onItem().transform(updatedProduct -> Response.ok(updatedProduct).build())
            .onFailure().invoke(e -> LOG.errorf("Failed to update product: %s", e.getMessage()))
            .eventually(() -> {
                MDC.remove("productId");
                return Uni.createFrom().voidItem();
            });
    }

    @DELETE
    @Path("/{id}")
    public Uni<Response> deleteProduct(@PathParam("id") int id) {
        MDC.put("productId", id);
        LOG.infof("Received deleteProduct request: productId=%d", id);
        return productService.delete(id)
            .onItem().invoke(v -> LOG.infof("Product deleted: productId=%d", id))
            .onItem().transform(v -> Response.noContent().build())
            .onFailure().invoke(e -> LOG.errorf("Failed to delete product: %s", e.getMessage()))
            .eventually(() -> {
                MDC.remove("productId");
                return Uni.createFrom().voidItem();
            });
    }
}