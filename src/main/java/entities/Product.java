package entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntity;
import jakarta.validation.constraints.*;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

@MongoEntity(collection = "products")
public class Product extends ReactivePanacheMongoEntity {

    @NotNull(message = "keycloakId must not be null")
    @BsonProperty("keycloak_id")
    @JsonProperty("keycloak_id")
    public String keycloakId;

    @NotBlank(message = "Name must not be blank")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    @JsonProperty("name")
    public String name;

    @NotNull(message = "Price must not be null")
    @Positive(message = "Price must be a positive number")
    @JsonProperty("price")
    public Double price;

    @NotBlank(message = "Description must not be blank")
    @Size(max = 500, message = "Description cannot be longer than 500 characters")
    @JsonProperty("description")
    public String description;

    @PositiveOrZero(message = "Stock must be a positive number")
    @JsonProperty("stock")
    public int stock;

    public Product() {
    }

    public Product(String name, String keycloakId, Double price, String description, int stock) {
        this.name = name;
        this.keycloakId = keycloakId;
        this.price = price;
        this.description = description;
        this.stock = stock;
    }

    public Product(ObjectId id, String name, String keycloakId, Double price, String description, int stock) {
        this.id = id;
        this.name = name;
        this.keycloakId = keycloakId;
        this.price = price;
        this.description = description;
        this.stock = stock;
    }

    @JsonProperty("product_id")
    public String getProductId() {
        return this.id != null ? this.id.toString() : null;
    }

    public String getKeycloakId() {
        return keycloakId;
    }

    public String getName() {
        return name;
    }

    public Double getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public int getStock() {
        return stock;
    }
}