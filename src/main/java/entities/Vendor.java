package entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "VENDOR")
@DiscriminatorValue("VENDOR")
@PrimaryKeyJoinColumn(name = "oauthId")
public class Vendor extends User {
    @NotBlank(message = "Store name must not be blank")
    @Size(max = 100, message = "Store name must not exceed 100 characters")
    private String storeName;

    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonbTransient
    private List<Product> products = new ArrayList<>();

    public Vendor() {
    }

    @JsonbCreator
    public Vendor(@JsonbProperty("oauthId") int oauthId, @JsonbProperty("oauthProvider") int oauthProvider,
            @JsonbProperty("storeName") String storeName) {
        super(oauthId, oauthProvider);
        this.storeName = storeName;
    }

    public String getStoreName() {
        return storeName;
    }

    public List<Product> getProducts() {
        return products;
    }
}