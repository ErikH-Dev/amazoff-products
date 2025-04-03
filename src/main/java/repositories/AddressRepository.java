package repositories;

import entities.Address;
import interfaces.IAddressRepository;
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
    public Address create(Address address) {
        entityManager.persist(address);
        entityManager.flush();
        entityManager.refresh(address);
        return address;
    }

    @Override
    public Address update(Address address) {
        return entityManager.merge(address);
    }

    @Override
    public void delete(int id) {
        Address address = entityManager.find(Address.class, id);
        if (address == null) {
            throw new EntityNotFoundException("Address not found with id: " + id);
        }
        entityManager.remove(address);
    }

    @Override
    public List<Address> readAllByUser(int oauthId) {
        return entityManager.createQuery(
            "SELECT a FROM Address a WHERE a.buyer.oauthId = :oauthId", Address.class
        ).setParameter("oauthId", oauthId).getResultList();
    }

    @Override
    public Address readById(int id) {
        return entityManager.find(Address.class, id);
    }
}