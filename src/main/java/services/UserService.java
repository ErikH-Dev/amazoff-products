package services;

import entities.User;
import interfaces.IUserRepository;
import interfaces.IUserService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class UserService implements IUserService{
    private IUserRepository userRepository;

    public UserService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    @Transactional
    public User create(User user) {
        return userRepository.create(user);
    }

    @Override
    public User read(int oauthId) {
        return userRepository.read(oauthId);
    }

    @Override
    @Transactional
    public User update(User user) {
        return userRepository.update(user);
    }

    @Override
    @Transactional
    public void delete(int id) {
        userRepository.delete(id);
    }
}
