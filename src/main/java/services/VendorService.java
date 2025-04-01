package services;

import entities.Vendor;
import interfaces.IVendorRepository;
import interfaces.IVendorService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class VendorService implements IVendorService{
    private IVendorRepository vendorRepository;

    public VendorService(IVendorRepository vendorRepository) {
        this.vendorRepository = vendorRepository;
    }

    @Override
    @Transactional
    public Vendor create(Vendor vendor) {
        return vendorRepository.create(vendor);
    }

    @Override
    public Vendor read(int oauthId) {
        return vendorRepository.read(oauthId);
    }

    @Override
    @Transactional
    public Vendor update(Vendor vendor) {
        return vendorRepository.update(vendor);
    }

    @Override
    @Transactional
    public void delete(int oauthId) {
        vendorRepository.delete(oauthId);
    }
    
}
