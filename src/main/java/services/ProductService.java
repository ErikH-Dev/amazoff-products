package services;

import entities.Product;
import exceptions.errors.VendorNotFoundException;
import interfaces.IProductRepository;
import interfaces.IProductService;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import clients.IVendorServiceClient;
import dto.CreateProductRequest;
import dto.UpdateProductRequest;

@ApplicationScoped
public class ProductService implements IProductService {
    private IProductRepository productRepository;
    private @RestClient IVendorServiceClient vendorServiceClient;

    public ProductService(IProductRepository productRepository, @RestClient IVendorServiceClient vendorServiceClient) {
        this.productRepository = productRepository;
        this.vendorServiceClient = vendorServiceClient;
    }

    @Override
    public Uni<Product> create(CreateProductRequest productRequest) {
        return vendorServiceClient.getVendorById(productRequest.oauth_id)
                .onItem().ifNull().failWith(new VendorNotFoundException(productRequest.oauth_id))
                .onItem().transform(vendor -> new Product(productRequest.name, productRequest.oauth_id,
                        productRequest.price, productRequest.description))
                .onItem().transformToUni(product -> productRepository.create(product));
    }

    @Override
    public Uni<List<Product>> readAll() {
        return productRepository.readAll()
            .onItem().transformToUni(products ->
                Uni.combine().all().unis(
                    products.stream()
                        .map(product -> vendorServiceClient.getVendorById(product.getOauthId())
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
                        .map(product -> vendorServiceClient.getVendorById(product.getOauthId())
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
                vendorServiceClient.getVendorById(product.getOauthId())
                    .invoke(product::setVendorDTO)
                    .replaceWith(product)
            );
    }

    @Override
    public Uni<Product> update(UpdateProductRequest productRequest) {
        return vendorServiceClient.getVendorById(productRequest.oauth_id)
                .onItem().ifNull().failWith(new VendorNotFoundException(productRequest.oauth_id))
                .onItem().transform(vendor -> new Product(productRequest.id, productRequest.name,
                        productRequest.price, productRequest.description))
                .onItem().transformToUni(product -> productRepository.update(product));
    }

    @Override
    public Uni<Void> delete(int id) {
        return productRepository.delete(id);
    }
}