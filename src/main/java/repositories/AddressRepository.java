package repositories;

import entities.Address;
import interfaces.IAddressRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@ApplicationScoped
public class AddressRepository implements IAddressRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Uni<Address> create(Address address) {
        return Uni.createFrom().item(() -> {
            entityManager.persist(address);
            entityManager.flush();
            entityManager.refresh(address);
            return address;
        });
    }

    @Override
    public Uni<Address> update(Address address) {
        return Uni.createFrom().item(() -> entityManager.find(Address.class, address.getId()))
            .onItem().ifNull().failWith(() -> new EntityNotFoundException("Address not found with id: " + address.getId()))
            .map(existingAddress -> entityManager.merge(address));
    }

    @Override
    public Uni<Void> delete(int id) {
        return Uni.createFrom().item(() -> entityManager.find(Address.class, id))
            .onItem().ifNull().failWith(() -> new EntityNotFoundException("Address not found with id: " + id))
            .invoke(entityManager::remove)
            .replaceWithVoid();
    }

    @Override
    public Uni<List<Address>> readAllByUser(int oauthId) {
        return Uni.createFrom().item(() -> entityManager.createQuery(
            "SELECT a FROM Address a WHERE a.buyer.oauthId = :oauthId", Address.class
        ).setParameter("oauthId", oauthId).getResultList());
    }

    @Override
    public Uni<Address> readById(int id) {
        return Uni.createFrom().item(() -> entityManager.find(Address.class, id))
            .onItem().ifNull().failWith(() -> new EntityNotFoundException("Address not found with id: " + id));
    }
}