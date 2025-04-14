package services;

import entities.Product;
import interfaces.IProductRepository;
import interfaces.IProductService;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class ProductService implements IProductService {
    private IProductRepository productRepository;

    public ProductService(IProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    @Transactional
    @Override
    public Uni<Product> create(int oauthId, Product product) {

    }

    @Override
    public Uni<List<Product>> readAll() {
        return productRepository.readAll();
    }

    @Override
    public Uni<Product> read(int id) {
        return productRepository.read(id);
    }

    @Transactional
    @Override
    public Uni<Product> update(int oauthId, Product product) {

    }
    @Transactional
    @Override
    public Uni<Void> delete(int id) {
        return productRepository.delete(id);
    }
}