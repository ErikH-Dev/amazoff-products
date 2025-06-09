package messaging;

import java.util.List;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import dto.ReserveStockItem;
import dto.ReleaseStockRequest;
import dto.StockReleaseFailed;
import dto.StockReleased;
import exceptions.errors.ProductNotFoundException;
import interfaces.IProductService;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

@ApplicationScoped
public class ReleaseStockHandler {

    private static final Logger LOG = Logger.getLogger(ReleaseStockHandler.class);

    IProductService productService;

    public ReleaseStockHandler(IProductService productService) {
        this.productService = productService;
    }

    @Incoming("release-stock-requests")
    @Outgoing("release-stock-responses")
    public Uni<Message<JsonObject>> handleReleaseStock(Message<JsonObject> request) {
        ReleaseStockRequest releaseRequest = request.getPayload().mapTo(ReleaseStockRequest.class);
        List<ReserveStockItem> items = releaseRequest.items;
        LOG.infof("Handling release-stock request for items=%s", items);

        return productService.releaseProductStocks(items)
            .map(v -> {
                LOG.info("Stock released successfully");
                StockReleased released = new StockReleased(items);
                JsonObject response = JsonObject.mapFrom(released);
                return Message.of(response);
            })
            .onFailure(ProductNotFoundException.class).recoverWithItem(e -> {
                LOG.warn("Stock release failed: Product not found");
                StockReleaseFailed failed = new StockReleaseFailed(items, "Product not found");
                JsonObject response = JsonObject.mapFrom(failed);
                return Message.of(response);
            })
            .onFailure().recoverWithItem(e -> {
                LOG.warn("Stock release failed: " + e.getMessage());
                StockReleaseFailed failed = new StockReleaseFailed(items, e.getMessage());
                JsonObject response = JsonObject.mapFrom(failed);
                return Message.of(response);
            });
    }
}