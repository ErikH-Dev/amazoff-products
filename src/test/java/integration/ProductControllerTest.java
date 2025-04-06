package integration;

import io.quarkus.test.TestTransaction;

import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;

import entities.Product;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class ProductControllerTest {

    @Test
    @TestTransaction
    void addProduct_shouldReturnCreatedProduct_whenProductIsValid() {
        Product product = new Product(
            "Tablet",
            500.00,
            "High-performance tablet"
        );

        given()
            .queryParam("oauthId", 103) // Existing vendor ID from test-import.sql
            .contentType("application/json")
            .body(product)
        .when()
            .post("/products")
        .then()
            .statusCode(200);
    }

    @Test
    @TestTransaction
    void addProduct_shouldReturnBadRequest_whenProductIsInvalid() {
        Product product = new Product(
            "",
            -1200.00,
            ""
        );

        given()
            .queryParam("oauthId", 103) // Existing vendor ID (oauthId) from import.sql
            .contentType("application/json")
            .body(product)
        .when()
            .post("/products")
        .then()
            .statusCode(400);
    }

    @Test
    @TestTransaction
    void getProductById_shouldReturnProduct_whenProductExists() {
        int productId = 301; // Existing product ID from test-import.sql

        given()
            .pathParam("id", productId)
        .when()
            .get("/products/{id}")
        .then()
            .statusCode(200);
    }

    @Test
    @TestTransaction
    void getProductById_shouldReturnNotFound_whenProductDoesNotExist() {
        int productId = 9999; // Assuming this product ID does not exist

        given()
            .pathParam("id", productId)
        .when()
            .get("/products/{id}")
        .then()
            .statusCode(404);
    }

    @Test
    @TestTransaction
    void updateProduct_shouldReturnUpdatedProduct_whenProductIsValid() {
        Product product = new Product(
            301, // Assuming this product ID exists
            "Laptop",
            1200.00,
            "High-performance laptop"
        );

        given()
            .queryParam("oauthId", 103) // Existing vendor ID (oauthId) from import.sql
            .contentType("application/json")
            .body(product)
        .when()
            .put("/products")
        .then()
            .statusCode(200);
    }

    @Test
    @TestTransaction
    void updateProduct_shouldReturnBadRequest_whenProductIsInvalid() {
        Product product = new Product(
            9999, // Assuming this product ID does not exist
            "",
            -1200.00,
            ""
        );

        given()
            .queryParam("oauthId", 103) // Existing vendor ID (oauthId) from import.sql
            .contentType("application/json")
            .body(product)
        .when()
            .put("/products")
        .then()
            .statusCode(400);
    }

    @Test
    @TestTransaction
    void deleteProduct_shouldReturnNoContent_whenProductExists() {
        int productId = 302; // Existing product ID from test-import.sql

        given()
            .pathParam("id", productId)
        .when()
            .delete("/products/{id}")
        .then()
            .statusCode(204);
    }

    @Test
    @TestTransaction
    void deleteProduct_shouldReturnNotFound_whenProductDoesNotExist() {
        int productId = 9999; // Assuming this product ID does not exist

        given()
            .pathParam("id", productId)
        .when()
            .delete("/products/{id}")
        .then()
            .statusCode(404);
    }
}