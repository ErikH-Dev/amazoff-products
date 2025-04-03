package entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import enums.OrderStatus;
import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "`Order`")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @NotEmpty(message = "Order must contain at least one order item")
    @JsonbTransient
    private List<OrderItem> orderItems = new ArrayList<>();

    @NotNull(message = "Order status must not be null")
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @NotNull(message = "Order date must not be null")
    private LocalDateTime orderDate;

    @ManyToOne
    @JoinColumn(name = "oauthId", nullable = false)
    @JsonbTransient
    private Buyer buyer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Payment> payments = new ArrayList<>();

    public Order() {
    }

    public Order(Buyer buyer, List<OrderItem> orderItems, OrderStatus status, LocalDateTime orderDate) {
        this.buyer = buyer;
        this.orderItems = orderItems;
        this.status = status;
        this.orderDate = orderDate;
    }

    @JsonbCreator
    public Order(@JsonbProperty("id") int id, @JsonbProperty("buyer") Buyer buyer,
            @JsonbProperty("orderItems") List<OrderItem> orderItems, @JsonbProperty("status") OrderStatus status,
            @JsonbProperty("orderDate") LocalDateTime orderDate) {
        this.id = id;
        this.buyer = buyer;
        this.orderItems = orderItems;
        this.status = status;
        this.orderDate = orderDate;
    }

    public int getId() {
        return id;
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

    public Buyer getBuyer() {
        return buyer;
    }
}