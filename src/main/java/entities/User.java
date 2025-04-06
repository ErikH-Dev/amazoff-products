package entities;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
@Table(name = "App_User")
public abstract class User {
    @Id
    @NotNull(message = "OAuth ID must not be null")
    private int oauthId;

    @NotNull(message = "OAuth provider must not be null")
    private int oauthProvider;

    protected User() {}

    @JsonbCreator
    protected User(@JsonbProperty("oauthId") int oauthId, @JsonbProperty("oauthProvider") int oauthProvider) {
        this.oauthId = oauthId;
        this.oauthProvider = oauthProvider;
    }

    public int getOauthId() {
        return oauthId;
    }

    public int getOauthProvider() {
        return oauthProvider;
    }
}