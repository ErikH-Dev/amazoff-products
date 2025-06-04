package services;

import entities.Product;
import entities.ProductDocument;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;

@ApplicationScoped
public class ProductEventPublisher {
    
    private static final Logger LOG = Logger.getLogger(ProductEventPublisher.class);
    
    @Inject
    @Channel("product-events")
    Emitter<JsonObject> eventEmitter;
    
    public void publishProductCreated(Product product) {
        ProductDocument doc = new ProductDocument(product);
        JsonObject event = new JsonObject()
            .put("eventType", "PRODUCT_CREATED")
            .put("product", JsonObject.mapFrom(doc))
            .put("timestamp", System.currentTimeMillis());
            
        eventEmitter.send(event);
        LOG.infof("Published PRODUCT_CREATED event for: %s", product.getProductId());
    }
    
    public void publishProductUpdated(Product product) {
        ProductDocument doc = new ProductDocument(product);
        JsonObject event = new JsonObject()
            .put("eventType", "PRODUCT_UPDATED")
            .put("product", JsonObject.mapFrom(doc))
            .put("timestamp", System.currentTimeMillis());
            
        eventEmitter.send(event);
        LOG.infof("Published PRODUCT_UPDATED event for: %s", product.getProductId());
    }
    
    public void publishProductDeleted(String productId) {
        JsonObject event = new JsonObject()
            .put("eventType", "PRODUCT_DELETED")
            .put("productId", productId)
            .put("timestamp", System.currentTimeMillis());
            
        eventEmitter.send(event);
        LOG.infof("Published PRODUCT_DELETED event for: %s", productId);
    }
}