package unit;

import entities.Buyer;
import interfaces.IBuyerRepository;
import io.smallrye.mutiny.Uni;
import services.BuyerService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BuyerServiceTest {
    private IBuyerRepository buyerRepository;
    private BuyerService buyerService;

    @BeforeEach
    void setUp() {
        buyerRepository = mock(IBuyerRepository.class);
        buyerService = new BuyerService(buyerRepository);
    }

    @Test
    void addBuyer_shouldReturnCreatedBuyer_whenBuyerIsValid() {
        Buyer buyer = new Buyer();
        when(buyerRepository.create(buyer)).thenReturn(Uni.createFrom().item(buyer));

        Uni<Buyer> result = buyerService.create(buyer);

        assertEquals(buyer, result.await().indefinitely());
        verify(buyerRepository, times(1)).create(buyer);
    }

    @Test
    void addBuyer_shouldThrowRuntimeException_whenBuyerIsInvalid() {
        Buyer buyer = new Buyer();
        when(buyerRepository.create(buyer)).thenReturn(Uni.createFrom().failure(new RuntimeException("Error")));

        Uni<Buyer> result = buyerService.create(buyer);

        assertThrows(RuntimeException.class, () -> result.await().indefinitely());
        verify(buyerRepository, times(1)).create(buyer);
    }

    @Test
    void getBuyerById_shouldReturnBuyer_whenBuyerExists() {
        int oauthId = 1;
        Buyer buyer = new Buyer();
        when(buyerRepository.read(oauthId)).thenReturn(Uni.createFrom().item(buyer));

        Uni<Buyer> result = buyerService.read(oauthId);

        assertEquals(buyer, result.await().indefinitely());
        verify(buyerRepository, times(1)).read(oauthId);
    }

    @Test
    void getBuyerById_shouldReturnNull_whenBuyerDoesNotExist() {
        int oauthId = 1;
        when(buyerRepository.read(oauthId)).thenReturn(Uni.createFrom().nullItem());

        Uni<Buyer> result = buyerService.read(oauthId);

        assertNull(result.await().indefinitely());
        verify(buyerRepository, times(1)).read(oauthId);
    }

    @Test
    void updateBuyer_shouldReturnUpdatedBuyer_whenBuyerIsValid() {
        Buyer buyer = new Buyer();
        when(buyerRepository.update(buyer)).thenReturn(Uni.createFrom().item(buyer));

        Uni<Buyer> result = buyerService.update(buyer);

        assertEquals(buyer, result.await().indefinitely());
        verify(buyerRepository, times(1)).update(buyer);
    }

    @Test
    void updateBuyer_shouldThrowRuntimeException_whenBuyerIsInvalid() {
        Buyer buyer = new Buyer();
        when(buyerRepository.update(buyer)).thenReturn(Uni.createFrom().failure(new RuntimeException("Error")));

        Uni<Buyer> result = buyerService.update(buyer);

        assertThrows(RuntimeException.class, () -> result.await().indefinitely());
        verify(buyerRepository, times(1)).update(buyer);
    }

    @Test
    void deleteBuyer_shouldReturnNoContent_whenBuyerExists() {
        int oauthId = 1;
        when(buyerRepository.delete(oauthId)).thenReturn(Uni.createFrom().voidItem());

        Uni<Void> result = buyerService.delete(oauthId);

        assertDoesNotThrow(() -> result.await().indefinitely());
        verify(buyerRepository, times(1)).delete(oauthId);
    }

    @Test
    void deleteBuyer_shouldThrowRuntimeException_whenBuyerDoesNotExist() {
        int oauthId = 1;
        when(buyerRepository.delete(oauthId)).thenReturn(Uni.createFrom().failure(new RuntimeException("Error")));

        Uni<Void> result = buyerService.delete(oauthId);

        assertThrows(RuntimeException.class, () -> result.await().indefinitely());
        verify(buyerRepository, times(1)).delete(oauthId);
    }

}
