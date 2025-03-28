package entities;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ADMIN")
public class Admin extends User {
    public Admin() {}

    @JsonbCreator
    public Admin(@JsonbProperty("oauthId") int oauthId, @JsonbProperty("oauthProvider") int oauthProvider) {
        super(oauthId, oauthProvider);
    }
}