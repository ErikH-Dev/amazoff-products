package repositories;

import java.util.List;

import entities.Product;
import exceptions.errors.ProductNotFoundException;
import interfaces.IProductRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.hibernate.reactive.mutiny.Mutiny.SessionFactory;
import org.jboss.logging.Logger;

@ApplicationScoped
public class ProductRepository implements IProductRepository {

    private static final Logger LOG = Logger.getLogger(ProductRepository.class);

    @Inject
    SessionFactory sessionFactory;

    @Override
    public Uni<Product> create(Product product) {
        LOG.debug("Persisting new product");
        return sessionFactory.withTransaction(session -> session.persist(product).replaceWith(product))
            .onItem().invoke(o -> LOG.debugf("Product persisted: productId=%d", o.getId()))
            .onFailure().invoke(e -> {
                LOG.errorf("Failed to create product: %s", e.getMessage());
                throw new RuntimeException("Failed to create product: " + e.getMessage(), e);
            });
    }

    @Override
    public Uni<List<Product>> readAll() {
        LOG.debug("Fetching all products from DB");
        return sessionFactory.withSession(session -> session.createQuery("FROM Product", Product.class).getResultList())
            .onItem().invoke(list -> LOG.debugf("Fetched %d products from DB", list.size()))
            .onFailure().invoke(e -> {
                LOG.errorf("Failed to retrieve products: %s", e.getMessage());
                throw new RuntimeException("Failed to retrieve products: " + e.getMessage(), e);
            });
    }

    @Override
    public Uni<List<Product>> readByIds(List<Integer> ids) {
        LOG.debugf("Fetching products by IDs from DB: %s", ids);
        return sessionFactory.withSession(session -> session.createQuery("FROM Product WHERE id IN :ids", Product.class)
            .setParameter("ids", ids).getResultList())
            .onItem().invoke(list -> LOG.debugf("Fetched %d products by IDs from DB", list.size()))
            .onFailure().invoke(e -> {
                LOG.errorf("Failed to retrieve products by IDs: %s", e.getMessage());
                throw new RuntimeException("Failed to retrieve products by IDs: " + e.getMessage(), e);
            });
    }

    @Override
    public Uni<Product> read(int id) {
        LOG.debugf("Fetching product from DB: productId=%d", id);
        return sessionFactory.withSession(session -> session.find(Product.class, id))
            .onItem().invoke(o -> {
                if (o != null) LOG.debugf("Product fetched: productId=%d", o.getId());
            })
            .onItem().ifNull().failWith(() -> new ProductNotFoundException(id));
    }

    @Override
    public Uni<Product> update(Product product) {
        LOG.debugf("Updating product: productId=%d", product.getId());
        return sessionFactory.withTransaction(session -> 
            session.find(Product.class, product.getId())
                .onItem().ifNull().failWith(() -> new ProductNotFoundException(product.getId()))
                .onItem().ifNotNull().transformToUni(found -> session.merge(product))
        ).onItem().invoke(o -> LOG.debugf("Product updated: productId=%d", o.getId()));
    }

    @Override
    public Uni<Void> delete(int id) {
        LOG.debugf("Deleting product from DB: productId=%d", id);
        return sessionFactory.withTransaction(session -> session.find(Product.class, id)
            .onItem().ifNull().failWith(() -> new ProductNotFoundException(id))
            .onItem().ifNotNull().invoke(session::remove)
            .replaceWithVoid())
            .invoke(() -> LOG.debugf("Product deleted from DB: productId=%d", id));
    }
}