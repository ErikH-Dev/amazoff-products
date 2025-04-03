package services;

import entities.Product;
import entities.Vendor;
import interfaces.IProductRepository;
import interfaces.IProductService;
import interfaces.IVendorRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

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
    @Transactional
    public Product create(int oauthId, Product product) {
        Vendor vendor = vendorRepository.read(oauthId);
        if (vendor == null) {
            throw new EntityNotFoundException("Vendor not found with id: " + oauthId);
        }
        product.setVendor(vendor);
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
    public Product update(int oauthId, Product product) {
        Vendor vendor = vendorRepository.read(oauthId);
        if (vendor == null) {
            throw new EntityNotFoundException("Vendor not found with id: " + oauthId);
        }

        Product existingProduct = productRepository.read(product.getId());
        if (existingProduct == null) {
            throw new EntityNotFoundException("Product not found with id: " + product.getId());
        }
        product.setVendor(vendor);
        return productRepository.update(product);
    }

    @Override
    @Transactional
    public void delete(int id) {
        productRepository.delete(id);
    }
}