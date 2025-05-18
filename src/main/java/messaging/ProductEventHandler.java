package messaging;

import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

import org.eclipse.microprofile.reactive.messaging.*;

import interfaces.IProductRepository;

@ApplicationScoped
public class ProductEventHandler {

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
        return productRepository.readByIds(ids)
                .map(products -> {
                    JsonObject response = new JsonObject().put("products", products);
                    return Message.of(response);
                });
    }
}