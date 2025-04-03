package interfaces;

import entities.Product;
import java.util.List;

public interface IProductService {
    Product create(int oauthId, Product product);
    List<Product> readAll();
    Product read(int id);
    Product update(int oauthId, Product product);
    void delete(int id);
}