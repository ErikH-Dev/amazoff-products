package services;

import entities.Product;
import entities.Vendor;
import interfaces.IProductRepository;
import interfaces.IProductService;
import interfaces.IVendorRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;

@ApplicationScoped
public class ProductService implements IProductService {
    private IProductRepository productRepository;
    private IVendorRepository vendorRepository;

    public ProductService(IProductRepository productRepository, IVendorRepository vendorRepository) {
        this.productRepository = productRepository;
        this.vendorRepository = vendorRepository;
    }

    @Override
    public Uni<Product> create(int oauthId, Product product) {
        return vendorRepository.read(oauthId)
            .onItem().ifNull().failWith(() -> new EntityNotFoundException("Vendor not found with id: " + oauthId))
            .flatMap(vendor -> {
                product.setVendor(vendor);
                return productRepository.create(product);
            });
    }

    @Override
    public Uni<List<Product>> readAll() {
        return productRepository.readAll();
    }

    @Override
    public Uni<Product> read(int id) {
        return productRepository.read(id);
    }

    @Override
    public Uni<Product> update(int oauthId, Product product) {
        return vendorRepository.read(oauthId)
            .onItem().ifNull().failWith(() -> new EntityNotFoundException("Vendor not found with id: " + oauthId))
            .flatMap(vendor -> productRepository.read(product.getId())
                .onItem().ifNull().failWith(() -> new EntityNotFoundException("Product not found with id: " + product.getId()))
                .flatMap(existingProduct -> {
                    product.setVendor(vendor);
                    return productRepository.update(product);
                }));
    }

    @Override
    public Uni<Void> delete(int id) {
        return productRepository.delete(id);
    }
}