package interfaces;

import entities.Buyer;

public interface IBuyerService {
    Buyer create(Buyer buyer);
    Buyer read(int oauthId);
    Buyer update(Buyer buyer);
    void delete(int oauthId);
}
