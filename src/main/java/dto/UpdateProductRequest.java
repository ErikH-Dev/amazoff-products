package dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class UpdateProductRequest {
    @NotNull(message = "Product ID must not be null")
    @JsonProperty("product_id")
    public String id;

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

    @Positive(message = "Stock must be a positive number")
    @JsonProperty("stock")
    public int stock;
}