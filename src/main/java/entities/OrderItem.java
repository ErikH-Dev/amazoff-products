package entities;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @NotNull
    private Order order;

    @ManyToOne
    @NotNull
    private Product product;

    @NotNull
    private int quantity;

    @NotNull
    private double priceAtPurchase;

    public OrderItem() {}

    @JsonbCreator
    public OrderItem(@JsonbProperty("id") int id, @JsonbProperty("order") Order order,
            @JsonbProperty("product") Product product, @JsonbProperty("quantity") int quantity,
            @JsonbProperty("priceAtPurchase") double priceAtPurchase) {
        this.id = id;
        this.order = order;
        this.product = product;
        this.quantity = quantity;
        this.priceAtPurchase = priceAtPurchase;
    }

    public int getId() {
        return id;
    }

    public Order getOrder() {
        return order;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPriceAtPurchase() {
        return priceAtPurchase;
    }
}