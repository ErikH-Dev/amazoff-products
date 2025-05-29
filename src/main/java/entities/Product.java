package entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntity;
import jakarta.validation.constraints.*;
import org.bson.codecs.pojo.annotations.BsonProperty;

@MongoEntity(collection = "products")
public class Product extends ReactivePanacheMongoEntity {
    
    @NotNull(message = "oauthId must not be null")
    @BsonProperty("oauth_id")
    @JsonProperty("oauth_id")
    public int oauthId;

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

    public Product() {}

    public Product(String name, int oauthId, Double price, String description, int stock) {
        this.name = name;
        this.oauthId = oauthId;
        this.price = price;
        this.description = description;
        this.stock = stock;
    }

    // Getters for backward compatibility
    @JsonProperty("product_id")
    public String getProductId() { 
        return this.id != null ? this.id.toString() : null; 
    }
    
    public int getOauthId() { return oauthId; }
    public String getName() { return name; }
    public Double getPrice() { return price; }
    public String getDescription() { return description; }
    public int getStock() { return stock; }
}