package controllers;

import java.util.List;

import dto.CreateProductRequest;
import dto.UpdateProductRequest;
import entities.Product;
import interfaces.IProductService;
import utils.JwtUtil;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MediaType;

import org.bson.types.ObjectId;
import org.jboss.logging.Logger;
import org.jboss.logging.MDC;

@Path("/products")
public class ProductController {
    private static final Logger LOG = Logger.getLogger(ProductController.class);
    private IProductService productService;

    @Inject
    JwtUtil jwtUtil;

    public ProductController(IProductService productService) {
        this.productService = productService;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("vendor")
    public Uni<Response> addProduct(@Valid CreateProductRequest productRequest) {
        String keycloakId = jwtUtil.getCurrentKeycloakUserId();
        LOG.infof("Received addProduct request: name=%s, keycloakId=%s", productRequest.name, keycloakId);
        Product product = new Product(

                productRequest.name,
                keycloakId,
                productRequest.price,
                productRequest.description,
                productRequest.stock);

        return productService.create(product)
                .onItem().invoke(createdProduct -> {
                    MDC.put("productId", createdProduct.productId);
                    LOG.infof("Product created: productId=%s", createdProduct.productId);
                    MDC.remove("productId");
                })
                .onItem().transform(createdProduct -> Response.ok(createdProduct).build())
                .onFailure().invoke(e -> LOG.errorf("Failed to create product: %s", e.getMessage()));
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> getProductById(@PathParam("id") String id) {
        MDC.put("productId", id);
        LOG.infof("Received getProductById request: productId=%s", id);
        return productService.read(id)
                .onItem().invoke(product -> LOG.infof("Product retrieved: productId=%s", product.productId))
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
    public Uni<Response> getProductsByIds(@QueryParam("ids") List<String> ids) {
        LOG.infof("Received getProductsByIds request: ids=%s", ids);
        return productService.readByIds(ids)
                .onItem().invoke(products -> LOG.infof("Products batch retrieved: count=%d", products.size()))
                .onItem().transform(products -> Response.ok(products).build())
                .onFailure().invoke(e -> LOG.errorf("Failed to get products by ids: %s", e.getMessage()));
    }

    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> searchProducts(@QueryParam("q") String query) {
        if (query == null || query.trim().isEmpty()) {
            return Uni.createFrom().item(
                    Response.status(Response.Status.BAD_REQUEST)
                            .entity("{\"error\": \"Query parameter 'q' is required\"}")
                            .build());
        }

        LOG.infof("Received search request: query=%s", query);
        return productService.searchProducts(query)
                .onItem().invoke(products -> LOG.infof("Search results: count=%d", products.size()))
                .onItem().transform(products -> Response.ok(products).build())
                .onFailure().invoke(e -> LOG.errorf("Failed to search products: %s", e.getMessage()));
    }

    @GET
    @Path("/vendor")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("vendor")
    public Uni<Response> getVendorProducts() {
        String keycloakId = jwtUtil.getCurrentKeycloakUserId();
        LOG.infof("Received getVendorProducts request: keycloakId=%s", keycloakId);
        return productService.readByVendorId(keycloakId)
                .onItem().invoke(products -> LOG.infof("Vendor products retrieved: count=%d", products.size()))
                .onItem().transform(products -> Response.ok(products).build())
                .onFailure().invoke(e -> LOG.errorf("Failed to get vendor products: %s", e.getMessage()));
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("vendor")
    public Uni<Response> updateProduct(@Valid UpdateProductRequest productRequest) {
        String keycloakId = jwtUtil.getCurrentKeycloakUserId();
        ObjectId productId = new ObjectId(productRequest.id);
        Product product = new Product(
                productId,
                productRequest.name,
                keycloakId,
                productRequest.price,
                productRequest.description,
                productRequest.stock);
        MDC.put("productId", productRequest.id);
        LOG.infof("Received updateProduct request: productId=%s, keycloakId=%s", productRequest.id, keycloakId);
        return productService.update(product)
                .onItem().invoke(updatedProduct -> LOG.infof("Product updated: productId=%s", updatedProduct.productId))
                .onItem().transform(updatedProduct -> Response.ok(updatedProduct).build())
                .onFailure().invoke(e -> LOG.errorf("Failed to update product: %s", e.getMessage()))
                .eventually(() -> {
                    MDC.remove("productId");
                    return Uni.createFrom().voidItem();
                });
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("vendor")
    public Uni<Response> deleteProduct(@PathParam("id") String id) {
        String keycloakId = jwtUtil.getCurrentKeycloakUserId();
        MDC.put("productId", id);
        LOG.infof("Received deleteProduct request: productId=%s, keycloakId=%s", id, keycloakId);
        return productService.read(id)
                .onItem().ifNull().failWith(new NotFoundException("Product not found"))
                .onItem().invoke(product -> {
                    if (!product.keycloakId.equals(keycloakId)) {
                        throw new ForbiddenException("You do not have permission to delete this product");
                    }
                })
                .onItem().transformToUni(product -> productService.delete(id))
                .onItem().invoke(() -> LOG.infof("Product deleted: productId=%s", id))
                .onItem().transform(deleted -> Response.noContent().build())
                .onFailure().invoke(e -> LOG.errorf("Failed to delete product: %s", e.getMessage()))
                .eventually(() -> {
                    MDC.remove("productId");
                    return Uni.createFrom().voidItem();
                });
    }
}