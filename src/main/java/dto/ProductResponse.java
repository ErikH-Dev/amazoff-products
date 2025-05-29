package dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import entities.Product;

public class ProductResponse {
    @JsonProperty("product_id")
    public String productId; // Changed from int to String

    @JsonProperty("oauth_id")
    public int oauthId;
    
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
        this.productId = product.getProductId(); // Now returns String
        this.oauthId = product.oauthId;
        this.vendor = vendor;
        this.name = product.name;
        this.price = product.price;
        this.description = product.description;
        this.stock = product.stock;
    }

    // Static factory method for easy creation
    public static ProductResponse from(Product product, VendorDTO vendor) {
        return new ProductResponse(product, vendor);
    }
}