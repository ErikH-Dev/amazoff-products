package unit;

import dto.OrderRequest;
import dto.OrderItemRequest;
import entities.Buyer;
import entities.Order;
import entities.Product;
import enums.OrderStatus;
import interfaces.IBuyerRepository;
import interfaces.IOrderRepository;
import interfaces.IProductRepository;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.EntityNotFoundException;
import services.OrderService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {
    private IOrderRepository orderRepository;
    private IBuyerRepository buyerRepository;
    private IProductRepository productRepository;
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderRepository = mock(IOrderRepository.class);
        buyerRepository = mock(IBuyerRepository.class);
        productRepository = mock(IProductRepository.class);
        orderService = new OrderService(orderRepository, buyerRepository, productRepository);
    }

    @Test
    void createOrder_shouldReturnCreatedOrder_whenRequestIsValid() {
        OrderRequest orderRequest = mock(OrderRequest.class);
        Buyer buyer = new Buyer();
        Product product = new Product(1, "Product Name", 100.0, "Product Description");
        OrderItemRequest itemRequest = new OrderItemRequest(1, 2); // Mock item request with productId and quantity

        when(orderRequest.getOauthId()).thenReturn(1);
        when(orderRequest.getItems()).thenReturn(List.of(itemRequest));
        when(buyerRepository.read(orderRequest.getOauthId())).thenReturn(Uni.createFrom().item(buyer));
        when(productRepository.read(itemRequest.getProductId())).thenReturn(Uni.createFrom().item(product));
        when(orderRepository.create(any(Order.class))).thenReturn(Uni.createFrom().item(new Order()));

        Uni<Order> result = orderService.create(orderRequest);

        assertNotNull(result.await().indefinitely());
        verify(buyerRepository, times(1)).read(orderRequest.getOauthId());
        verify(productRepository, times(1)).read(itemRequest.getProductId());
        verify(orderRepository, times(1)).create(any(Order.class));
    }

    @Test
    void createOrder_shouldThrowEntityNotFoundException_whenBuyerDoesNotExist() {
        OrderRequest orderRequest = new OrderRequest();
        when(buyerRepository.read(orderRequest.getOauthId())).thenReturn(Uni.createFrom().nullItem());

        Uni<Order> result = orderService.create(orderRequest);

        assertThrows(EntityNotFoundException.class, () -> result.await().indefinitely());
        verify(buyerRepository, times(1)).read(orderRequest.getOauthId());
        verify(orderRepository, never()).create(any());
    }

    @Test
    void getOrderById_shouldReturnOrder_whenOrderExists() {
        int orderId = 1;
        Order order = new Order();
        when(orderRepository.read(orderId)).thenReturn(Uni.createFrom().item(order));

        Uni<Order> result = orderService.read(orderId);

        assertEquals(order, result.await().indefinitely());
        verify(orderRepository, times(1)).read(orderId);
    }

    @Test
    void getOrderById_shouldReturnNull_whenOrderDoesNotExist() {
        int orderId = 1;
        when(orderRepository.read(orderId)).thenReturn(Uni.createFrom().nullItem());

        Uni<Order> result = orderService.read(orderId);

        assertNull(result.await().indefinitely());
        verify(orderRepository, times(1)).read(orderId);
    }

    @Test
    void getAllOrdersByUser_shouldReturnOrders_whenUserExists() {
        int oauthId = 1;
        Buyer buyer = new Buyer();
        List<Order> orders = List.of(new Order(), new Order());
        when(buyerRepository.read(oauthId)).thenReturn(Uni.createFrom().item(buyer));
        when(orderRepository.readAllByUser(oauthId)).thenReturn(Uni.createFrom().item(orders));

        Uni<List<Order>> result = orderService.readAllByUser(oauthId);

        assertEquals(orders, result.await().indefinitely());
        verify(buyerRepository, times(1)).read(oauthId);
        verify(orderRepository, times(1)).readAllByUser(oauthId);
    }

    @Test
    void getAllOrdersByUser_shouldThrowEntityNotFoundException_whenUserDoesNotExist() {
        int oauthId = 1;
        when(buyerRepository.read(oauthId)).thenReturn(Uni.createFrom().nullItem());

        Uni<List<Order>> result = orderService.readAllByUser(oauthId);

        assertThrows(EntityNotFoundException.class, () -> result.await().indefinitely());
        verify(buyerRepository, times(1)).read(oauthId);
        verify(orderRepository, never()).readAllByUser(anyInt());
    }

    @Test
    void updateOrderStatus_shouldReturnUpdatedOrder_whenStatusIsValid() {
        int orderId = 1;
        Order order = new Order();
        OrderStatus newStatus = OrderStatus.DELIVERED;
        when(orderRepository.read(orderId)).thenReturn(Uni.createFrom().item(order));
        when(orderRepository.update(order)).thenReturn(Uni.createFrom().item(order));

        Uni<Order> result = orderService.updateOrderStatus(orderId, newStatus);

        assertEquals(order, result.await().indefinitely());
        assertEquals(newStatus, order.getStatus());
        verify(orderRepository, times(1)).read(orderId);
        verify(orderRepository, times(1)).update(order);
    }

    @Test
    void updateOrderStatus_shouldThrowEntityNotFoundException_whenOrderDoesNotExist() {
        int orderId = 1;
        OrderStatus newStatus = OrderStatus.DELIVERED;
        when(orderRepository.read(orderId)).thenReturn(Uni.createFrom().nullItem());

        Uni<Order> result = orderService.updateOrderStatus(orderId, newStatus);

        assertThrows(EntityNotFoundException.class, () -> result.await().indefinitely());
        verify(orderRepository, times(1)).read(orderId);
        verify(orderRepository, never()).update(any());
    }

    @Test
    void deleteOrder_shouldReturnNoContent_whenOrderExists() {
        int orderId = 1;
        when(orderRepository.delete(orderId)).thenReturn(Uni.createFrom().voidItem());

        Uni<Void> result = orderService.delete(orderId);

        assertDoesNotThrow(() -> result.await().indefinitely());
        verify(orderRepository, times(1)).delete(orderId);
    }

    @Test
    void deleteOrder_shouldThrowRuntimeException_whenOrderDoesNotExist() {
        int orderId = 1;
        when(orderRepository.delete(orderId)).thenReturn(Uni.createFrom().failure(new RuntimeException("Error")));

        Uni<Void> result = orderService.delete(orderId);

        assertThrows(RuntimeException.class, () -> result.await().indefinitely());
        verify(orderRepository, times(1)).delete(orderId);
    }
}
