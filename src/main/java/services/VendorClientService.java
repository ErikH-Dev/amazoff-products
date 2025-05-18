package services;

import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;

import dto.VendorDTO;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class VendorClientService {

    @Inject
    @Channel("get-vendor-requests")
    Emitter<JsonObject> requestEmitter;

    private final ConcurrentHashMap<Integer, CompletableFuture<VendorDTO>> pendingRequests = new ConcurrentHashMap<>();

    public Uni<VendorDTO> getVendorByOauthId(int oauthId) {
        CompletableFuture<VendorDTO> future = new CompletableFuture<>();
        pendingRequests.put(oauthId, future);
        JsonObject requestJson = new JsonObject().put("oauthId", oauthId);
        requestEmitter.send(requestJson);
        return Uni.createFrom().completionStage(future);
    }

    @Incoming("get-vendor-responses")
    public Uni<Void> onVendorResponse(Message<JsonObject> message) {
        JsonObject json = message.getPayload();
        VendorDTO vendor;
        try {
            vendor = json.mapTo(VendorDTO.class);
        } catch (Exception e) {
            return Uni.createFrom().completionStage(message.ack()).replaceWithVoid();
        }
        int oauthId = vendor.getOauthId();
        CompletableFuture<VendorDTO> future = pendingRequests.remove(oauthId);
        if (future != null) {
            future.complete(vendor);
        }
        return Uni.createFrom().completionStage(message.ack()).replaceWithVoid();
    }
}