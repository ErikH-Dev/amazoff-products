package services;

import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;
import java.time.Duration;


import dto.VendorDTO;
import exceptions.errors.VendorNotFoundException;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class VendorClientService {

    private static final Logger LOG = Logger.getLogger(VendorClientService.class);

    @Inject
    @Channel("get-vendor-requests")
    Emitter<JsonObject> requestEmitter;

    private final ConcurrentHashMap<Integer, CompletableFuture<VendorDTO>> pendingRequests = new ConcurrentHashMap<>();

public Uni<VendorDTO> getVendorByOauthId(int oauthId) {
    LOG.infof("Requesting vendor details for oauthId=%d", oauthId);
    CompletableFuture<VendorDTO> future = new CompletableFuture<>();
    pendingRequests.put(oauthId, future);
    JsonObject requestJson = new JsonObject().put("oauthId", oauthId);
    
    try {
        requestEmitter.send(requestJson);
    } catch (Exception e) {
        LOG.errorf("Failed to send vendor request: %s", e.getMessage());
        pendingRequests.remove(oauthId);
        return Uni.createFrom().failure(e);
    }

    return Uni.createFrom().completionStage(future)
            .ifNoItem().after(Duration.ofSeconds(10)).failWith(new RuntimeException("Timeout waiting for vendor response for oauthId=" + oauthId))
            .onFailure().invoke(e -> {
                // Clean up pending request on timeout or failure
                pendingRequests.remove(oauthId);
                LOG.errorf("Failed to get vendor for oauthId=%d: %s", oauthId, e.getMessage());
            });
}

    @Incoming("get-vendor-responses")
    public Uni<Void> onVendorResponse(Message<JsonObject> message) {
        LOG.info("Received vendor response from Users service");
        JsonObject json = message.getPayload();

        // Check if this is an error response
        if (json.getBoolean("error", false)) {
            int oauthId = json.getInteger("oauthId");
            String errorMessage = json.getString("message", "Unknown error");
            LOG.warnf("Received error response for oauthId=%d: %s", oauthId, errorMessage);

            CompletableFuture<VendorDTO> future = pendingRequests.remove(oauthId);
            if (future != null) {
                future.completeExceptionally(new VendorNotFoundException(oauthId));
            }
            return Uni.createFrom().completionStage(message.ack()).replaceWithVoid();
        }

        VendorDTO vendor;
        try {
            vendor = json.mapTo(VendorDTO.class);
        } catch (Exception e) {
            LOG.errorf("Failed to parse vendor response: %s", e.getMessage());
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