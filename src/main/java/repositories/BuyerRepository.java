package repositories;

import entities.Buyer;
import interfaces.IBuyerRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class BuyerRepository implements IBuyerRepository{
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Buyer create(Buyer buyer) {
        entityManager.persist(buyer);
        entityManager.flush();
        entityManager.refresh(buyer);
        return buyer;
    }

    @Override
    public Buyer read(int oauthId) {
        Buyer buyer = entityManager.find(Buyer.class, oauthId);
        if (buyer == null) {
            throw new EntityNotFoundException("Buyer not found with id: " + oauthId);
        }
        return buyer;
    }

    @Override
    public Buyer update(Buyer buyer) {
        Buyer existingBuyer = entityManager.find(Buyer.class, buyer.getOauthId());
        if (existingBuyer == null) {
            throw new EntityNotFoundException("Buyer not found with id: " + buyer.getOauthId());
        }

        return entityManager.merge(buyer);
    }

    @Override
    public void delete(int oauthId) {
        Buyer buyer = entityManager.find(Buyer.class, oauthId);
        if (buyer == null) {
            throw new EntityNotFoundException("Buyer not found with id: " + oauthId);
        }
        entityManager.remove(buyer);
    }
}
