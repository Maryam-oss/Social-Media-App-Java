package dao;

import model.Message;
import java.util.List;

/**
 * Repository interface for Message data access
 * Provides abstraction over data persistence layer
 */
public interface IMsgR {
    String create(Message message);

    Message findById(String id);

    List<Message> getAll();

    boolean update(Message message);

    boolean delete(String id);

    List<Message> getConversation(String userId1, String userId2);
}
