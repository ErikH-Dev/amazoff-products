package integration;

import io.quarkus.test.TestTransaction;

import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;

import entities.Vendor;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class VendorControllerTest {

    @Test
    @TestTransaction
    void addVendor_shouldReturnCreatedVendor_whenVendorIsValid() {
        Vendor vendor = new Vendor(
            107,
            1,
            "New Tech Store"
        );

        given()
            .contentType("application/json")
            .body(vendor)
        .when()
            .post("/vendors")
        .then()
            .statusCode(200);
    }

    @Test
    @TestTransaction
    void addVendor_shouldReturnBadRequest_whenVendorIsInvalid() {
        Vendor vendor = new Vendor(
            1,
            102,
            "" // Invalid store name
        );

        given()
            .contentType("application/json")
            .body(vendor)
        .when()
            .post("/vendors")
        .then()
            .statusCode(400);
    }

    @Test
    @TestTransaction
    void getVendorById_shouldReturnVendor_whenVendorExists() {
        int oauthId = 103; // Existing vendor ID from test-import.sql

        given()
            .pathParam("id", oauthId)
        .when()
            .get("/vendors/{id}")
        .then()
            .statusCode(200);
    }

    @Test
    @TestTransaction
    void getVendorById_shouldReturnNotFound_whenVendorDoesNotExist() {
        int oauthId = 9999; // Assuming this vendor ID does not exist

        given()
            .pathParam("id", oauthId)
        .when()
            .get("/vendors/{id}")
        .then()
            .statusCode(404);
    }

    @Test
    @TestTransaction
    void updateVendor_shouldReturnUpdatedVendor_whenVendorIsValid() {
        Vendor vendor = new Vendor(
            103,
            102,
            "Updated Tech Store"
        );

        given()
            .contentType("application/json")
            .body(vendor)
        .when()
            .put("/vendors")
        .then()
            .statusCode(200);
    }

    @Test
    @TestTransaction
    void updateVendor_shouldReturnBadRequest_whenVendorIsInvalid() {
        Vendor vendor = new Vendor(
            104,
            102,
            "" // Invalid store name
        );

        given()
            .contentType("application/json")
            .body(vendor)
        .when()
            .put("/vendors")
        .then()
            .statusCode(400);
    }

    @Test
    @TestTransaction
    void deleteVendor_shouldReturnNoContent_whenVendorExists() {
        int oauthId = 104; // Existing vendor ID from test-import.sql

        given()
            .pathParam("id", oauthId)
        .when()
            .delete("/vendors/{id}")
        .then()
            .statusCode(204);
    }

    @Test
    @TestTransaction
    void deleteVendor_shouldReturnNotFound_whenVendorDoesNotExist() {
        int oauthId = 9999; // Assuming this vendor ID does not exist

        given()
            .pathParam("id", oauthId)
        .when()
            .delete("/vendors/{id}")
        .then()
            .statusCode(404);
    }
}