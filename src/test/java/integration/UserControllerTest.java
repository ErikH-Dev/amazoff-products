package integration;

import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;

import entities.Buyer;
import entities.Vendor;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class UserControllerTest {
    private Buyer createValidBuyer() {
        return new Buyer(1, 101, "John", "Doe", "john.doe@example.com");
    }

    private Vendor createValidVendor() {
        return new Vendor(3, 103, "Tech Store");
    }

    @Test
    void addUser_shouldReturnCreatedUser_whenUserIsValidBuyer() {
        Buyer buyer = createValidBuyer();

        given()
            .contentType("application/json")
            .body(buyer)
        .when()
            .post("/users")
        .then()
            .statusCode(200);
    }

    @Test
    void addUser_shouldReturnCreatedUser_whenUserIsValidVendor() {
        Vendor vendor = createValidVendor();

        given()
            .contentType("application/json")
            .body(vendor)
        .when()
            .post("/users")
        .then()
            .statusCode(200);
    }

    @Test
    void addUser_shouldReturnBadRequest_whenUserIsInvalidBuyer() {
        Buyer buyer = new Buyer(1, 101, "", "", "");

        given()
            .contentType("application/json")
            .body(buyer)
        .when()
            .post("/users")
        .then()
            .statusCode(400);
    }

    @Test
    void addUser_shouldReturnBadRequest_whenUserIsInvalidVendor() {
        Vendor vendor = new Vendor(3, 103, "");

        given()
            .contentType("application/json")
            .body(vendor)
        .when()
            .post("/users")
        .then()
            .statusCode(400);
    }

    @Test
    void getUserById_shouldReturnUser_whenUserExists() {
        int userId = 1;

        given()
            .pathParam("id", userId)
        .when()
            .get("/users/{id}")
        .then()
            .statusCode(200);
    }

    @Test
    void getUserById_shouldReturnNotFound_whenUserDoesNotExist() {
        int userId = 9999;

        given()
            .pathParam("id", userId)
        .when()
            .get("/users/{id}")
        .then()
            .statusCode(404);
    }

    @Test
    void updateUser_shouldReturnUpdatedUser_whenUserIsValid() {
        Buyer buyer = createValidBuyer();

        given()
            .contentType("application/json")
            .body(buyer)
        .when()
            .put("/users")
        .then()
            .statusCode(200);
    }

    @Test
    void updateUser_shouldReturnBadRequest_whenUserIsInvalid() {
        Buyer buyer = new Buyer(1, 101, "", "", "");

        given()
            .contentType("application/json")
            .body(buyer)
        .when()
            .put("/users")
        .then()
            .statusCode(400);
    }

    @Test
    void deleteUser_shouldReturnNoContent_whenUserExists() {
        int userId = 1;

        given()
            .pathParam("id", userId)
        .when()
            .delete("/users/{id}")
        .then()
            .statusCode(204);
    }

    @Test
    void deleteUser_shouldReturnNotFound_whenUserDoesNotExist() {
        int userId = 9999;

        given()
            .pathParam("id", userId)
        .when()
            .delete("/users/{id}")
        .then()
            .statusCode(404);
    }
}