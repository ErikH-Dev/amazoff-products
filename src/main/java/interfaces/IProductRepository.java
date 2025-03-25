package interfaces;

import java.util.List;

import entities.Product;

public interface IProductRepository {

    Product create(Product product);
    List<Product> readAll();
    Product read(int id);

}
