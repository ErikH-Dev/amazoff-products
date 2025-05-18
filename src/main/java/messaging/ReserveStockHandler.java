package messaging;

import java.util.List;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import dto.ReserveStockItem;
import dto.ReserveStockRequest;
import dto.StockReservationFailed;
import dto.StockReserved;
import exceptions.errors.InsufficientStockException;
import exceptions.errors.ProductNotFoundException;
import interfaces.IProductService;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ReserveStockHandler {

    IProductService productService;

    public ReserveStockHandler(IProductService productService) {
        this.productService = productService;
    }

    @Incoming("reserve-stock-requests")
    @Outgoing("reserve-stock-responses")
    public Uni<Message<JsonObject>> handleReserveStock(Message<JsonObject> request) {
        ReserveStockRequest reserveRequest = request.getPayload().mapTo(ReserveStockRequest.class);
        List<ReserveStockItem> items = reserveRequest.items;

        return productService.reserveProductStocks(items)
            .map(v -> {
                StockReserved reserved = new StockReserved(items);
                JsonObject response = JsonObject.mapFrom(reserved);
                return Message.of(response);
            })
            .onFailure(ProductNotFoundException.class).recoverWithItem(e -> {
                StockReservationFailed failed = new StockReservationFailed(items, "Product not found");
                JsonObject response = JsonObject.mapFrom(failed);
                return Message.of(response);
            })
            .onFailure(InsufficientStockException.class).recoverWithItem(e -> {
                StockReservationFailed failed = new StockReservationFailed(items, "Insufficient stock");
                JsonObject response = JsonObject.mapFrom(failed);
                return Message.of(response);
            });
    }
}