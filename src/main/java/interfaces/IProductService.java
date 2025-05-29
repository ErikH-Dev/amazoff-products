package interfaces;

import dto.ProductResponse;
import io.smallrye.mutiny.Uni;
import java.util.List;

import dto.CreateProductRequest;
import dto.ReserveStockItem;
import dto.UpdateProductRequest;

public interface IProductService {
    Uni<ProductResponse> create(CreateProductRequest productRequest);
    Uni<List<ProductResponse>> readAll();
    Uni<List<ProductResponse>> readByIds(List<String> ids);
    Uni<ProductResponse> read(String id);
    Uni<List<ProductResponse>> searchProducts(String query);
    Uni<ProductResponse> update(UpdateProductRequest productRequest);
    Uni<Void> reserveProductStocks(List<ReserveStockItem> items);
    Uni<Void> delete(String id);
}