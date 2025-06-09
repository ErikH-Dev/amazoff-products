package interfaces;

import java.util.List;

import entities.Product;
import io.smallrye.mutiny.Uni;

public interface IProductRepository {

    Uni<Product> create(Product product);
    Uni<List<Product>> readAll();
    Uni<List<Product>> readByIds(List<String> ids);
    Uni<Product> read(String id);
    Uni<Product> update(Product product);
    Uni<Void> delete(String id);

}