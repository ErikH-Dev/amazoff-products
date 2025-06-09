package repositories;

import java.util.List;

import entities.Product;
import exceptions.errors.ProductNotFoundException;
import interfaces.IProductRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;
import org.bson.types.ObjectId;

@ApplicationScoped
public class ProductRepository implements IProductRepository {

    private static final Logger LOG = Logger.getLogger(ProductRepository.class);

    @Override
    public Uni<Product> create(Product product) {
        LOG.debug("Persisting new product to MongoDB");
        return product.persist()
            .onItem().invoke(savedProduct -> LOG.debugf("Product persisted to MongoDB: productId=%s", ((Product) savedProduct).getProductId()))
            .onFailure().invoke(e -> {
                LOG.errorf("Failed to create product in MongoDB: %s", e.getMessage());
                throw new RuntimeException("Failed to create product: " + e.getMessage(), e);
            })
            .map(savedProduct -> (Product) savedProduct);
    }

    @Override
    public Uni<List<Product>> readAll() {
        LOG.debug("Fetching all products from MongoDB");
        return Product.<Product>listAll()
            .onItem().invoke(list -> LOG.debugf("Fetched %d products from MongoDB", list.size()))
            .onFailure().invoke(e -> {
                LOG.errorf("Failed to retrieve products from MongoDB: %s", e.getMessage());
                throw new RuntimeException("Failed to retrieve products: " + e.getMessage(), e);
            });
    }

    @Override
    public Uni<List<Product>> readByIds(List<String> ids) {
        LOG.debugf("Fetching products by IDs from MongoDB: %s", ids);
        List<ObjectId> objectIds = ids.stream()
            .map(ObjectId::new)
            .toList();
        
        return Product.<Product>find("_id in ?1", objectIds).list()
            .onItem().invoke(list -> LOG.debugf("Fetched %d products by IDs from MongoDB", list.size()))
            .onFailure().invoke(e -> {
                LOG.errorf("Failed to retrieve products by IDs from MongoDB: %s", e.getMessage());
                throw new RuntimeException("Failed to retrieve products by IDs: " + e.getMessage(), e);
            });
    }

    @Override
    public Uni<Product> read(String id) {
        LOG.debugf("Fetching product from MongoDB: productId=%s", id);
        return Product.<Product>findById(new ObjectId(id))
            .onItem().invoke(product -> {
                if (product != null) LOG.debugf("Product fetched from MongoDB: productId=%s", product.getProductId());
            })
            .onItem().ifNull().failWith(() -> new ProductNotFoundException(id));
    }

    @Override
    public Uni<Product> update(Product product) {
        LOG.debugf("Updating product in MongoDB: productId=%s", product.getProductId());
        return Product.<Product>findById(product.id)
            .onItem().ifNull().failWith(() -> new ProductNotFoundException(product.getProductId()))
            .onItem().ifNotNull().transformToUni(existingProduct -> {
                existingProduct.name = product.name;
                existingProduct.price = product.price;
                existingProduct.description = product.description;
                existingProduct.stock = product.stock;
                existingProduct.keycloakId = product.keycloakId;

                return existingProduct.update()
                    .map(updated -> existingProduct);
            })
            .onItem().invoke(updatedProduct -> LOG.debugf("Product updated in MongoDB: productId=%s", updatedProduct.getProductId()));
    }

    @Override
    public Uni<Void> delete(String id) {
        LOG.debugf("Deleting product from MongoDB: productId=%s", id);
        return Product.<Product>findById(new ObjectId(id))
            .onItem().ifNull().failWith(() -> new ProductNotFoundException(id))
            .onItem().ifNotNull().transformToUni(product -> product.delete())
            .onItem().invoke(() -> LOG.debugf("Product deleted from MongoDB: productId=%s", id))
            .replaceWithVoid();
    }

    public Uni<Product> findByObjectId(ObjectId id) {
        LOG.debugf("Fetching product by ObjectId from MongoDB: %s", id);
        return Product.<Product>findById(id)
            .onItem().ifNull().failWith(() -> new ProductNotFoundException(id.toString()));
    }

    public Uni<List<Product>> findByVendor(String keycloakId) {
        LOG.debugf("Fetching products by vendor from MongoDB: keycloakId=%s", keycloakId);
        return Product.<Product>find("keycloakId", keycloakId).list();
    }

    public Uni<Long> countByVendor(String keycloakId) {
        return Product.count("keycloakId", keycloakId);
    }
}