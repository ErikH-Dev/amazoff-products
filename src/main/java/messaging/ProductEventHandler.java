package messaging;

import interfaces.IProductRepository;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.*;
import org.jboss.logging.Logger;

import java.util.List;

@ApplicationScoped
public class ProductEventHandler {

    private static final Logger LOG = Logger.getLogger(ProductEventHandler.class);
    IProductRepository productRepository;
    
    public ProductEventHandler(IProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Incoming("get-products-requests")
    @Outgoing("get-products-responses")
    public Uni<Message<JsonObject>> handleGetProducts(Message<JsonObject> request) {
        List<Integer> ids = request.getPayload().getJsonArray("productIds").stream()
                .map(Object::toString)
                .map(Integer::valueOf)
                .toList();
        LOG.infof("Handling get-products request for ids=%s", ids);
        
        return productRepository.readByIds(ids)
                .map(products -> {
                    LOG.infof("Returning %d products for ids=%s", products.size(), ids);
                    JsonObject response = new JsonObject().put("products", products);
                    return Message.of(response);
                })
                .onFailure().recoverWithItem(e -> {
                    LOG.errorf("Failed to handle get-products request: %s", e.getMessage());
                    JsonObject errorResponse = new JsonObject()
                            .put("error", true)
                            .put("message", "Failed to retrieve products")
                            .put("productIds", ids);
                    return Message.of(errorResponse);
                });
    }
}