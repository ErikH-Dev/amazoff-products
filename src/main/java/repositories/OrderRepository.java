package repositories;

import java.util.List;

import entities.Order;
import interfaces.IOrderRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class OrderRepository implements IOrderRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Uni<Order> create(Order order) {
        return Uni.createFrom().item(() -> {
            entityManager.persist(order);
            entityManager.flush();
            entityManager.refresh(order);
            return order;
        });
    }

    @Override
    public Uni<Order> read(int id) {
        return Uni.createFrom().item(() -> entityManager.find(Order.class, id))
            .onItem().ifNull().failWith(() -> new EntityNotFoundException("Order not found with ID: " + id));
    }

    @Override
    public Uni<List<Order>> readAllByUser(int oauthId) {
        return Uni.createFrom().item(() -> entityManager.createQuery(
            "SELECT o FROM Order o WHERE o.buyer.oauthId = :oauthId", Order.class
        ).setParameter("oauthId", oauthId).getResultList());
    }

    @Override
    public Uni<Order> update(Order order) {
        return Uni.createFrom().item(() -> entityManager.find(Order.class, order.getId()))
            .onItem().ifNull().failWith(() -> new EntityNotFoundException("Order not found with ID: " + order.getId()))
            .map(existingOrder -> entityManager.merge(order));
    }

    @Override
    public Uni<Void> delete(int id) {
        return Uni.createFrom().item(() -> entityManager.find(Order.class, id))
            .onItem().ifNull().failWith(() -> new EntityNotFoundException("Order not found with ID: " + id))
            .invoke(entityManager::remove)
            .replaceWithVoid();
    }
}