package interfaces;

import entities.Address;

import java.util.List;

public interface IAddressRepository {
    Address create(Address address);
    Address update(Address address);
    void delete(int id);
    List<Address> readAllByUser(int userId);
    Address readById(int id);
}