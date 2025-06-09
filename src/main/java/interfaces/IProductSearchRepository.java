package interfaces;
import java.util.List;

import entities.ProductDocument;
import io.smallrye.mutiny.Uni;

public interface IProductSearchRepository {
    Uni<Void> indexProduct(ProductDocument product);
    Uni<List<ProductDocument>> searchProducts(String query);
    Uni<Void> deleteProduct(String productId);
}