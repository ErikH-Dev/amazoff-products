package repositories;

import java.util.List;

import entities.Product;
import exceptions.errors.ProductNotFoundException;
import interfaces.IProductRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.hibernate.reactive.mutiny.Mutiny.SessionFactory;

@ApplicationScoped
public class ProductRepository implements IProductRepository {

    @Inject
    SessionFactory sessionFactory;

    @Override
    public Uni<Product> create(Product product) {
        return sessionFactory.withTransaction(session -> session.persist(product).replaceWith(product))
            .onFailure().invoke(e -> {
                throw new RuntimeException("Failed to create product: " + e.getMessage(), e);
            });
    }

    @Override
    public Uni<List<Product>> readAll() {
        return sessionFactory.withSession(session -> session.createQuery("FROM Product", Product.class).getResultList())
            .onFailure().invoke(e -> {
                throw new RuntimeException("Failed to retrieve products: " + e.getMessage(), e);
            });
    }

    @Override
    public Uni<List<Product>> readByIds(List<Integer> ids) {
        return sessionFactory.withSession(session -> session.createQuery("FROM Product WHERE id IN :ids", Product.class)
            .setParameter("ids", ids).getResultList())
            .onFailure().invoke(e -> {
                throw new RuntimeException("Failed to retrieve products by IDs: " + e.getMessage(), e);
            });
    }

    @Override
    public Uni<Product> read(int id) {
        return sessionFactory.withSession(session -> session.find(Product.class, id))
            .onItem().ifNull().failWith(() -> new ProductNotFoundException(id));
    }

    @Override
    public Uni<Product> update(Product product) {
        return sessionFactory.withTransaction(session -> 
            session.find(Product.class, product.getId())
                .onItem().ifNull().failWith(() -> new ProductNotFoundException(product.getId()))
                .onItem().ifNotNull().transformToUni(found -> session.merge(product))
        );
    }

    @Override
    public Uni<Void> delete(int id) {
        return sessionFactory.withTransaction(session -> session.find(Product.class, id)
            .onItem().ifNull().failWith(() -> new ProductNotFoundException(id))
            .onItem().ifNotNull().invoke(session::remove)
            .replaceWithVoid());
    }
}