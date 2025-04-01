package integration;

import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;

import entities.Product;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class ProductControllerTest {

    @Test
    void addProduct_shouldReturnCreatedProduct_whenProductIsValid() {
        Product product = new Product(
            "Laptop",
            1200.00,
            "High-performance laptop"
        );

        given()
            .contentType("application/json")
            .body(product)
        .when()
            .post("/products")
        .then()
            .statusCode(200);
    }

    @Test
    void addProduct_shouldReturnBadRequest_whenProductIsInvalid() {
        Product product = new Product(
            "",
            -1200.00,
            ""
        );

        given()
            .contentType("application/json")
            .body(product)
        .when()
            .post("/products")
        .then()
            .statusCode(400);
    }

    @Test
    void getProductById_shouldReturnProduct_whenProductExists() {
        int productId = 101; // Assuming this product ID exists

        given()
            .pathParam("id", productId)
        .when()
            .get("/products/{id}")
        .then()
            .statusCode(200);
    }

    @Test
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
    void updateProduct_shouldReturnUpdatedProduct_whenProductIsValid() {
        Product product = new Product(
            101, // Assuming this product ID exists
            "Laptop",
            1200.00,
            "High-performance laptop"
        );

        given()
            .contentType("application/json")
            .body(product)
        .when()
            .put("/products")
        .then()
            .statusCode(200);
    }

    @Test
    void updateProduct_shouldReturnBadRequest_whenProductIsInvalid() {
        Product product = new Product(
            9999, // Assuming this product ID does not exist
            "",
            -1200.00,
            ""
        );

        given()
            .contentType("application/json")
            .body(product)
        .when()
            .put("/products")
        .then()
            .statusCode(400);
    }

    @Test
    void deleteProduct_shouldReturnNoContent_whenProductExists() {
        int productId = 102; // Assuming this product ID exists

        given()
            .pathParam("id", productId)
        .when()
            .delete("/products/{id}")
        .then()
            .statusCode(204);
    }

    @Test
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