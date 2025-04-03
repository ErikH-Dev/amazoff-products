package entities;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id", nullable = false)
    @JsonbTransient
    private Order order;

    @NotNull
    private String productName;

    @NotNull
    private double productPrice;

    @NotNull
    private String productDescription;

    @NotNull
    private int quantity;

    @NotNull
    private double priceAtPurchase;

    public OrderItem() {}

    public OrderItem(Order order, String productName, double productPrice, String productDescription, int quantity, double priceAtPurchase) {
        this.order = order;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productDescription = productDescription;
        this.quantity = quantity;
        this.priceAtPurchase = priceAtPurchase;
    }

    @JsonbCreator
    public OrderItem(@JsonbProperty("id") int id, @JsonbProperty("order") Order order,
                     @JsonbProperty("productName") String productName, @JsonbProperty("productPrice") double productPrice,
                     @JsonbProperty("productDescription") String productDescription, @JsonbProperty("quantity") int quantity,
                     @JsonbProperty("priceAtPurchase") double priceAtPurchase) {
        this.id = id;
        this.order = order;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productDescription = productDescription;
        this.quantity = quantity;
        this.priceAtPurchase = priceAtPurchase;
    }

    public int getId() {
        return id;
    }

    public Order getOrder() {
        return order;
    }

    public String getProductName() {
        return productName;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPriceAtPurchase() {
        return priceAtPurchase;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}