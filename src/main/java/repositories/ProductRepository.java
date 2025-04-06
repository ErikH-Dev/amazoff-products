package repositories;

import java.util.List;

import entities.Product;
import interfaces.IProductRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class ProductRepository implements IProductRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Uni<Product> create(Product product) {
        return Uni.createFrom().item(() -> {
            entityManager.persist(product);
            entityManager.flush();
            entityManager.refresh(product);
            return product;
        });
    }

    @Override
    public Uni<List<Product>> readAll() {
        return Uni.createFrom().item(() -> entityManager.createQuery("SELECT p FROM Product p", Product.class)
            .getResultList());
    }

    @Override
    public Uni<Product> read(int id) {
        return Uni.createFrom().item(() -> entityManager.find(Product.class, id))
            .onItem().ifNull().failWith(() -> new EntityNotFoundException("Product not found with id: " + id));
    }

    @Override
    public Uni<Product> update(Product product) {
        return Uni.createFrom().item(() -> entityManager.find(Product.class, product.getId()))
            .onItem().ifNull().failWith(() -> new EntityNotFoundException("Product not found with id: " + product.getId()))
            .map(existingProduct -> entityManager.merge(product));
    }

    @Override
    public Uni<Void> delete(int id) {
        return Uni.createFrom().item(() -> entityManager.find(Product.class, id))
            .onItem().ifNull().failWith(() -> new EntityNotFoundException("Product not found with id: " + id))
            .invoke(entityManager::remove)
            .replaceWithVoid();
    }
}