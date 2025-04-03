package entities;

import java.time.LocalDateTime;

import enums.PaymentStatus;
import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @NotBlank(message = "Stripe payment ID must not be blank")
    private String stripePaymentId;

    @NotNull(message = "Amount must not be null")
    @Positive(message = "Amount must be greater than zero")
    private Double amount;

    @NotNull(message = "Payment status must not be null")
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @NotNull(message = "Payment date must not be null")
    private LocalDateTime paymentDate;

    public Payment() {
    }

    public Payment(Order order, String stripePaymentId, Double amount, PaymentStatus status, LocalDateTime paymentDate) {
        this.order = order;
        this.stripePaymentId = stripePaymentId;
        this.amount = amount;
        this.status = status;
        this.paymentDate = paymentDate;
    }

    @JsonbCreator
    public Payment(@JsonbProperty("id") int id, @JsonbProperty("order") Order order,
                   @JsonbProperty("stripePaymentId") String stripePaymentId, @JsonbProperty("amount") Double amount,
                   @JsonbProperty("status") PaymentStatus status, @JsonbProperty("paymentDate") LocalDateTime paymentDate) {
        this.id = id;
        this.order = order;
        this.stripePaymentId = stripePaymentId;
        this.amount = amount;
        this.status = status;
        this.paymentDate = paymentDate;
    }

    public int getId() {
        return id;
    }

    public Order getOrder() {
        return order;
    }

    public String getStripePaymentId() {
        return stripePaymentId;
    }

    public Double getAmount() {
        return amount;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }
}