package repositories;

import entities.User;
import interfaces.IUserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@ApplicationScoped
public class UserRepository implements IUserRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public User create(@Valid User user) {
        entityManager.persist(user);
        entityManager.flush();
        entityManager.refresh(user);
        return user;
    }

    @Override
    public User read(int id) {
        User user = entityManager.find(User.class, id);
        if (user == null) {
            throw new EntityNotFoundException("User not found with id: " + id);
        }
        return user;
    }

    @Override
    public User update(@Valid User user) {
        User existingUser = entityManager.find(User.class, user.getOauthId());
        if (existingUser == null) {
            throw new EntityNotFoundException("User not found with id: " + user.getOauthId());
        }

        if (!existingUser.getClass().equals(user.getClass())) {
            throw new IllegalArgumentException(
                    "User type mismatch: cannot update " + existingUser.getClass().getSimpleName() +
                            " with " + user.getClass().getSimpleName());
        }

        return entityManager.merge(user);
    }

    @Override
    public void delete(int id) {
        User user = entityManager.find(User.class, id);
        if (user == null) {
            throw new EntityNotFoundException("User not found with id: " + id);
        }
        entityManager.remove(user);
    }
}