package interfaces;

import java.util.List;

import entities.Order;

public interface IOrderRepository {
    Order create(Order order);
    Order read(int id);
    List<Order> readAllByUser(int oauthId);
    Order update(Order order);
    void delete(int id);
}
