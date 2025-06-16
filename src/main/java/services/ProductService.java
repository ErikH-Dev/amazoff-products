package services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jboss.logging.Logger;
import org.jboss.logging.MDC;

import dto.ProductResponse;
import dto.ReserveStockItem;
import dto.VendorDTO;
import entities.Product;
import entities.ProductDocument;
import exceptions.errors.InsufficientStockException;
import exceptions.errors.ProductNotFoundException;
import exceptions.errors.VendorNotFoundException;
import interfaces.IProductRepository;
import interfaces.IProductSearchRepository;
import interfaces.IProductService;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Valid;

@ApplicationScoped
public class ProductService implements IProductService {
    private static final Logger LOG = Logger.getLogger(ProductService.class);
    private IProductRepository productRepository;
    private VendorClientService vendorClientService;
    private IProductSearchRepository productSearchRepository;
    private ProductEventPublisher eventPublisher;

    public ProductService(IProductRepository productRepository, IProductSearchRepository productSearchRepository,
            VendorClientService vendorClientService, ProductEventPublisher eventPublisher) {
        this.productRepository = productRepository;
        this.productSearchRepository = productSearchRepository;
        this.vendorClientService = vendorClientService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Uni<ProductResponse> create(@Valid Product product) {
        LOG.infof("Creating product: name=%s, keycloakId=%s", product.name, product.keycloakId);
        return vendorClientService.getVendorByKeycloakId(product.keycloakId)
                .onItem().ifNull().failWith(new VendorNotFoundException(product.keycloakId))
                .onItem().transformToUni(vendor -> {
                    return productRepository.create(product)
                            .onItem().invoke(persisted -> {
                                eventPublisher.publishProductCreated(persisted);
                                LOG.infof("Product created: productId=%s", persisted.getProductId());
                            })
                            .onItem().transform(persisted -> new ProductResponse(persisted, vendor));
                });
    }

    @Override
    public Uni<List<ProductResponse>> readAll() {
        LOG.info("Reading all products");
        return productRepository.readAll()
                .onItem().invoke(products -> LOG.infof("Read %d products", products.size()))
                .onFailure().invoke(e -> LOG.errorf("Failed to read all products: %s", e.getMessage()))
                .onItem().transformToUni(this::enrichWithVendorInfo);
    }

    @Override
    public Uni<List<ProductResponse>> readByIds(List<String> ids) {
        LOG.infof("Reading products by ids: %s", ids);
        return productRepository.readByIds(ids)
                .onItem().invoke(products -> LOG.infof("Read %d products by ids", products.size()))
                .onFailure().invoke(e -> LOG.errorf("Failed to read products by ids: %s", e.getMessage()))
                .onItem().transformToUni(this::enrichWithVendorInfo);
    }

    @Override
    public Uni<ProductResponse> read(String id) {
        MDC.put("productId", id);
        LOG.infof("Reading product: productId=%s", id);
        return productRepository.read(id)
                .onItem().ifNull().failWith(new ProductNotFoundException(id))
                .onItem().invoke(product -> LOG.infof("Product read: productId=%s", product.getProductId()))
                .onFailure().invoke(e -> LOG.errorf("Failed to read product: %s", e.getMessage()))
                .onItem().transformToUni(product -> vendorClientService.getVendorByKeycloakId(product.getKeycloakId())
                        .onItem().transform(vendor -> new ProductResponse(product, vendor)))
                .eventually(() -> {
                    MDC.remove("productId");
                    return Uni.createFrom().voidItem();
                });
    }

    @Override
    public Uni<List<ProductResponse>> searchProducts(String query) {
        LOG.infof("Searching products with query: %s", query);
        return productSearchRepository.searchProducts(query)
                .onItem().transformToUni(productDocs -> {
                    if (productDocs.isEmpty()) {
                        return Uni.createFrom().item(List.of());
                    }

                    // Convert ProductDocument back to Product entities
                    List<Product> products = productDocs.stream()
                            .map(ProductDocument::toProduct)
                            .collect(Collectors.toList());

                    return enrichWithVendorInfo(products);
                });
    }

    @Override
    public Uni<ProductResponse> update(Product product) {
        MDC.put("productId", product.id.toString());
        LOG.infof("Updating product: productId=%s", product.id);
        return productRepository.read(product.id.toString())
                .onItem().ifNull().failWith(new ProductNotFoundException(product.id.toString()))
                .onItem().transformToUni(existing -> {
                    // Update the existing product with new values
                    existing.name = product.name;
                    existing.price = product.price;
                    existing.description = product.description;
                    existing.stock = product.stock;

                    return productRepository.update(existing)
                            .onItem().invoke(updated -> {
                                eventPublisher.publishProductUpdated(updated);
                                LOG.infof("Product updated: productId=%s", updated.getProductId());
                            })
                            .onItem().transformToUni(
                                    updated -> vendorClientService.getVendorByKeycloakId(updated.getKeycloakId())
                                            .onItem().transform(vendor -> new ProductResponse(updated, vendor)));
                })
                .onFailure().invoke(e -> LOG.errorf("Failed to update product: %s", e.getMessage()))
                .eventually(() -> {
                    MDC.remove("productId");
                    return Uni.createFrom().voidItem();
                });
    }

    @Override
    public Uni<Void> delete(String id) {
        MDC.put("productId", id);
        LOG.infof("Deleting product: productId=%s", id);
        return productRepository.delete(id)
                .onItem().invoke(() -> {
                    eventPublisher.publishProductDeleted(id);
                    LOG.infof("Product deleted: productId=%s", id);
                })
                .onFailure().invoke(e -> LOG.errorf("Failed to delete product: %s", e.getMessage()))
                .eventually(() -> {
                    MDC.remove("productId");
                    return Uni.createFrom().voidItem();
                });
    }

    @Override
    public Uni<Void> reserveProductStocks(List<ReserveStockItem> items) {
        LOG.infof("Reserving stock for items: %s", items);
        List<String> ids = items.stream().map(i -> i.productId).toList();
        return productRepository.readByIds(ids)
                .onItem().ifNull().failWith(new ProductNotFoundException("unknown"))
                .onItem().transformToUni(products -> {
                    Map<String, Product> productMap = new HashMap<>();
                    for (Product p : products) {
                        productMap.put(p.getProductId(), p);
                    }

                    // Validate all items before making any changes
                    for (ReserveStockItem item : items) {
                        Product p = productMap.get(item.productId);
                        if (p == null) {
                            return Uni.createFrom().failure(new ProductNotFoundException(item.productId));
                        }
                        if (p.getStock() < item.quantity) {
                            return Uni.createFrom().failure(
                                    new InsufficientStockException(item.productId, item.quantity, p.getStock()));
                        }
                    }

                    // Update stock for all items
                    List<Uni<Product>> updates = items.stream()
                            .map(item -> {
                                Product p = productMap.get(item.productId);
                                Product updated = new Product(
                                        p.getName(),
                                        p.getKeycloakId(),
                                        p.getPrice(),
                                        p.getDescription(),
                                        p.getStock() - item.quantity);
                                updated.id = p.id;
                                return productRepository.update(updated)
                                        .onItem().invoke(updatedProduct -> {
                                            eventPublisher.publishProductUpdated(updatedProduct);
                                        });
                            })
                            .toList();

                    return Uni.combine().all().unis(updates).discardItems();
                });
    }

    @Override
    public Uni<List<ProductResponse>> readByVendorId(String vendorId) {
        LOG.infof("Reading products by vendorId: %s", vendorId);
        return productRepository.readByVendorId(vendorId)
                .onItem().invoke(products -> LOG.infof("Read %d products for vendorId=%s", products.size(), vendorId))
                .onFailure().invoke(e -> LOG.errorf("Failed to read products by vendorId: %s", e.getMessage()))
                .onItem().transformToUni(this::enrichWithVendorInfo)
                .onFailure().recoverWithItem(List.of());
    }

    @Override
    public Uni<Void> releaseProductStocks(List<ReserveStockItem> items) {
        LOG.infof("Releasing stock for items: %s", items);
        List<String> ids = items.stream().map(i -> i.productId).toList();
        return productRepository.readByIds(ids)
                .onItem().ifNull().failWith(new ProductNotFoundException("unknown"))
                .onItem().transformToUni(products -> {
                    Map<String, Product> productMap = new HashMap<>();
                    for (Product p : products) {
                        productMap.put(p.getProductId(), p);
                    }

                    // Validate all products exist
                    for (ReserveStockItem item : items) {
                        Product p = productMap.get(item.productId);
                        if (p == null) {
                            return Uni.createFrom().failure(new ProductNotFoundException(item.productId));
                        }
                    }

                    // Release stock for all items
                    List<Uni<Product>> updates = items.stream()
                            .map(item -> {
                                Product p = productMap.get(item.productId);
                                Product updated = new Product(
                                        p.getName(),
                                        p.getKeycloakId(),
                                        p.getPrice(),
                                        p.getDescription(),
                                        p.getStock() + item.quantity); // Add back the quantity
                                updated.id = p.id;
                            return productRepository.update(updated)
                                .onItem().invoke(updatedProduct -> {
                                    eventPublisher.publishProductUpdated(updatedProduct);
                                });
                        })
                        .toList();

                return Uni.combine().all().unis(updates).discardItems();
                });
    }

    private Uni<List<ProductResponse>> enrichWithVendorInfo(List<Product> products) {
        if (products.isEmpty()) {
            return Uni.createFrom().item(List.of());
        }

        // Get unique keycloak IDs to avoid duplicate vendor requests
        List<String> uniqueKeycloakIds = products.stream()
                .map(Product::getKeycloakId)
                .distinct()
                .collect(Collectors.toList());

        LOG.infof("Found %d unique vendors for %d products", uniqueKeycloakIds.size(), products.size());

        // Fetch all vendors concurrently
        List<Uni<VendorDTO>> vendorUnis = uniqueKeycloakIds.stream()
                .map(keycloakId -> vendorClientService.getVendorByKeycloakId(keycloakId)
                        .onFailure().recoverWithItem(() -> {
                            LOG.warnf("Failed to fetch vendor for keycloakId=%s, continuing without vendor info",
                                    keycloakId);
                            return null;
                        }))
                .collect(Collectors.toList());

        return Uni.combine().all().unis(vendorUnis)
                .combinedWith(vendorList -> {
                    // Create a map of keycloakId -> VendorDTO for quick lookup
                    Map<String, VendorDTO> vendorMap = new HashMap<>();
                    for (int i = 0; i < uniqueKeycloakIds.size(); i++) {
                        VendorDTO vendor = (VendorDTO) vendorList.get(i);
                        if (vendor != null) {
                            vendorMap.put(uniqueKeycloakIds.get(i), vendor);
                        }
                    }

                    // Create ProductResponse objects with vendor information
                    return products.stream()
                            .map(product -> {
                                VendorDTO vendor = vendorMap.get(product.getKeycloakId());
                                if (vendor != null) {
                                    LOG.debugf("Set vendor info for product %s", product.getProductId());
                                } else {
                                    LOG.warnf("No vendor info available for product %s (keycloakId=%s)",
                                            product.getProductId(), product.getKeycloakId());
                                }
                                return new ProductResponse(product, vendor);
                            })
                            .collect(Collectors.toList());
                });
    }
}