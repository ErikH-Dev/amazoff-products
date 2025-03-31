package integration;

import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;

import entities.Product;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class ProductControllerTest {

    private Product createValidProduct() {
        return new Product(
            1,
            "Laptop",
            1200.00,
            "High-performance laptop"
        );
    }

    private Product createInvalidProduct() {
        return new Product(
            0,
            "",
            -1200.00,
            ""
        );
    }

    @Test
    void addProduct_shouldReturnCreatedProduct_whenProductIsValid() {
        Product product = createValidProduct();

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
        Product product = createInvalidProduct();

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
        int productId = 1; // Assuming this product ID exists

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
        Product product = createValidProduct();

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
        Product product = createInvalidProduct();

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
        int productId = 2; // Assuming this product ID exists

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