package services;

import entities.Buyer;
import interfaces.IBuyerRepository;
import interfaces.IBuyerService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class BuyerService implements IBuyerService{
    private IBuyerRepository buyerRepository;

    public BuyerService(IBuyerRepository buyerRepository) {
        this.buyerRepository = buyerRepository;
    }

    @Override
    @Transactional
    public Buyer create(Buyer buyer) {
        return buyerRepository.create(buyer);
    }

    @Override
    public Buyer read(int oauthId) {
        return buyerRepository.read(oauthId);
    }

    @Override
    @Transactional
    public Buyer update(Buyer buyer) {
        return buyerRepository.update(buyer);
    }

    @Override
    @Transactional
    public void delete(int oauthId) {
        buyerRepository.delete(oauthId);
    }
    
}
