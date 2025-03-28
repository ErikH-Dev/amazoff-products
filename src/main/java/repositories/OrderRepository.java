package repositories;

import java.util.List;

import entities.Order;
import interfaces.IOrderRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@ApplicationScoped
public class OrderRepository implements IOrderRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Order create(Order order) {
        entityManager.persist(order);
        entityManager.flush();
        entityManager.refresh(order);
        return order;
    }

    @Override
    public Order read(int id) {
        Order order = entityManager.find(Order.class, id);
        if (order == null) {
            throw new EntityNotFoundException("Order not found with id: " + id);
        }
        return order;
    }

    @Override
    public List<Order> readAll() {
        TypedQuery<Order> query = entityManager.createQuery("SELECT o FROM Order o", Order.class);
        return query.getResultList();
    }

    @Override
    public Order update(Order order) {
        Order existingOrder = entityManager.find(Order.class, order.getId());
        if (existingOrder == null) {
            throw new EntityNotFoundException("Order not found with id: " + order.getId());
        }

        if (!existingOrder.getClass().equals(order.getClass())) {
            throw new IllegalArgumentException(
                    "Order type mismatch: cannot update " + existingOrder.getClass().getSimpleName() +
                            " with " + order.getClass().getSimpleName());
        }

        return entityManager.merge(order);
    }

    @Override
    public void delete(int id) {
        Order order = entityManager.find(Order.class, id);
        if (order == null) {
            throw new EntityNotFoundException("Order not found with id: " + id);
        }
        entityManager.remove(order);
    }
}
