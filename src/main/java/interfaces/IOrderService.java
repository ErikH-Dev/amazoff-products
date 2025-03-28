package interfaces;

import java.util.List;

import entities.Order;
import enums.OrderStatus;

public interface IOrderService {
    Order create(Order order);
    Order read(int id);
    List<Order> readAll();
    Order updateOrderStatus(int id, OrderStatus orderStatus);
    void delete(int id);
}
