package integration;

import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;

import dto.OrderRequest;
import dto.OrderItemRequest;
import enums.OrderStatus;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.TestTransaction;

import java.util.List;

@QuarkusTest
class OrderControllerTest {

    @Test
    @TestTransaction
    void createOrder_shouldReturnCreatedOrder_whenRequestIsValid() {
        OrderRequest orderRequest = new OrderRequest(101, // Existing buyer ID from test-import.sql
            List.of(
                new OrderItemRequest(301, 1), // Product ID 301 with quantity 1
                new OrderItemRequest(302, 2)  // Product ID 302 with quantity 2
            )
        );

        given()
            .contentType("application/json")
            .body(orderRequest)
        .when()
            .post("/orders")
        .then()
            .statusCode(200);
    }

    @Test
    @TestTransaction
    void createOrder_shouldReturnBadRequest_whenRequestIsInvalid() {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setOauthId(102); // Valid buyer ID
        orderRequest.setItems(List.of(
            new OrderItemRequest(301, 0) // Invalid quantity (must be >= 1)
        ));

        given()
            .contentType("application/json")
            .body(orderRequest)
        .when()
            .post("/orders")
        .then()
            .statusCode(400)
            .log().all();
    }

    @Test
    @TestTransaction
    void getOrderById_shouldReturnOrder_whenOrderExists() {
        int orderId = 401; // Existing order ID from test-import.sql

        given()
            .pathParam("id", orderId)
        .when()
            .get("/orders/{id}")
        .then()
            .statusCode(200);
    }

    @Test
    @TestTransaction
    void getOrderById_shouldReturnNotFound_whenOrderDoesNotExist() {
        int orderId = 9999; // Non-existent order ID

        given()
            .pathParam("id", orderId)
        .when()
            .get("/orders/{id}")
        .then()
            .statusCode(404)
            .log().all();
    }

    @Test
    @TestTransaction
    void getAllOrdersByUser_shouldReturnOrders_whenUserExists() {
        int oauthId = 101; // Existing buyer ID from import.sql

        given()
            .pathParam("oauthId", oauthId)
        .when()
            .get("/orders/user/{oauthId}")
        .then()
            .statusCode(200)
            .log().all();
    }

    @Test
    @TestTransaction
    void getAllOrdersByUser_shouldReturnNotFound_whenUserDoesNotExist() {
        int oauthId = 9999; // Non-existent buyer ID

        given()
            .pathParam("oauthId", oauthId)
        .when()
            .get("/orders/user/{oauthId}")
        .then()
            .statusCode(404)
            .log().all();
    }

    @Test
    @TestTransaction
    void updateOrderStatus_shouldReturnUpdatedOrder_whenStatusIsValid() {
        int orderId = 401; // Existing order ID from import.sql
        String updatedStatus = OrderStatus.PENDING.name(); // Valid status

        given()
            .pathParam("id", orderId)
            .contentType("application/json")
            .body(updatedStatus)
        .when()
            .put("/orders/{id}")
        .then()
            .statusCode(200)
            .log().all();
    }

    @Test
    @TestTransaction
    void updateOrderStatus_shouldReturnNotFound_whenOrderDoesNotExist() {
        int orderId = 9999; // Non-existent order ID
        String updatedStatus = OrderStatus.PENDING.name(); // Valid status

        given()
            .pathParam("id", orderId)
            .contentType("application/json")
            .body(updatedStatus)
        .when()
            .put("/orders/{id}")
        .then()
            .statusCode(404)
            .log().all();
    }

    @Test
    @TestTransaction
    void deleteOrder_shouldReturnNoContent_whenOrderExists() {
        int orderId = 402; // Existing order ID from test-import.sql

        given()
            .pathParam("id", orderId)
        .when()
            .delete("/orders/{id}")
        .then()
            .statusCode(204);
    }

    @Test
    @TestTransaction
    void deleteOrder_shouldReturnNotFound_whenOrderDoesNotExist() {
        int orderId = 9999; // Non-existent order ID

        given()
            .pathParam("id", orderId)
        .when()
            .delete("/orders/{id}")
        .then()
            .statusCode(404)
            .log().all();
    }
}