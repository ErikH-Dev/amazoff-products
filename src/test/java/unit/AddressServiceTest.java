package unit;

import entities.Address;
import entities.Buyer;
import interfaces.IAddressRepository;
import interfaces.IBuyerRepository;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.EntityNotFoundException;
import services.AddressService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AddressServiceTest {
    private IAddressRepository addressRepository;
    private IBuyerRepository buyerRepository;
    private AddressService addressService;

    @BeforeEach
    void setUp() {
        addressRepository = mock(IAddressRepository.class);
        buyerRepository = mock(IBuyerRepository.class);
        addressService = new AddressService(addressRepository, buyerRepository);
    }

    @Test
    void createAddress_shouldReturnCreatedAddress_whenBuyerExists() {
        int oauthId = 1;
        Buyer buyer = new Buyer();
        Address address = new Address();
        when(buyerRepository.read(oauthId)).thenReturn(Uni.createFrom().item(buyer));
        when(addressRepository.create(address)).thenReturn(Uni.createFrom().item(address));

        Uni<Address> result = addressService.create(oauthId, address);

        assertEquals(address, result.await().indefinitely());
        verify(buyerRepository, times(1)).read(oauthId);
        verify(addressRepository, times(1)).create(address);
    }

    @Test
    void createAddress_shouldThrowEntityNotFoundException_whenBuyerDoesNotExist() {
        int oauthId = 1;
        Address address = new Address();
        when(buyerRepository.read(oauthId)).thenReturn(Uni.createFrom().nullItem());

        Uni<Address> result = addressService.create(oauthId, address);

        assertThrows(EntityNotFoundException.class, () -> result.await().indefinitely());
        verify(buyerRepository, times(1)).read(oauthId);
        verify(addressRepository, never()).create(any());
    }

    @Test
    void updateAddress_shouldReturnUpdatedAddress_whenBuyerAndAddressExist() {
        int oauthId = 1;
        Buyer buyer = new Buyer();
        Address address = new Address(1, "123 Main St", "City", "State", "12345", "Country");
        when(buyerRepository.read(oauthId)).thenReturn(Uni.createFrom().item(buyer));
        when(addressRepository.readById(address.getId())).thenReturn(Uni.createFrom().item(address));
        when(addressRepository.update(address)).thenReturn(Uni.createFrom().item(address));

        Uni<Address> result = addressService.update(oauthId, address);

        assertEquals(address, result.await().indefinitely());
        verify(buyerRepository, times(1)).read(oauthId);
        verify(addressRepository, times(1)).readById(address.getId());
        verify(addressRepository, times(1)).update(address);
    }

    @Test
    void updateAddress_shouldThrowEntityNotFoundException_whenBuyerDoesNotExist() {
        int oauthId = 1;
        Address address = new Address(1, "123 Main St", "City", "State", "12345", "Country");
        when(buyerRepository.read(oauthId)).thenReturn(Uni.createFrom().nullItem());

        Uni<Address> result = addressService.update(oauthId, address);

        assertThrows(EntityNotFoundException.class, () -> result.await().indefinitely());
        verify(buyerRepository, times(1)).read(oauthId);
        verify(addressRepository, never()).readById(anyInt());
        verify(addressRepository, never()).update(any());
    }

    @Test
    void updateAddress_shouldThrowEntityNotFoundException_whenAddressDoesNotExist() {
        int oauthId = 1;
        Buyer buyer = new Buyer();
        Address address = new Address(1, "123 Main St", "City", "State", "12345", "Country");
        when(buyerRepository.read(oauthId)).thenReturn(Uni.createFrom().item(buyer));
        when(addressRepository.readById(address.getId())).thenReturn(Uni.createFrom().nullItem());

        Uni<Address> result = addressService.update(oauthId, address);

        assertThrows(EntityNotFoundException.class, () -> result.await().indefinitely());
        verify(buyerRepository, times(1)).read(oauthId);
        verify(addressRepository, times(1)).readById(address.getId());
        verify(addressRepository, never()).update(any());
    }

    @Test
    void deleteAddress_shouldReturnNoContent_whenAddressExists() {
        int addressId = 1;
        when(addressRepository.delete(addressId)).thenReturn(Uni.createFrom().voidItem());

        Uni<Void> result = addressService.delete(addressId);

        assertDoesNotThrow(() -> result.await().indefinitely());
        verify(addressRepository, times(1)).delete(addressId);
    }

    @Test
    void deleteAddress_shouldThrowRuntimeException_whenAddressDoesNotExist() {
        int addressId = 1;
        when(addressRepository.delete(addressId)).thenReturn(Uni.createFrom().failure(new RuntimeException("Error")));

        Uni<Void> result = addressService.delete(addressId);

        assertThrows(RuntimeException.class, () -> result.await().indefinitely());
        verify(addressRepository, times(1)).delete(addressId);
    }

    @Test
    void readAllAddressesByUser_shouldReturnAddresses_whenBuyerExists() {
        int oauthId = 1;
        Buyer buyer = new Buyer();
        List<Address> addresses = List.of(new Address(), new Address());
        when(buyerRepository.read(oauthId)).thenReturn(Uni.createFrom().item(buyer));
        when(addressRepository.readAllByUser(oauthId)).thenReturn(Uni.createFrom().item(addresses));

        Uni<List<Address>> result = addressService.readAllByUser(oauthId);

        assertEquals(addresses, result.await().indefinitely());
        verify(buyerRepository, times(1)).read(oauthId);
        verify(addressRepository, times(1)).readAllByUser(oauthId);
    }

    @Test
    void readAllAddressesByUser_shouldThrowEntityNotFoundException_whenBuyerDoesNotExist() {
        int oauthId = 1;
        when(buyerRepository.read(oauthId)).thenReturn(Uni.createFrom().nullItem());

        Uni<List<Address>> result = addressService.readAllByUser(oauthId);

        assertThrows(EntityNotFoundException.class, () -> result.await().indefinitely());
        verify(buyerRepository, times(1)).read(oauthId);
        verify(addressRepository, never()).readAllByUser(anyInt());
    }
}
