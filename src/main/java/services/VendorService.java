package services;

import entities.Vendor;
import interfaces.IVendorRepository;
import interfaces.IVendorService;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class VendorService implements IVendorService {
    private IVendorRepository vendorRepository;

    public VendorService(IVendorRepository vendorRepository) {
        this.vendorRepository = vendorRepository;
    }

    @Override
    public Uni<Vendor> create(Vendor vendor) {
        return vendorRepository.create(vendor);
    }

    @Override
    public Uni<Vendor> read(int oauthId) {
        return vendorRepository.read(oauthId);
    }

    @Override
    public Uni<Vendor> update(Vendor vendor) {
        return vendorRepository.update(vendor);
    }

    @Override
    public Uni<Void> delete(int oauthId) {
        return vendorRepository.delete(oauthId);
    }
}