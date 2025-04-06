package interfaces;

import java.util.List;

import dto.OrderRequest;
import entities.Order;
import enums.OrderStatus;
import io.smallrye.mutiny.Uni;

public interface IOrderService {
    Uni<Order> create(OrderRequest orderRequest);
    Uni<Order> read(int id);
    Uni<List<Order>> readAllByUser(int oauthId);
    Uni<Order> updateOrderStatus(int id, OrderStatus orderStatus);
    Uni<Void> delete(int id);
}