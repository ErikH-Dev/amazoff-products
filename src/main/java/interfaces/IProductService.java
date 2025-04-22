package interfaces;

import entities.Product;
import io.smallrye.mutiny.Uni;
import java.util.List;

import dto.CreateProductRequest;
import dto.UpdateProductRequest;

public interface IProductService {
    Uni<Product> create(CreateProductRequest productRequest);
    Uni<List<Product>> readAll();
    Uni<List<Product>> readByIds(List<Integer> ids);
    Uni<Product> read(int id);
    Uni<Product> update(UpdateProductRequest productRequest);
    Uni<Void> delete(int id);
}