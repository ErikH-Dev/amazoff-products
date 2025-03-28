package interfaces;

import entities.Product;
import java.util.List;

public interface IProductService {
    Product create(Product product);
    List<Product> readAll();
    Product read(int id);
    Product update(Product product);
    void delete(int id);
}