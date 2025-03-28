package interfaces;

import java.util.List;

import entities.Product;

public interface IProductRepository {

    Product create(Product product);
    List<Product> readAll();
    Product read(int id);
    Product update(Product product);
    void delete(int id);

}
