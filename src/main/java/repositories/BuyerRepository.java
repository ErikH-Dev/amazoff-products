package repositories;

import entities.Buyer;
import interfaces.IBuyerRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class BuyerRepository implements IBuyerRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Uni<Buyer> create(Buyer buyer) {
        return Uni.createFrom().item(() -> {
            entityManager.persist(buyer);
            entityManager.flush();
            entityManager.refresh(buyer);
            return buyer;
        });
    }

    @Override
    public Uni<Buyer> read(int oauthId) {
        return Uni.createFrom().item(() -> entityManager.find(Buyer.class, oauthId))
            .onItem().ifNull().failWith(() -> new EntityNotFoundException("Buyer not found with id: " + oauthId));
    }

    @Override
    public Uni<Buyer> update(Buyer buyer) {
        return Uni.createFrom().item(() -> entityManager.find(Buyer.class, buyer.getOauthId()))
            .onItem().ifNull().failWith(() -> new EntityNotFoundException("Buyer not found with id: " + buyer.getOauthId()))
            .map(existingBuyer -> entityManager.merge(buyer));
    }

    @Override
    public Uni<Void> delete(int oauthId) {
        return Uni.createFrom().item(() -> entityManager.find(Buyer.class, oauthId))
            .onItem().ifNull().failWith(() -> new EntityNotFoundException("Buyer not found with id: " + oauthId))
            .invoke(entityManager::remove)
            .replaceWithVoid();
    }
}