package entities;

import java.time.LocalDateTime;
import java.util.List;

import enums.OrderStatus;
import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @NotNull(message = "User must not be null")
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @NotEmpty(message = "Order must contain at least one order item")
    private List<OrderItem> orderItems;

    @NotNull(message = "Order status must not be null")
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @NotNull(message = "Order date must not be null")
    private LocalDateTime orderDate;

    public Order() {}

    @JsonbCreator
    public Order(@JsonbProperty("id") int id, @JsonbProperty("user") User user,
            @JsonbProperty("orderItems") List<OrderItem> orderItems, @JsonbProperty("status") OrderStatus status,
            @JsonbProperty("orderDate") LocalDateTime orderDate) {
        this.id = id;
        this.user = user;
        this.orderItems = orderItems;
        this.status = status;
        this.orderDate = orderDate;
    }

    public int getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }
}