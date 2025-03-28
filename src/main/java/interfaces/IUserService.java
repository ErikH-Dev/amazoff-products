package interfaces;

import entities.User;

public interface IUserService {
    public User create(User user);
    public User read(int id);
    public User update(User user);
    public void delete(int id);
}
