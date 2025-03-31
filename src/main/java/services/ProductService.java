package services;

import entities.Product;
import interfaces.IProductRepository;
import interfaces.IProductService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class ProductService implements IProductService {
    private IProductRepository productRepository;

    public ProductService(IProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public Product create(Product product) {
        return productRepository.create(product);
    }

    @Override
    public List<Product> readAll() {
        return productRepository.readAll();
    }

    @Override
    public Product read(int id) {
        return productRepository.read(id);
    }

    @Override
    @Transactional
    public Product update(Product product) {
        return productRepository.update(product);
    }

    @Override
    @Transactional
    public void delete(int id) {
        productRepository.delete(id);
    }
}