package integration;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

import entities.Buyer;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.TestTransaction;

@QuarkusTest
class BuyerControllerTest {

    @Test
    @TestTransaction
    void addBuyer_shouldReturnCreatedBuyer_whenBuyerIsValid() {
        Buyer buyer = new Buyer(
            1,
            102,
            "John",
            "Doe",
            "john.doe@example.com"
        );

        given()
            .contentType("application/json")
            .body(buyer)
        .when()
            .post("/buyers")
        .then()
            .statusCode(200);
    }

    @Test
    @TestTransaction
    void addBuyer_shouldReturnBadRequest_whenBuyerIsInvalid() {
        Buyer buyer = new Buyer(
            1,
            102,
            "",
            "",
            ""
        );

        given()
            .contentType("application/json")
            .body(buyer)
        .when()
            .post("/buyers")
        .then()
            .statusCode(400);
    }

    @Test
    @TestTransaction
    void getBuyerById_shouldReturnBuyer_whenBuyerExists() {
        int oauthId = 101; // Existing buyer ID from test-import.sql

        given()
            .pathParam("id", oauthId)
        .when()
            .get("/buyers/{id}")
        .then()
            .statusCode(200);
    }

    @Test
    @TestTransaction
    void getBuyerById_shouldReturnNotFound_whenBuyerDoesNotExist() {
        int oauthId = 9999; // Assuming this buyer ID does not exist

        given()
            .pathParam("id", oauthId)
        .when()
            .get("/buyers/{id}")
        .then()
            .statusCode(404);
    }

    @Test
    @TestTransaction
    void updateBuyer_shouldReturnUpdatedBuyer_whenBuyerIsValid() {
        Buyer buyer = new Buyer(
            101,
            102,
            "Bop",
            "Bil",
            "bop.bil@example.com"
        );

        given()
            .contentType("application/json")
            .body(buyer)
        .when()
            .put("/buyers")
        .then()
            .statusCode(200);
    }

    @Test
    @TestTransaction
    void updateBuyer_shouldReturnBadRequest_whenBuyerIsInvalid() {
        Buyer buyer = new Buyer(
            101,
            102,
            "",
            "",
            ""
        );

        given()
            .contentType("application/json")
            .body(buyer)
        .when()
            .put("/buyers")
        .then()
            .statusCode(400);
    }

    @Test
    @TestTransaction
    void deleteBuyer_shouldReturnNoContent_whenBuyerExists() {
        int oauthId = 102; // Existing buyer ID from test-import.sql

        given()
            .pathParam("id", oauthId)
        .when()
            .delete("/buyers/{id}")
        .then()
            .statusCode(204);
    }

    @Test
    @TestTransaction
    void deleteBuyer_shouldReturnNotFound_whenBuyerDoesNotExist() {
        int oauthId = 9999; // Assuming this buyer ID does not exist

        given()
            .pathParam("id", oauthId)
        .when()
            .delete("/buyers/{id}")
        .then()
            .statusCode(404);
    }
}