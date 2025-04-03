package utils;

import enums.OrderStatus;

public class OrderStatusUtils {
    private OrderStatusUtils() {
        // Private constructor to prevent instantiation
    }
    public static OrderStatus fromString(String value) {
        try {
            return OrderStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalArgumentException("Invalid order status: " + value);
        }
    }
}