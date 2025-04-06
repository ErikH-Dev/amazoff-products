package interfaces;

import entities.Product;
import io.smallrye.mutiny.Uni;
import java.util.List;

public interface IProductService {
    Uni<Product> create(int oauthId, Product product);
    Uni<List<Product>> readAll();
    Uni<Product> read(int id);
    Uni<Product> update(int oauthId, Product product);
    Uni<Void> delete(int id);
}