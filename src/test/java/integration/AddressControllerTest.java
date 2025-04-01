package integration;

import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;

import entities.Address;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class AddressControllerTest {

    @Test
    void addAddress_shouldReturnCreatedAddress_whenAddressIsValid() {
        Address address = new Address(
            101,
            "Damrak 1",
            "Amsterdam",
            "Noord-Holland",
            "1012LG",
            "Netherlands"
        );

        given()
            .contentType("application/json")
            .body(address)
        .when()
            .post("/addresses")
        .then()
            .statusCode(200);
    }

    @Test
    void addAddress_shouldReturnBadRequest_whenAddressIsInvalid() {
        Address address = new Address(
            101,
            "",
            "",
            "",
            "",
            ""
        );

        given()
            .contentType("application/json")
            .body(address)
        .when()
            .post("/addresses")
        .then()
            .statusCode(400);
    }

    @Test
    void getAllAddressesByUser_shouldReturnAddresses_whenUserExists() {
        int oauthId = 101; // Assuming this user ID exists

        given()
            .pathParam("id", oauthId)
        .when()
            .get("/addresses/{id}")
        .then()
            .statusCode(200);
    }

    @Test
    void getAllAddressesByUser_shouldReturnNotFound_whenUserDoesNotExist() {
        int oauthId = 9999; // Assuming this user ID does not exist

        given()
            .pathParam("id", oauthId)
        .when()
            .get("/addresses/{id}")
        .then()
            .statusCode(404);
    }

    @Test
    void updateAddress_shouldReturnUpdatedAddress_whenAddressIsValid() {
        Address address = new Address(
            101,
            101,
            "Damrak 1",
            "Amsterdam",
            "Noord-Holland",
            "1012LG",
            "Netherlands"
        );

        given()
            .contentType("application/json")
            .body(address)
        .when()
            .put("/addresses")
        .then()
            .statusCode(200);
    }

    @Test
    void updateAddress_shouldReturnBadRequest_whenAddressIsInvalid() {
        Address address = new Address(
            101,
            101,
            "",
            "",
            "",
            "",
            ""
        );

        given()
            .contentType("application/json")
            .body(address)
        .when()
            .put("/addresses")
        .then()
            .statusCode(400);
    }

    @Test
    void deleteAddress_shouldReturnNoContent_whenAddressExists() {
        int addressId = 101; // Assuming this address ID exists

        given()
            .pathParam("id", addressId)
        .when()
            .delete("/addresses/{id}")
        .then()
            .statusCode(204);
    }

    @Test
    void deleteAddress_shouldReturnNotFound_whenAddressDoesNotExist() {
        int addressId = 9999; // Assuming this address ID does not exist

        given()
            .pathParam("id", addressId)
        .when()
            .delete("/addresses/{id}")
        .then()
            .statusCode(404);
    }
}