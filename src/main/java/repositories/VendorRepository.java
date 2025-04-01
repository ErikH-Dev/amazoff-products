package repositories;

import entities.Vendor;
import interfaces.IVendorRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class VendorRepository implements IVendorRepository{
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Vendor create(Vendor vendor) {
        entityManager.persist(vendor);
        entityManager.flush();
        entityManager.refresh(vendor);
        return vendor;
    }

    @Override
    public Vendor read(int oauthId) {
        Vendor vendor = entityManager.find(Vendor.class, oauthId);
        if (vendor == null) {
            throw new EntityNotFoundException("Vendor not found with id: " + oauthId);
        }
        return vendor;
    }

    @Override
    public Vendor update(Vendor vendor) {
        Vendor existingVendor = entityManager.find(Vendor.class, vendor.getOauthId());
        if (existingVendor == null) {
            throw new EntityNotFoundException("Vendor not found with id: " + vendor.getOauthId());
        }

        return entityManager.merge(vendor);
    }

    @Override
    public void delete(int oauthId) {
        Vendor vendor = entityManager.find(Vendor.class, oauthId);
        if (vendor == null) {
            throw new EntityNotFoundException("Vendor not found with id: " + oauthId);
        }
        entityManager.remove(vendor);
    }
}
