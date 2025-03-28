package interfaces;

import java.util.List;

import entities.Address;
import entities.User;

public interface IAddressService {
    Address create(Address address);
    List<Address> readAllByUser(int userId);
    Address update(Address address);
    void delete(int id);
}
