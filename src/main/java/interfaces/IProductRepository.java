package interfaces;

import java.util.List;

import entities.Product;
import io.smallrye.mutiny.Uni;

public interface IProductRepository {

    Uni<Product> create(Product product);
    Uni<List<Product>> readAll();
    Uni<Product> read(int id);
    Uni<Product> update(Product product);
    Uni<Void> delete(int id);

}