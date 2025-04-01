package entities;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "BUYER")
@DiscriminatorValue("BUYER")
@PrimaryKeyJoinColumn(name = "oauthId")
public class Buyer extends User {
    @NotBlank(message = "First name must not be blank")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    private String firstName;

    @NotBlank(message = "Last name must not be blank")
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    private String lastName;

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email must be a valid email address")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    public Buyer() {
    }

    @JsonbCreator
    public Buyer(@JsonbProperty("oauthId") int oauthId,
            @JsonbProperty("oauthProvider") int oauthProvider,
            @JsonbProperty("firstName") String firstName,
            @JsonbProperty("lastName") String lastName,
            @JsonbProperty("email") String email) {
        super(oauthId, oauthProvider);
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }
}