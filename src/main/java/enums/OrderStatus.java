package enums;

import adapters.OrderStatusAdapter;
import jakarta.json.bind.annotation.JsonbTypeAdapter;

@JsonbTypeAdapter(OrderStatusAdapter.class)
public enum OrderStatus {
    PENDING, SHIPPED, DELIVERED, CANCELLED
}
