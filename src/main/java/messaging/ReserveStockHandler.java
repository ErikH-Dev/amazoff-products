package messaging;

import java.util.List;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import dto.ReserveStockItem;
import dto.ReserveStockRequest;
import dto.StockReservationFailed;
import dto.StockReserved;
import exceptions.errors.ProductNotFoundException;
import exceptions.errors.InsufficientStockException;
import interfaces.IProductService;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

@ApplicationScoped
public class ReserveStockHandler {

    private static final Logger LOG = Logger.getLogger(ReserveStockHandler.class);

    IProductService productService;

    public ReserveStockHandler(IProductService productService) {
        this.productService = productService;
    }

    @Incoming("reserve-stock-requests")
    @Outgoing("reserve-stock-responses")
    public Uni<Message<JsonObject>> handleReserveStock(Message<JsonObject> request) {
        ReserveStockRequest reserveRequest = request.getPayload().mapTo(ReserveStockRequest.class);
        List<ReserveStockItem> items = reserveRequest.items;
        LOG.infof("Handling reserve-stock request for items=%s", items);

        return productService.reserveProductStocks(items)
            .map(v -> {
                LOG.info("Stock reserved successfully");
                StockReserved reserved = new StockReserved(items);
                JsonObject response = JsonObject.mapFrom(reserved).put("status", "StockReserved");
                return Message.of(response);
            })
            .onFailure(InsufficientStockException.class).recoverWithItem(e -> {
                LOG.warnf("Stock reservation failed due to insufficient stock: %s", e.getMessage());
                StockReservationFailed failed = new StockReservationFailed(items, e.getMessage());
                JsonObject response = JsonObject.mapFrom(failed).put("status", "StockReservationFailed");
                return Message.of(response);
            })
            .onFailure(ProductNotFoundException.class).recoverWithItem(e -> {
                LOG.warnf("Stock reservation failed: Product not found: %s", e.getMessage());
                StockReservationFailed failed = new StockReservationFailed(items, "Product not found: " + e.getMessage());
                JsonObject response = JsonObject.mapFrom(failed).put("status", "StockReservationFailed");
                return Message.of(response);
            })
            .onFailure().recoverWithItem(e -> {
                LOG.errorf("Stock reservation failed with unexpected error: %s", e.getMessage());
                StockReservationFailed failed = new StockReservationFailed(items, "Internal error: " + e.getMessage());
                JsonObject response = JsonObject.mapFrom(failed).put("status", "StockReservationFailed");
                return Message.of(response);
            });
    }
}