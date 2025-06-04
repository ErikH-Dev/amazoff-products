package messaging;

import entities.ProductDocument;
import interfaces.IProductSearchRepository;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

@ApplicationScoped
public class ProductSyncHandler {
    
    private static final Logger LOG = Logger.getLogger(ProductSyncHandler.class);
    
    private final IProductSearchRepository productSearchRepository;
    
    public ProductSyncHandler(IProductSearchRepository productSearchRepository) {
        this.productSearchRepository = productSearchRepository;
    }
    
    @Incoming("product-sync")
    public Uni<Void> handleProductSync(Message<JsonObject> message) {
        JsonObject payload = message.getPayload();
        String eventType = payload.getString("eventType");
        
        LOG.infof("Received product sync event: %s", eventType);
        
        Uni<Void> result;
        
        if ("PRODUCT_CREATED".equals(eventType) || "PRODUCT_UPDATED".equals(eventType)) {
            ProductDocument product = payload.getJsonObject("product").mapTo(ProductDocument.class);
            result = productSearchRepository.indexProduct(product)
                .invoke(() -> LOG.infof("Synced product to Elasticsearch: %s", product.productId))
                .replaceWithVoid();
        } else if ("PRODUCT_DELETED".equals(eventType)) {
            String productId = payload.getString("productId");
            result = deleteFromElasticsearch(productId)
                .invoke(() -> LOG.infof("Deleted product from Elasticsearch: %s", productId))
                .replaceWithVoid();
        } else {
            LOG.warnf("Unknown event type: %s", eventType);
            result = Uni.createFrom().voidItem();
        }
        
        return result.eventually(() -> Uni.createFrom().completionStage(message.ack()));
    }
    
    private Uni<Void> deleteFromElasticsearch(String productId) {
        return productSearchRepository.deleteProduct(productId);
    }
}