package services;

import entities.Product;
import exceptions.errors.InsufficientStockException;
import exceptions.errors.ProductNotFoundException;
import exceptions.errors.VendorNotFoundException;
import interfaces.IProductRepository;
import interfaces.IProductService;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dto.CreateProductRequest;
import dto.ReserveStockItem;
import dto.UpdateProductRequest;

@ApplicationScoped
public class ProductService implements IProductService {
    private IProductRepository productRepository;
    private VendorClientService vendorClientService;

    public ProductService(IProductRepository productRepository, VendorClientService vendorClientService) {
        this.productRepository = productRepository;
        this.vendorClientService = vendorClientService;
    }

    @Override
    public Uni<Product> create(CreateProductRequest productRequest) {
        return vendorClientService.getVendorByOauthId(productRequest.oauth_id)
                .onItem().ifNull().failWith(new VendorNotFoundException(productRequest.oauth_id))
                .onItem().transform(vendor -> new Product(productRequest.name, productRequest.oauth_id,
                        productRequest.price, productRequest.description, productRequest.stock))
                .onItem().transformToUni(product -> productRepository.create(product));
    }

    @Override
    public Uni<List<Product>> readAll() {
        return productRepository.readAll()
            .onItem().transformToUni(products ->
                Uni.combine().all().unis(
                    products.stream()
                        .map(product -> vendorClientService.getVendorByOauthId(product.getOauthId())
                            .invoke(product::setVendorDTO)
                            .replaceWith(product))
                        .toList()
                ).combinedWith(list -> (List<Product>) list)
            );
    }
    
    @Override
    public Uni<List<Product>> readByIds(List<Integer> ids) {
        return productRepository.readByIds(ids)
            .onItem().transformToUni(products ->
                Uni.combine().all().unis(
                    products.stream()
                        .map(product -> vendorClientService.getVendorByOauthId(product.getOauthId())
                            .invoke(product::setVendorDTO)
                            .replaceWith(product))
                        .toList()
                ).combinedWith(list -> (List<Product>) list)
            );
    }
    
    @Override
    public Uni<Product> read(int id) {
        return productRepository.read(id)
            .onItem().transformToUni(product ->
                vendorClientService.getVendorByOauthId(product.getOauthId())
                    .invoke(product::setVendorDTO)
                    .replaceWith(product)
            );
    }

    @Override
    public Uni<Product> update(UpdateProductRequest productRequest) {
        return vendorClientService.getVendorByOauthId(productRequest.oauth_id)
                .onItem().ifNull().failWith(new VendorNotFoundException(productRequest.oauth_id))
                .onItem().transform(vendor -> new Product(productRequest.id, productRequest.name,
                        productRequest.price, productRequest.description, productRequest.stock))
                .onItem().transformToUni(product -> productRepository.update(product));
    }

    @Override
    public Uni<Void> reserveProductStocks(List<ReserveStockItem> items) {
        List<Integer> ids = items.stream().map(i -> i.productId).toList();
        return productRepository.readByIds(ids)
            .onItem().ifNull().failWith(new ProductNotFoundException(-1))
            .onItem().transformToUni(products -> {
                Map<Integer, Product> productMap = new HashMap<>();
                for (Product p : products) productMap.put(p.getId(), p);

                for (ReserveStockItem item : items) {
                    Product p = productMap.get(item.productId);
                    if (p == null) {
                        return Uni.createFrom().failure(new ProductNotFoundException(item.productId));
                    }
                    if (p.getStock() < item.quantity) {
                        return Uni.createFrom().failure(new InsufficientStockException(item.productId, item.quantity, p.getStock()));
                    }
                }
                List<Uni<Product>> updates = items.stream()
                    .map(item -> {
                        Product p = productMap.get(item.productId);
                        Product updated = new Product(
                            p.getId(), p.getName(), p.getOauthId(), p.getPrice(), p.getDescription(), p.getStock() - item.quantity
                        );
                        return productRepository.update(updated);
                    })
                    .toList();
                return Uni.combine().all().unis(updates).discardItems();
            });
    }

    @Override
    public Uni<Void> delete(int id) {
        return productRepository.delete(id);
    }
}