package exceptions.errors;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class ProductNotFoundException extends WebApplicationException {
    private String productId;

    public ProductNotFoundException(String productId) {
        super("Product not found with ID: " + productId, Response.status(Response.Status.NOT_FOUND).entity("Product not found with ID: " + productId).build());
        this.productId = productId;
    }

    public String getProductId() {
        return productId;
    }
}