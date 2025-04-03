package unit;

import adapters.OrderStatusAdapter;
import enums.OrderStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderStatusAdapterTest {

    private final OrderStatusAdapter adapter = new OrderStatusAdapter();

    @Test
    void testAdaptToJson() {
        assertEquals("PENDING", adapter.adaptToJson(OrderStatus.PENDING));
        assertEquals("SHIPPED", adapter.adaptToJson(OrderStatus.SHIPPED));
    }

    @Test
    void testAdaptFromJson() {
        assertEquals(OrderStatus.PENDING, adapter.adaptFromJson("PENDING"));
        assertEquals(OrderStatus.SHIPPED, adapter.adaptFromJson("shipped"));
    }
}