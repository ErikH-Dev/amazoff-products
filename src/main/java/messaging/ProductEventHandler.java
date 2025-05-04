package messaging;

import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.microprofile.reactive.messaging.*;

import entities.Product;
import interfaces.IProductRepository;

@ApplicationScoped
public class ProductEventHandler {

    IProductRepository productRepository;

    public ProductEventHandler(IProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Incoming("get-products-requests")
    @Outgoing("get-products-responses")
    public Uni<Message<List<Product>>> handleGetProducts(Message<JsonObject> request) {
        return productRepository.readByIds(request.getPayload().getJsonArray("productIds").stream()
                .map(Object::toString)
                .map(Integer::valueOf)
                .toList())
                .map(Message::of);
    }
}
