package services;

import entities.Address;
import entities.Buyer;
import interfaces.IAddressRepository;
import interfaces.IBuyerRepository;
import interfaces.IAddressService;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
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
    public Uni<Address> create(int oauthId, Address address) {
        return buyerRepository.read(oauthId)
            .onItem().ifNull().failWith(() -> new EntityNotFoundException("Buyer not found with id: " + oauthId))
            .flatMap(buyer -> {
                address.setBuyer(buyer);
                return addressRepository.create(address);
            });
    }

    @Override
    public Uni<Address> update(int oauthId, Address address) {
        return buyerRepository.read(oauthId)
            .onItem().ifNull().failWith(() -> new EntityNotFoundException("Buyer not found with id: " + oauthId))
            .flatMap(buyer -> addressRepository.readById(address.getId())
                .onItem().ifNull().failWith(() -> new EntityNotFoundException("Address not found with id: " + address.getId()))
                .flatMap(existingAddress -> {
                    address.setBuyer(buyer);
                    return addressRepository.update(address);
                }));
    }

    @Override
    public Uni<Void> delete(int id) {
        return addressRepository.delete(id);
    }

    @Override
    public Uni<List<Address>> readAllByUser(int oauthId) {
        return buyerRepository.read(oauthId)
            .onItem().ifNull().failWith(() -> new EntityNotFoundException("Buyer not found with id: " + oauthId))
            .flatMap(buyer -> addressRepository.readAllByUser(oauthId));
    }
}