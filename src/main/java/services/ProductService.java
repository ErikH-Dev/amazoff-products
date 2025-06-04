package services;

import entities.Product;
import entities.ProductDocument;
import dto.ProductResponse;
import exceptions.errors.InsufficientStockException;
import exceptions.errors.ProductNotFoundException;
import exceptions.errors.VendorNotFoundException;
import interfaces.IProductRepository;
import interfaces.IProductSearchRepository;
import interfaces.IProductService;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;
import org.jboss.logging.MDC;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import dto.CreateProductRequest;
import dto.ReserveStockItem;
import dto.UpdateProductRequest;
import dto.VendorDTO;

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
    public Uni<ProductResponse> create(CreateProductRequest productRequest) {
        return vendorClientService.getVendorByOauthId(productRequest.oauth_id)
                .onItem().ifNull().failWith(new VendorNotFoundException(productRequest.oauth_id))
                .onItem().transformToUni(vendor -> {
                    Product product = new Product(productRequest.name, productRequest.oauth_id,
                            productRequest.price, productRequest.description, productRequest.stock);

                    return productRepository.create(product)
                            .onItem().invoke(persisted -> {
                                eventPublisher.publishProductCreated(persisted);
                            })
                            .onItem().transform(persisted -> ProductResponse.from(persisted, vendor));
                });
    }

    @Override
    public Uni<List<ProductResponse>> readAll() {
        LOG.info("Reading all products");
        return productRepository.readAll()
                .onItem().invoke(products -> LOG.infof("Read %d products", products.size()))
                .onFailure().invoke(e -> LOG.errorf("Failed to read all products: %s", e.getMessage()))
                .onItem().transformToUni(products -> {
                    if (products.isEmpty()) {
                        return Uni.createFrom().item(List.of());
                    }

                    // Get unique oauth IDs to avoid duplicate vendor requests
                    List<Integer> uniqueOauthIds = products.stream()
                            .map(Product::getOauthId)
                            .distinct()
                            .collect(Collectors.toList());

                    LOG.infof("Found %d unique vendors for %d products", uniqueOauthIds.size(), products.size());

                    // Fetch all vendors concurrently
                    List<Uni<VendorDTO>> vendorUnis = uniqueOauthIds.stream()
                            .map(oauthId -> vendorClientService.getVendorByOauthId(oauthId)
                                    .onFailure().recoverWithItem(() -> {
                                        LOG.warnf(
                                                "Failed to fetch vendor for oauthId=%d, continuing without vendor info",
                                                oauthId);
                                        return null;
                                    }))
                            .collect(Collectors.toList());

                    return Uni.combine().all().unis(vendorUnis)
                            .combinedWith(vendorList -> {
                                // Create a map of oauthId -> VendorDTO for quick lookup
                                Map<Integer, VendorDTO> vendorMap = new HashMap<>();
                                for (int i = 0; i < uniqueOauthIds.size(); i++) {
                                    VendorDTO vendor = (VendorDTO) vendorList.get(i);
                                    if (vendor != null) {
                                        vendorMap.put(uniqueOauthIds.get(i), vendor);
                                    }
                                }

                                // Create ProductResponse objects with vendor information
                                return products.stream()
                                        .map(product -> {
                                            VendorDTO vendor = vendorMap.get(product.getOauthId());
                                            if (vendor != null) {
                                                LOG.debugf("Set vendor info for product %s", product.getProductId());
                                            } else {
                                                LOG.warnf("No vendor info available for product %s (oauthId=%d)",
                                                        product.getProductId(), product.getOauthId());
                                            }
                                            return ProductResponse.from(product, vendor);
                                        })
                                        .collect(Collectors.toList());
                            });
                });
    }

    @Override
    public Uni<List<ProductResponse>> readByIds(List<String> ids) { // Changed from List<Integer> to List<String>
        LOG.infof("Reading products by ids: %s", ids);
        return productRepository.readByIds(ids)
                .onItem().invoke(products -> LOG.infof("Read %d products by ids", products.size()))
                .onFailure().invoke(e -> LOG.errorf("Failed to read products by ids: %s", e.getMessage()))
                .onItem().transformToUni(products -> {
                    if (products.isEmpty()) {
                        return Uni.createFrom().item(List.of());
                    }

                    // Get unique oauth IDs to avoid duplicate vendor requests
                    List<Integer> uniqueOauthIds = products.stream()
                            .map(Product::getOauthId)
                            .distinct()
                            .collect(Collectors.toList());

                    LOG.infof("Found %d unique vendors for %d products", uniqueOauthIds.size(), products.size());

                    // Fetch all vendors concurrently
                    List<Uni<VendorDTO>> vendorUnis = uniqueOauthIds.stream()
                            .map(oauthId -> vendorClientService.getVendorByOauthId(oauthId)
                                    .onFailure().recoverWithItem(() -> {
                                        LOG.warnf(
                                                "Failed to fetch vendor for oauthId=%d, continuing without vendor info",
                                                oauthId);
                                        return null;
                                    }))
                            .collect(Collectors.toList());

                    return Uni.combine().all().unis(vendorUnis)
                            .combinedWith(vendorList -> {
                                // Create a map of oauthId -> VendorDTO for quick lookup
                                Map<Integer, VendorDTO> vendorMap = new HashMap<>();
                                for (int i = 0; i < uniqueOauthIds.size(); i++) {
                                    VendorDTO vendor = (VendorDTO) vendorList.get(i);
                                    if (vendor != null) {
                                        vendorMap.put(uniqueOauthIds.get(i), vendor);
                                    }
                                }

                                // Create ProductResponse objects with vendor information
                                return products.stream()
                                        .map(product -> {
                                            VendorDTO vendor = vendorMap.get(product.getOauthId());
                                            if (vendor != null) {
                                                LOG.debugf("Set vendor info for product %s", product.getProductId());
                                            } else {
                                                LOG.warnf("No vendor info available for product %s (oauthId=%d)",
                                                        product.getProductId(), product.getOauthId());
                                            }
                                            return ProductResponse.from(product, vendor);
                                        })
                                        .collect(Collectors.toList());
                            });
                });
    }

    @Override
    public Uni<ProductResponse> read(String id) { // Changed from int to String
        MDC.put("productId", id);
        LOG.infof("Reading product: productId=%s", id);
        return productRepository.read(id)
                .onItem().invoke(product -> LOG.infof("Product read: productId=%s", product.getProductId()))
                .onFailure().invoke(e -> LOG.errorf("Failed to read product: %s", e.getMessage()))
                .eventually(() -> {
                    MDC.remove("productId");
                    return Uni.createFrom().voidItem();
                })
                .onItem().transformToUni(product -> vendorClientService.getVendorByOauthId(product.getOauthId())
                        .onItem().transform(vendor -> ProductResponse.from(product, vendor)));
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

                    // Get vendor information (same logic as readAll)
                    return enrichWithVendorInfo(products);
                });
    }

    @Override
    public Uni<ProductResponse> update(UpdateProductRequest productRequest) {
        MDC.put("productId", productRequest.id);
        LOG.infof("Updating product: productId=%s", productRequest.id);
        return productRepository.read(productRequest.id)
                .onItem().ifNull().failWith(new ProductNotFoundException(productRequest.id))
                .onItem().transformToUni(existing -> {
                    existing.name = productRequest.name;
                    existing.price = productRequest.price;
                    existing.description = productRequest.description;
                    existing.stock = productRequest.stock;

                    return productRepository.update(existing)
                            .onItem().invoke(updated -> {
                                eventPublisher.publishProductUpdated(updated);
                            })
                            .onItem().transformToUni(updated -> vendorClientService.getVendorByOauthId(updated.getOauthId())
                                    .onItem().transform(vendor -> ProductResponse.from(updated, vendor)));
                })
                .onFailure().invoke(e -> LOG.errorf("Failed to update product: %s", e.getMessage()))
                .eventually(() -> {
                    MDC.remove("productId");
                    return Uni.createFrom().voidItem();
                });
    }

    @Override
    public Uni<Void> reserveProductStocks(List<ReserveStockItem> items) {
        throw new UnsupportedOperationException("ReserveProductStocks is not implemented yet");
    }

    @Override
    public Uni<Void> delete(String id) {
        MDC.put("productId", id);
        LOG.infof("Deleting product: productId=%s", id);
        return productRepository.delete(id)
                .invoke(() -> {
                    eventPublisher.publishProductDeleted(id);
                    LOG.infof("Product deleted: productId=%s", id);
                })
                .onFailure().invoke(e -> LOG.errorf("Failed to delete product: %s", e.getMessage()))
                .eventually(() -> {
                    MDC.remove("productId");
                    return Uni.createFrom().voidItem();
                });
    }

    private Uni<List<ProductResponse>> enrichWithVendorInfo(List<Product> products) {
        if (products.isEmpty()) {
            return Uni.createFrom().item(List.of());
        }

        // Get unique oauth IDs to avoid duplicate vendor requests
        List<Integer> uniqueOauthIds = products.stream()
                .map(Product::getOauthId)
                .distinct()
                .collect(Collectors.toList());

        LOG.infof("Found %d unique vendors for %d products", uniqueOauthIds.size(), products.size());

        // Fetch all vendors concurrently
        List<Uni<VendorDTO>> vendorUnis = uniqueOauthIds.stream()
                .map(oauthId -> vendorClientService.getVendorByOauthId(oauthId)
                        .onFailure().recoverWithItem(() -> {
                            LOG.warnf("Failed to fetch vendor for oauthId=%d, continuing without vendor info", oauthId);
                            return null;
                        }))
                .collect(Collectors.toList());

        return Uni.combine().all().unis(vendorUnis)
                .combinedWith(vendorList -> {
                    // Create a map of oauthId -> VendorDTO for quick lookup
                    Map<Integer, VendorDTO> vendorMap = new HashMap<>();
                    for (int i = 0; i < uniqueOauthIds.size(); i++) {
                        VendorDTO vendor = (VendorDTO) vendorList.get(i);
                        if (vendor != null) {
                            vendorMap.put(uniqueOauthIds.get(i), vendor);
                        }
                    }

                    // Create ProductResponse objects with vendor information
                    return products.stream()
                            .map(product -> {
                                VendorDTO vendor = vendorMap.get(product.getOauthId());
                                if (vendor != null) {
                                    LOG.debugf("Set vendor info for product %s", product.getProductId());
                                } else {
                                    LOG.warnf("No vendor info available for product %s (oauthId=%d)",
                                            product.getProductId(), product.getOauthId());
                                }
                                return ProductResponse.from(product, vendor);
                            })
                            .collect(Collectors.toList());
                });
    }
}