package interfaces;

import dto.ProductResponse;
import io.smallrye.mutiny.Uni;
import jakarta.validation.Valid;

import java.util.List;

import dto.ReserveStockItem;
import entities.Product;

public interface IProductService {
    Uni<ProductResponse> create(@Valid Product product);
    Uni<List<ProductResponse>> readAll();
    Uni<List<ProductResponse>> readByIds(List<String> ids);
    Uni<ProductResponse> read(String id);
    Uni<List<ProductResponse>> searchProducts(String query);
    Uni<ProductResponse> update(Product product);
    Uni<Void> reserveProductStocks(List<ReserveStockItem> items);
    Uni<Void> releaseProductStocks(List<ReserveStockItem> items);
    Uni<Void> delete(String id);
}