package entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProductDocument {
    @JsonProperty("product_id")
    public String productId;

    @JsonProperty("keycloak_id")
    public String keycloakId;

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
        this.keycloakId = product.keycloakId;
        this.name = product.name;
        this.price = product.price;
        this.description = product.description;
        this.stock = product.stock;
    }

    public Product toProduct() {
        Product product = new Product();
        product.keycloakId = this.keycloakId;
        product.name = this.name;
        product.price = this.price;
        product.description = this.description;
        product.stock = this.stock;
        return product;
    }
}