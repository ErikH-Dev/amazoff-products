package interfaces;

import entities.Vendor;

public interface IVendorRepository {
    Vendor create(Vendor buyer);
    Vendor read(int oauthId);
    Vendor update(Vendor buyer);
    void delete(int oauthId);
}
