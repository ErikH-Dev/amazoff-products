package dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import entities.Product;

public class ProductResponse {
    @JsonProperty("product_id")
    public String productId;

    @JsonProperty("keycloak_id")
    public String keycloakId;

    @JsonProperty("vendor")
    public VendorDTO vendor;

    @JsonProperty("name")
    public String name;

    @JsonProperty("price")
    public Double price;

    @JsonProperty("description")
    public String description;

    @JsonProperty("stock")
    public int stock;

    public ProductResponse() {}

    public ProductResponse(Product product, VendorDTO vendor) {
        this.productId = product.getProductId();
        this.keycloakId = product.keycloakId;
        this.vendor = vendor;
        this.name = product.name;
        this.price = product.price;
        this.description = product.description;
        this.stock = product.stock;
    }
}