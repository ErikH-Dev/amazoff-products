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
            throw new EntityNotFoundException("Order not found with ID: " + id);
        }
        return order;
    }

    @Override
    public List<Order> readAllByUser(int oauthId) {
        TypedQuery<Order> query = entityManager.createQuery(
            "SELECT o FROM Order o WHERE o.buyer.oauthId = :oauthId", Order.class);
        query.setParameter("oauthId", oauthId);
        return query.getResultList();
    }

    @Override
    public Order update(Order order) {
        return entityManager.merge(order);
    }

    @Override
    public void delete(int id) {
        Order order = entityManager.find(Order.class, id);
        if (order == null) {
            throw new EntityNotFoundException("Order not found with ID: " + id);
        }
        entityManager.remove(order);
    }
}