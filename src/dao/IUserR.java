package dao;

import model.User;
import java.util.List;

//Repository interface for User data access
//Provides abstraction over data persistence layer

public interface IUserR {
    String create(User user);

    User findById(String id);

    List<User> getAll();

    boolean update(User user);

    boolean delete(String id);

    User findByUsername(String username);

    User findByEmail(String email);

    List<User> searchByUsername(String query);
}
