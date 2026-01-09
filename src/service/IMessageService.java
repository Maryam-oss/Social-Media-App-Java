package service;

import model.Message;
import java.util.List;

/**
 * Interface for Message Service operations
 * Provides abstraction for messaging functionality and real-time updates
 */
public interface IMessageService {

    // Message CRUD
    boolean sendMessage(String senderId, String receiverId, String content);

    Message getMessageById(String messageId);

    List<Message> getConversation(String userId1, String userId2);

    boolean updateMessage(String messageId, String newContent);

    boolean deleteMessage(String messageId);

    // Conversation Management
    List<String> getConversationList(String userId);

    // Real-Time Listener (Observer Pattern)
    void startRealTimeListener(dao.MessageListener listener);

    void stopRealTimeListener();
}
