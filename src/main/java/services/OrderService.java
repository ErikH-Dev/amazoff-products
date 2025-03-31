package services;

import java.util.List;

import entities.Order;
import enums.OrderStatus;
import interfaces.IOrderRepository;
import interfaces.IOrderService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class OrderService implements IOrderService {
    private IOrderRepository orderRepository;

    public OrderService(IOrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional
    public Order create(Order order) {
        return orderRepository.create(order);
    }

    @Override
    public Order read(int id) {
        return orderRepository.read(id);
    }

    @Override
    public List<Order> readAll() {
        return orderRepository.readAll();
    }

    @Override
    @Transactional
    public Order updateOrderStatus(int id, OrderStatus orderStatus) {
        Order order = orderRepository.read(id);
        order.setStatus(orderStatus);
        return orderRepository.update(order);
    }

    @Override
    @Transactional
    public void delete(int id) {
        orderRepository.delete(id);
    }
}
