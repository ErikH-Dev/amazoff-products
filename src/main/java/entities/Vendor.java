package entities;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("VENDOR")
public class Vendor extends User {
    private String storeName;

    public Vendor() {}

    @JsonbCreator
    public Vendor(@JsonbProperty("oauthId") int oauthId, @JsonbProperty("oauthProvider") int oauthProvider, @JsonbProperty("storeName") String storeName) {
        super(oauthId, oauthProvider);
        this.storeName = storeName;
    }

    public String getStoreName() {
        return storeName;
    }
}