package services;

import entities.Product;
import exceptions.errors.InsufficientStockException;
import exceptions.errors.ProductNotFoundException;
import exceptions.errors.VendorNotFoundException;
import interfaces.IProductRepository;
import interfaces.IProductService;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;
import org.jboss.logging.MDC;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dto.CreateProductRequest;
import dto.ReserveStockItem;
import dto.UpdateProductRequest;
import dto.VendorDTO;

@ApplicationScoped
public class ProductService implements IProductService {
    private static final Logger LOG = Logger.getLogger(ProductService.class);
    private IProductRepository productRepository;
    private VendorClientService vendorClientService;

    public ProductService(IProductRepository productRepository, VendorClientService vendorClientService) {
        this.productRepository = productRepository;
        this.vendorClientService = vendorClientService;
    }

    @Override
    public Uni<Product> create(CreateProductRequest productRequest) {
        LOG.infof("Creating product: name=%s, oauth_id=%d", productRequest.name, productRequest.oauth_id);
        return vendorClientService.getVendorByOauthId(productRequest.oauth_id)
                .onItem().ifNull().failWith(new VendorNotFoundException(productRequest.oauth_id))
                .onItem().transform(vendor -> new Product(productRequest.name, productRequest.oauth_id,
                        productRequest.price, productRequest.description, productRequest.stock))
                .onItem().transformToUni(product -> productRepository.create(product)
                        .invoke(persisted -> {
                            MDC.put("productId", persisted.getId());
                            LOG.infof("Product persisted: productId=%d", persisted.getId());
                            MDC.remove("productId");
                        }))
                .onFailure().invoke(e -> LOG.errorf("Failed to create product: %s", e.getMessage()));
    }

    @Override
    public Uni<List<Product>> readAll() {
        LOG.info("Reading all products");
        return productRepository.readAll()
                .onItem().invoke(products -> LOG.infof("Read %d products", products.size()))
                .onFailure().invoke(e -> LOG.errorf("Failed to read all products: %s", e.getMessage()))
                .onItem().transformToUni(products -> {
                    if (products.isEmpty()) {
                        return Uni.createFrom().item(products);
                    }

                    // Get unique oauth IDs to avoid duplicate vendor requests
                    List<Integer> uniqueOauthIds = products.stream()
                            .map(Product::getOauthId)
                            .distinct()
                            .toList();

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
                            .toList();

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

                                // Set vendor information for each product
                                products.forEach(product -> {
                                    VendorDTO vendor = vendorMap.get(product.getOauthId());
                                    if (vendor != null) {
                                        product.setVendorDTO(vendor);
                                        LOG.debugf("Set vendor info for product %d", product.getId());
                                    } else {
                                        LOG.warnf("No vendor info available for product %d (oauthId=%d)",
                                                product.getId(), product.getOauthId());
                                    }
                                });

                                return products;
                            });
                });
    }

    @Override
    public Uni<List<Product>> readByIds(List<Integer> ids) {
        LOG.infof("Reading products by ids: %s", ids);
        return productRepository.readByIds(ids)
                .onItem().invoke(products -> LOG.infof("Read %d products by ids", products.size()))
                .onFailure().invoke(e -> LOG.errorf("Failed to read products by ids: %s", e.getMessage()))
                .onItem().transformToUni(products -> {
                    if (products.isEmpty()) {
                        return Uni.createFrom().item(products);
                    }

                    // Get unique oauth IDs to avoid duplicate vendor requests
                    List<Integer> uniqueOauthIds = products.stream()
                            .map(Product::getOauthId)
                            .distinct()
                            .toList();

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
                            .toList();

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

                                // Set vendor information for each product
                                products.forEach(product -> {
                                    VendorDTO vendor = vendorMap.get(product.getOauthId());
                                    if (vendor != null) {
                                        product.setVendorDTO(vendor);
                                        LOG.debugf("Set vendor info for product %d", product.getId());
                                    } else {
                                        LOG.warnf("No vendor info available for product %d (oauthId=%d)",
                                                product.getId(), product.getOauthId());
                                    }
                                });

                                return products;
                            });
                });
    }

    @Override
    public Uni<Product> read(int id) {
        MDC.put("productId", id);
        LOG.infof("Reading product: productId=%d", id);
        return productRepository.read(id)
                .onItem().invoke(product -> LOG.infof("Product read: productId=%d", product.getId()))
                .onFailure().invoke(e -> LOG.errorf("Failed to read product: %s", e.getMessage()))
                .eventually(() -> {
                    MDC.remove("productId");
                    return Uni.createFrom().voidItem();
                })
                .onItem().transformToUni(product -> vendorClientService.getVendorByOauthId(product.getOauthId())
                        .invoke(product::setVendorDTO)
                        .replaceWith(product));
    }

    @Override
    public Uni<Product> update(UpdateProductRequest productRequest) {
        MDC.put("productId", productRequest.id);
        LOG.infof("Updating product: productId=%d", productRequest.id);
        // Only check if the product exists, do not check oauthId/vendor
        return productRepository.read(productRequest.id)
                .onItem().ifNull().failWith(new ProductNotFoundException(productRequest.id))
                .onItem().transform(existing -> new Product(
                        productRequest.id,
                        productRequest.name,
                        existing.getOauthId(),
                        productRequest.price,
                        productRequest.description,
                        productRequest.stock))
                .onItem().transformToUni(product -> productRepository.update(product)
                        .invoke(updated -> LOG.infof("Product updated: productId=%d", updated.getId())))
                .onFailure().invoke(e -> LOG.errorf("Failed to update product: %s", e.getMessage()))
                .eventually(() -> {
                    MDC.remove("productId");
                    return Uni.createFrom().voidItem();
                });
    }

    @Override
    public Uni<Void> reserveProductStocks(List<ReserveStockItem> items) {
        LOG.infof("Reserving stock for items: %s", items);
        List<Integer> ids = items.stream().map(i -> i.productId).toList();
        return productRepository.readByIds(ids)
                .onItem().ifNull().failWith(new ProductNotFoundException(-1))
                .onItem().transformToUni(products -> {
                    Map<Integer, Product> productMap = new HashMap<>();
                    for (Product p : products)
                        productMap.put(p.getId(), p);

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
                    List<Uni<Product>> updates = items.stream()
                            .map(item -> {
                                Product p = productMap.get(item.productId);
                                Product updated = new Product(
                                        p.getId(), p.getName(), p.getOauthId(), p.getPrice(), p.getDescription(),
                                        p.getStock() - item.quantity);
                                return productRepository.update(updated);
                            })
                            .toList();
                    return Uni.combine().all().unis(updates).discardItems();
                });
    }

    @Override
    public Uni<Void> delete(int id) {
        MDC.put("productId", id);
        LOG.infof("Deleting product: productId=%d", id);
        return productRepository.delete(id)
                .invoke(() -> LOG.infof("Product deleted: productId=%d", id))
                .onFailure().invoke(e -> LOG.errorf("Failed to delete product: %s", e.getMessage()))
                .eventually(() -> {
                    MDC.remove("productId");
                    return Uni.createFrom().voidItem();
                });
    }
}