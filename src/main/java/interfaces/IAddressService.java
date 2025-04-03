package interfaces;

import java.util.List;

import entities.Address;

public interface IAddressService {
    Address create(int oauthId, Address address);
    Address update(int oauthId, Address address);
    void delete(int id);
    List<Address> readAllByUser(int oauthId);
}