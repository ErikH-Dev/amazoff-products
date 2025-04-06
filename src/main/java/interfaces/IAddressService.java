package interfaces;

import entities.Address;
import io.smallrye.mutiny.Uni;

import java.util.List;

public interface IAddressService {
    Uni<Address> create(int oauthId, Address address);
    Uni<Address> update(int oauthId, Address address);
    Uni<Void> delete(int id);
    Uni<List<Address>> readAllByUser(int oauthId);
}