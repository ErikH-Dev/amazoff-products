package integration;

import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;

import entities.Address;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.TestTransaction;

@QuarkusTest
class AddressControllerTest {

    @Test
    @TestTransaction
    void addAddress_shouldReturnCreatedAddress_whenAddressIsValid() {
        Address address = new Address(
            "Damrak 1",
            "Amsterdam",
            "Noord-Holland",
            "1012LG",
            "Netherlands"
        );

        given()
            .queryParam("oauthId", 101) // Existing user ID (oauthId) from import.sql
            .contentType("application/json")
            .body(address)
        .when()
            .post("/addresses")
        .then()
            .statusCode(200)
            .log().all();
    }

    @Test
    @TestTransaction
    void addAddress_shouldReturnBadRequest_whenAddressIsInvalid() {
        Address address = new Address(
            "", // Invalid street
            "", // Invalid city
            "", // Invalid state
            "", // Invalid postal code
            ""  // Invalid country
        );

        given()
            .queryParam("oauthId", 101) // Existing user ID (oauthId) from import.sql
            .contentType("application/json")
            .body(address)
        .when()
            .post("/addresses")
        .then()
            .statusCode(400)
            .log().all();
    }

    @Test
    @TestTransaction
    void getAllAddressesByUser_shouldReturnAddresses_whenUserExists() {
        int oauthId = 101; // Existing user ID from import.sql

        given()
            .pathParam("oauthId", oauthId)
        .when()
            .get("/addresses/{oauthId}")
        .then()
            .statusCode(200)
            .log().all();
    }

    @Test
    @TestTransaction
    void getAllAddressesByUser_shouldReturnNotFound_whenUserDoesNotExist() {
        int oauthId = 9999; // Non-existent user ID

        given()
            .pathParam("oauthId", oauthId)
        .when()
            .get("/addresses/{oauthId}")
        .then()
            .statusCode(404)
            .log().all();
    }

    @Test
    @TestTransaction
    void updateAddress_shouldReturnUpdatedAddress_whenAddressIsValid() {
        Address address = new Address(
            201,
            "Updated Street",
            "Updated City",
            "Updated State",
            "99999",
            "Updated Country"
        );

        given()
            .queryParam("oauthId", 101)
            .contentType("application/json")
            .body(address)
        .when()
            .put("/addresses")
        .then()
            .statusCode(200)
            .log().all();
    }

    @Test
    @TestTransaction
    void updateAddress_shouldReturnBadRequest_whenAddressIsInvalid() {
        Address address = new Address(
            201, // Existing address ID from import.sql
            "", // Invalid street
            "", // Invalid city
            "", // Invalid state
            "", // Invalid postal code
            ""  // Invalid country
        );

        given()
            .contentType("application/json")
            .body(address)
        .when()
            .put("/addresses")
        .then()
            .statusCode(400)
            .log().all();
    }

    @Test
    @TestTransaction
    void deleteAddress_shouldReturnNoContent_whenAddressExists() {
        int addressId = 201;

        given()
            .pathParam("id", addressId)
        .when()
            .delete("/addresses/{id}")
        .then()
            .statusCode(204)
            .log().all();
    }

    @Test
    @TestTransaction
    void deleteAddress_shouldReturnNotFound_whenAddressDoesNotExist() {
        int addressId = 9999; // Non-existent address ID

        given()
            .pathParam("id", addressId)
        .when()
            .delete("/addresses/{id}")
        .then()
            .statusCode(404)
            .log().all();
    }
}