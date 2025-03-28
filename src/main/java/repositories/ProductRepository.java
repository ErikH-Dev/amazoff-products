package repositories;

import java.util.List;

import entities.Product;
import interfaces.IProductRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@ApplicationScoped
public class ProductRepository implements IProductRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Product create(Product product) {
        entityManager.persist(product);
        entityManager.flush();
        entityManager.refresh(product);
        return product;
    }

    @Override
    public List<Product> readAll() {
        TypedQuery<Product> query = entityManager.createQuery("SELECT p FROM Product p", Product.class);
        return query.getResultList();
    }

    @Override
    public Product read(int id) {
        Product product = entityManager.find(Product.class, id);
        if (product == null) {
            throw new EntityNotFoundException("Product not found with id: " + id);
        }
        return product;
    }

    @Override
    public Product update(Product product) {
        Product existingProduct = entityManager.find(Product.class, product.getId());
        if (existingProduct == null) {
            throw new EntityNotFoundException("Product not found with id: " + product.getId());
        }

        if (!existingProduct.getClass().equals(product.getClass())) {
            throw new IllegalArgumentException(
                    "Product type mismatch: cannot update " + existingProduct.getClass().getSimpleName() +
                            " with " + product.getClass().getSimpleName());
        }

        return entityManager.merge(product);
    }

    @Override
    public void delete(int id) {
        Product product = entityManager.find(Product.class, id);
        if (product == null) {
            throw new EntityNotFoundException("Product not found with id: " + id);
        }
        entityManager.remove(product);
    }

}