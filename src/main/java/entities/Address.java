package entities;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "addresses")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    
    private int id;

    @NotNull(message = "User ID must not be null")
    @Column(name = "user_id")
    private int userId;

    @NotBlank(message = "Street must not be blank")
    @Size(max = 255, message = "Street must not exceed 255 characters")
    private String street;

    @NotBlank(message = "City must not be blank")
    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;

    @NotBlank(message = "State must not be blank")
    @Size(max = 100, message = "State must not exceed 100 characters")
    private String state;

    @NotBlank(message = "Postal code must not be blank")
    @Size(max = 20, message = "Postal code must not exceed 20 characters")
    @Column(name = "postal_code")
    private String postalCode;

    @NotBlank(message = "Country must not be blank")
    @Size(max = 100, message = "Country must not exceed 100 characters")
    private String country;

    public Address() {}

    @JsonbCreator
    public Address(@JsonbProperty("id") int id, @JsonbProperty("userId") int userId, @JsonbProperty("street") String street,
            @JsonbProperty("city") String city, @JsonbProperty("state") String state,
            @JsonbProperty("postalCode") String postalCode, @JsonbProperty("country") String country) {
        this.id = id;
        this.userId = userId;
        this.street = street;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.country = country;
    }

    public int getId() {
        return id;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public String getStreet() {
        return street;
    }
    
    public String getCity() {
        return city;
    }
    
    public String getState() {
        return state;
    }
    
    public String getPostalCode() {
        return postalCode;
    }
    
    public String getCountry() {
        return country;
    }
}