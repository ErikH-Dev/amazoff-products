package services;

import java.time.LocalDateTime;
import java.util.List;

import dto.OrderRequest;
import entities.Buyer;
import entities.Order;
import entities.OrderItem;
import entities.Product;
import enums.OrderStatus;
import interfaces.IBuyerRepository;
import interfaces.IOrderRepository;
import interfaces.IOrderService;
import interfaces.IProductRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class OrderService implements IOrderService {
    private final IOrderRepository orderRepository;
    private final IBuyerRepository buyerRepository;
    private final IProductRepository productRepository;

    public OrderService(IOrderRepository orderRepository, IBuyerRepository buyerRepository, IProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.buyerRepository = buyerRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public Order create(OrderRequest orderRequest) {
        Buyer buyer = buyerRepository.read(orderRequest.getOauthId());
        if (buyer == null) {
            throw new EntityNotFoundException("Buyer not found with ID: " + orderRequest.getOauthId());
        }
    
        List<OrderItem> orderItems = orderRequest.getItems().stream().map(itemRequest -> {
            Product product = productRepository.read(itemRequest.getProductId());
            if (product == null) {
                throw new EntityNotFoundException("Product not found with ID: " + itemRequest.getProductId());
            }
            double priceAtPurchase = product.getPrice() * itemRequest.getQuantity();
            return new OrderItem(null, product.getName(), product.getPrice(), product.getDescription(),
                    itemRequest.getQuantity(), priceAtPurchase);
        }).toList();
    
        Order order = new Order(buyer, orderItems, OrderStatus.PENDING, LocalDateTime.now());
    
        for (OrderItem orderItem : orderItems) {
            orderItem.setOrder(order);
        }
    
        return orderRepository.create(order);
    }

    @Override
    public Order read(int id) {
        return orderRepository.read(id);
    }

    @Override
    public List<Order> readAllByUser(int oauthId) {
        Buyer buyer = buyerRepository.read(oauthId);
        if (buyer == null) {
            throw new EntityNotFoundException("Buyer not found with ID: " + oauthId);
        }
        List<Order> orders = orderRepository.readAllByUser(oauthId);
        if (orders.isEmpty()) {
            throw new EntityNotFoundException("No orders found for Buyer with ID: " + oauthId);
        }
        return orders;
    }

    @Override
    @Transactional
    public Order updateOrderStatus(int id, OrderStatus orderStatus) {
        Order order = orderRepository.read(id);
        if (order == null) {
            throw new EntityNotFoundException("Order not found with ID: " + id);
        }
        order.setStatus(orderStatus);
        return orderRepository.update(order);
    }

    @Override
    @Transactional
    public void delete(int id) {
        orderRepository.delete(id);
    }
}