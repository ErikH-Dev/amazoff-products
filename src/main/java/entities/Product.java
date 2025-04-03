package entities;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotNull
    private Double price;

    @NotBlank
    @Size(max = 500, message = "Description cannot be longer than 500 characters")
    private String description;

    @ManyToOne
    @JoinColumn(name = "oauthId", nullable = false)
    private Vendor vendor;

    public Product() {
    }

    public Product(int id, String name, Double price, String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
    }

    public Product(String name, Double price, String description) {
        this.name = name;
        this.price = price;
        this.description = description;
    }

    public Product(String name, Double price, String description, Vendor vendor) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.vendor = vendor;
    }

    @JsonbCreator
    public Product(@JsonbProperty("id") int id, @JsonbProperty("name") String name,
                   @JsonbProperty("price") Double price, @JsonbProperty("description") String description,
                   @JsonbProperty("vendor") Vendor vendor) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.vendor = vendor;
    }

    public int getId() {
        return id;
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

    public Vendor getVendor() {
        return vendor;
    }

    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
    }
}