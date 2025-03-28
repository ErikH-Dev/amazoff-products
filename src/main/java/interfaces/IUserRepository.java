package interfaces;

import entities.User;

public interface IUserRepository {
    public User create(User user);
    public User read(int id);
    public User update(User user);
    public void delete(int id);
}
