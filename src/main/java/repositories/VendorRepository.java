package repositories;

import entities.Vendor;
import interfaces.IVendorRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class VendorRepository implements IVendorRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Uni<Vendor> create(Vendor vendor) {
        return Uni.createFrom().item(() -> {
            entityManager.persist(vendor);
            entityManager.flush();
            entityManager.refresh(vendor);
            return vendor;
        });
    }

    @Override
    public Uni<Vendor> read(int oauthId) {
        return Uni.createFrom().item(() -> entityManager.find(Vendor.class, oauthId))
            .onItem().ifNull().failWith(() -> new EntityNotFoundException("Vendor not found with id: " + oauthId));
    }

    @Override
    public Uni<Vendor> update(Vendor vendor) {
        return Uni.createFrom().item(() -> entityManager.find(Vendor.class, vendor.getOauthId()))
            .onItem().ifNull().failWith(() -> new EntityNotFoundException("Vendor not found with id: " + vendor.getOauthId()))
            .map(existingVendor -> entityManager.merge(vendor));
    }

    @Override
    public Uni<Void> delete(int oauthId) {
        return Uni.createFrom().item(() -> entityManager.find(Vendor.class, oauthId))
            .onItem().ifNull().failWith(() -> new EntityNotFoundException("Vendor not found with id: " + oauthId))
            .invoke(entityManager::remove)
            .replaceWithVoid();
    }
}