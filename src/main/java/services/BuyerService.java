package services;

import entities.Buyer;
import interfaces.IBuyerRepository;
import interfaces.IBuyerService;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BuyerService implements IBuyerService {
    private IBuyerRepository buyerRepository;

    public BuyerService(IBuyerRepository buyerRepository) {
        this.buyerRepository = buyerRepository;
    }

    @Override
    public Uni<Buyer> create(Buyer buyer) {
        return buyerRepository.create(buyer);
    }

    @Override
    public Uni<Buyer> read(int oauthId) {
        return buyerRepository.read(oauthId);
    }

    @Override
    public Uni<Buyer> update(Buyer buyer) {
        return buyerRepository.update(buyer);
    }

    @Override
    public Uni<Void> delete(int oauthId) {
        return buyerRepository.delete(oauthId);
    }
}