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
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityNotFoundException;

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
    public Uni<Order> create(OrderRequest orderRequest) {
        return buyerRepository.read(orderRequest.getOauthId())
            .onItem().ifNull().failWith(() -> new EntityNotFoundException("Buyer not found with ID: " + orderRequest.getOauthId()))
            .flatMap(buyer -> {
                List<Uni<OrderItem>> orderItemsUnis = orderRequest.getItems().stream()
                    .map(itemRequest -> productRepository.read(itemRequest.getProductId())
                        .onItem().ifNull().failWith(() -> new EntityNotFoundException("Product not found with ID: " + itemRequest.getProductId()))
                        .map(product -> {
                            double priceAtPurchase = product.getPrice() * itemRequest.getQuantity();
                            return new OrderItem(null, product.getName(), product.getPrice(), product.getDescription(),
                                itemRequest.getQuantity(), priceAtPurchase);
                        }))
                    .toList();

                return Uni.combine().all().unis(orderItemsUnis).combinedWith(orderItems -> {
                    List<OrderItem> orderItemsList = (List<OrderItem>) orderItems;
                    Order order = new Order(buyer, orderItemsList, OrderStatus.PENDING, LocalDateTime.now());
                    orderItemsList.forEach(orderItem -> orderItem.setOrder(order));
                    return order;
                }).flatMap(orderRepository::create);
            });
    }

    @Override
    public Uni<Order> read(int id) {
        return orderRepository.read(id);
    }

    @Override
    public Uni<List<Order>> readAllByUser(int oauthId) {
        return buyerRepository.read(oauthId)
            .onItem().ifNull().failWith(() -> new EntityNotFoundException("Buyer not found with ID: " + oauthId))
            .flatMap(buyer -> orderRepository.readAllByUser(oauthId));
    }

    @Override
    public Uni<Order> updateOrderStatus(int id, OrderStatus orderStatus) {
        return orderRepository.read(id)
            .onItem().ifNull().failWith(() -> new EntityNotFoundException("Order not found with ID: " + id))
            .flatMap(order -> {
                order.setStatus(orderStatus);
                return orderRepository.update(order);
            });
    }

    @Override
    public Uni<Void> delete(int id) {
        return orderRepository.delete(id);
    }
}