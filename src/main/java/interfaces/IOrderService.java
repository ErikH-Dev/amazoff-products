package interfaces;

import java.util.List;

import dto.OrderRequest;
import entities.Order;
import enums.OrderStatus;

public interface IOrderService {
    Order create(OrderRequest orderRequest);
    Order read(int id);
    List<Order> readAllByUser(int oauthId);
    Order updateOrderStatus(int id, OrderStatus orderStatus);
    void delete(int id);
}
