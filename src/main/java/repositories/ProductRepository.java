package repositories;

import java.util.List;

import entities.Product;
import interfaces.IProductRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ProductRepository implements IProductRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public Product create(Product product) {
        entityManager.persist(product);
        entityManager.flush();
        entityManager.refresh(product);
        return product;
    }

    @Override
    @Transactional
    public List<Product> readAll() {
        TypedQuery<Product> query = entityManager.createQuery("SELECT p FROM Product p", Product.class);
        return query.getResultList();
    }

    @Override
    @Transactional
    public Product read(int id) {
        Product product = entityManager.find(Product.class, id);
        if (product == null) {
            throw new EntityNotFoundException("Product not found with id: " + id);
        }
        return product;
    }
}