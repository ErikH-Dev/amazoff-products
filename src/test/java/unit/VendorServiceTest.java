package unit;

import entities.Vendor;
import interfaces.IVendorRepository;
import io.smallrye.mutiny.Uni;
import services.VendorService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VendorServiceTest {
    private IVendorRepository vendorRepository;
    private VendorService vendorService;

    @BeforeEach
    void setUp() {
        vendorRepository = mock(IVendorRepository.class);
        vendorService = new VendorService(vendorRepository);
    }

    @Test
    void addVendor_shouldReturnCreatedVendor_whenVendorIsValid() {
        Vendor vendor = new Vendor();
        when(vendorRepository.create(vendor)).thenReturn(Uni.createFrom().item(vendor));

        Uni<Vendor> result = vendorService.create(vendor);

        assertEquals(vendor, result.await().indefinitely());
        verify(vendorRepository, times(1)).create(vendor);
    }

    @Test
    void addVendor_shouldThrowRuntimeException_whenVendorIsInvalid() {
        Vendor vendor = new Vendor();
        when(vendorRepository.create(vendor)).thenReturn(Uni.createFrom().failure(new RuntimeException("Error")));

        Uni<Vendor> result = vendorService.create(vendor);

        assertThrows(RuntimeException.class, () -> result.await().indefinitely());
        verify(vendorRepository, times(1)).create(vendor);
    }

    @Test
    void getVendorById_shouldReturnVendor_whenVendorExists() {
        int oauthId = 1;
        Vendor vendor = new Vendor();
        when(vendorRepository.read(oauthId)).thenReturn(Uni.createFrom().item(vendor));

        Uni<Vendor> result = vendorService.read(oauthId);

        assertEquals(vendor, result.await().indefinitely());
        verify(vendorRepository, times(1)).read(oauthId);
    }

    @Test
    void getVendorById_shouldReturnNull_whenVendorDoesNotExist() {
        int oauthId = 1;
        when(vendorRepository.read(oauthId)).thenReturn(Uni.createFrom().nullItem());

        Uni<Vendor> result = vendorService.read(oauthId);

        assertNull(result.await().indefinitely());
        verify(vendorRepository, times(1)).read(oauthId);
    }

    @Test
    void updateVendor_shouldReturnUpdatedVendor_whenVendorIsValid() {
        Vendor vendor = new Vendor();
        when(vendorRepository.update(vendor)).thenReturn(Uni.createFrom().item(vendor));

        Uni<Vendor> result = vendorService.update(vendor);

        assertEquals(vendor, result.await().indefinitely());
        verify(vendorRepository, times(1)).update(vendor);
    }

    @Test
    void updateVendor_shouldThrowRuntimeException_whenVendorIsInvalid() {
        Vendor vendor = new Vendor();
        when(vendorRepository.update(vendor)).thenReturn(Uni.createFrom().failure(new RuntimeException("Error")));

        Uni<Vendor> result = vendorService.update(vendor);

        assertThrows(RuntimeException.class, () -> result.await().indefinitely());
        verify(vendorRepository, times(1)).update(vendor);
    }

    @Test
    void deleteVendor_shouldReturnNoContent_whenVendorExists() {
        int oauthId = 1;
        when(vendorRepository.delete(oauthId)).thenReturn(Uni.createFrom().voidItem());

        Uni<Void> result = vendorService.delete(oauthId);

        assertDoesNotThrow(() -> result.await().indefinitely());
        verify(vendorRepository, times(1)).delete(oauthId);
    }

    @Test
    void deleteVendor_shouldThrowRuntimeException_whenVendorDoesNotExist() {
        int oauthId = 1;
        when(vendorRepository.delete(oauthId)).thenReturn(Uni.createFrom().failure(new RuntimeException("Error")));

        Uni<Void> result = vendorService.delete(oauthId);

        assertThrows(RuntimeException.class, () -> result.await().indefinitely());
        verify(vendorRepository, times(1)).delete(oauthId);
    }
}
