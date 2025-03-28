package services;

import entities.User;
import interfaces.IUserRepository;
import interfaces.IUserService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@ApplicationScoped
public class UserService implements IUserService{
    private IUserRepository userRepository;

    public UserService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    @Transactional
    public User create(@Valid User user) {
        return userRepository.create(user);
    }

    @Override
    public User read(int id) {
        return userRepository.read(id);
    }

    @Override
    @Transactional
    public User update(@Valid User user) {
        return userRepository.update(user);
    }

    @Override
    @Transactional
    public void delete(int id) {
        userRepository.delete(id);
    }
}
