package services;

import entities.Address;
import entities.Buyer;
import interfaces.IAddressRepository;
import interfaces.IBuyerRepository;
import interfaces.IAddressService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;

@ApplicationScoped
public class AddressService implements IAddressService {
    private final IAddressRepository addressRepository;
    private final IBuyerRepository buyerRepository;

    public AddressService(IAddressRepository addressRepository, IBuyerRepository buyerRepository) {
        this.addressRepository = addressRepository;
        this.buyerRepository = buyerRepository;
    }

    @Override
    @Transactional
    public Address create(int userId, Address address) {
        Buyer buyer = buyerRepository.read(userId);
        if (buyer == null) {
            throw new EntityNotFoundException("Buyer not found with id: " + userId);
        }
        address.setBuyer(buyer);
        return addressRepository.create(address);
    }

    @Override
    @Transactional
    public Address update(int oauthId, Address address) {
        Buyer buyer = buyerRepository.read(oauthId);
        if (buyer == null) {
            throw new EntityNotFoundException("Buyer not found with id: " + oauthId);
        }

        Address existingAddress = addressRepository.readById(address.getId());
        if (existingAddress == null) {
            throw new EntityNotFoundException("Address not found with id: " + address.getId());
        }
        address.setBuyer(buyer);
        return addressRepository.update(address);
    }

    @Override
    @Transactional
    public void delete(int id) {
        addressRepository.delete(id);
    }

    @Override
    public List<Address> readAllByUser(int oauthId) {
        if (buyerRepository.read(oauthId) == null) {
            throw new EntityNotFoundException("Buyer not found with id: " + oauthId);
        }
        return addressRepository.readAllByUser(oauthId);
    }
}