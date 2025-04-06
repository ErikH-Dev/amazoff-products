package interfaces;

import entities.Vendor;
import io.smallrye.mutiny.Uni;

public interface IVendorService {
    Uni<Vendor> create(Vendor vendor);
    Uni<Vendor> read(int oauthId);
    Uni<Vendor> update(Vendor vendor);
    Uni<Void> delete(int oauthId);
}