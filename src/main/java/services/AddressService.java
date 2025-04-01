package services;

import java.util.List;

import entities.Address;
import interfaces.IAddressRepository;
import interfaces.IAddressService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class AddressService implements IAddressService {
    private IAddressRepository addressRepository;

    public AddressService(IAddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @Override
    @Transactional
    public Address create(Address address) {
        return addressRepository.create(address);
    }

    @Override
    public List<Address> readAllByUser(int oauthId) {
        return addressRepository.readAllByUser(oauthId);
    }

    @Override
    @Transactional
    public Address update(Address address) {
        return addressRepository.update(address);
    }

    @Override
    @Transactional
    public void delete(int id) {
        addressRepository.delete(id);
    }
}
