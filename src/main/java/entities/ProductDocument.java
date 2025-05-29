package entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProductDocument {
    @JsonProperty("product_id")
    public String productId;

    @JsonProperty("oauth_id")
    public int oauthId;

    @JsonProperty("name")
    public String name;

    @JsonProperty("price")
    public Double price;

    @JsonProperty("description")
    public String description;

    @JsonProperty("stock")
    public int stock;

    public ProductDocument() {}

    public ProductDocument(Product product) {
        this.productId = product.getProductId();
        this.oauthId = product.oauthId;
        this.name = product.name;
        this.price = product.price;
        this.description = product.description;
        this.stock = product.stock;
    }

    public Product toProduct() {
        Product product = new Product();
        product.oauthId = this.oauthId;
        product.name = this.name;
        product.price = this.price;
        product.description = this.description;
        product.stock = this.stock;
        return product;
    }
}