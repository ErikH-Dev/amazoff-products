package adapters;

import enums.OrderStatus;
import jakarta.json.bind.adapter.JsonbAdapter;

public class OrderStatusAdapter implements JsonbAdapter<OrderStatus, String> {

    @Override
    public String adaptToJson(OrderStatus orderStatus) {
        // Convert the enum to its string representation
        return orderStatus.name();
    }

    @Override
    public OrderStatus adaptFromJson(String json) {
        // Convert the string back to the enum
        return OrderStatus.valueOf(json.toUpperCase());
    }
}