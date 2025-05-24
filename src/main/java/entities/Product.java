package entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import dto.VendorDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "App_Product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull(message = "Product ID must not be null")
    @JsonProperty("product_id")
    private int id;

    @NotNull(message = "oauthId must not be null")
    @JsonProperty("oauth_id")
    private int oauthId;
    
    @Transient
    @JsonProperty("vendor")
    private VendorDTO vendorDTO;

    @NotBlank(message = "Name must not be blank")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    @JsonProperty("name")
    private String name;

    @NotNull(message = "Price must not be null")
    @Positive(message = "Price must be a positive number")
    @JsonProperty("price")
    private Double price;

    @NotBlank(message = "Description must not be blank")
    @Size(max = 500, message = "Description cannot be longer than 500 characters")
    @JsonProperty("description")
    private String description;

    @PositiveOrZero(message = "Stock must be a positive number")
    @JsonProperty("stock")
    private int stock;

    public Product() {
    }

    public Product(int id, String name, Double price, String description, int stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.stock = stock;
    }

    public Product(String name, int oauthId, Double price, String description, int stock) {
        this.name = name;
        this.oauthId = oauthId;
        this.price = price;
        this.description = description;
        this.stock = stock;
    }
    public Product(int id, String name, int oauthId, Double price, String description, int stock) {
        this.id = id;
        this.name = name;
        this.oauthId = oauthId;
        this.price = price;
        this.description = description;
        this.stock = stock;
    }

    public int getId() {
        return id;
    }

    public int getOauthId() {
        return oauthId;
    }

    public VendorDTO getVendorDTO() {
        return vendorDTO;
    }
    
    public void setVendorDTO(VendorDTO vendorDTO) {
        this.vendorDTO = vendorDTO;
        if (vendorDTO != null) {
            this.oauthId = vendorDTO.getOauthId();
        }
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