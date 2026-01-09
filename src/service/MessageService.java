package service;

import dao.DBConnection;
import dao.MessageDAO;
import dao.NotificationDAO;
import dao.UserDAO;
import model.Message;
import model.User;
import model.Notification;
import org.bson.Document;
import com.mongodb.client.*;
import com.mongodb.client.model.changestream.ChangeStreamDocument;

import javax.swing.SwingUtilities;
import java.util.List;

public class MessageService implements IMessageService {

    private MessageDAO messageDAO;
    private NotificationDAO notificationDAO;
    private UserDAO userDAO;

    public MessageService() {
        this.messageDAO = new MessageDAO();
        this.notificationDAO = new NotificationDAO();
        this.userDAO = new UserDAO();
    }

    // Send a message and create notification
    public boolean sendMessage(String senderId, String receiverId, String content) {
        if (content == null || content.trim().isEmpty())
            return false;

        Message message = new Message(senderId, receiverId, content);
        messageDAO.create(message);

        User sender = userDAO.findById(senderId);
        String senderName = (sender != null) ? sender.getUsername() : "Unknown";

        Notification notif = new Notification(receiverId, senderName + " sent you a message", "message");
        notificationDAO.create(notif);

        return true;
    }

    // Get conversation between two users
    public List<Message> getConversation(String userId1, String userId2) {
        List<Message> allMessages = messageDAO.getConversation(userId1, userId2);
        List<Message> filteredMessages = new java.util.ArrayList<>();
        for (Message m : allMessages) {
            if (m.getDeletedBy() == null || !m.getDeletedBy().contains(userId1)) {
                filteredMessages.add(m);
            }
        }
        return filteredMessages;
    }

    public void deleteMessageForMe(String messageId, String userId) {
        messageDAO.deleteForUser(messageId, userId);
    }

    public List<Message> getGroupMessages(String groupId) {
        return messageDAO.getGroupMessages(groupId);
    }

    // Delete a message by ID
    public boolean deleteMessage(String messageId) {
        return messageDAO.delete(messageId);
    }

    private Thread listenerThread;

    // Real-Time Listener for new messages
    public void startRealTimeListener(dao.MessageListener listener) {
        if (listenerThread != null && listenerThread.isAlive()) {
            return; // Already running
        }
        listenerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                MongoDatabase db = DBConnection.getInstance();
                MongoCollection<Document> messagesCollection = db.getCollection("messages");

                while (!Thread.currentThread().isInterrupted()) {
                    try (MongoChangeStreamCursor<ChangeStreamDocument<Document>> cursor = messagesCollection.watch()
                            .cursor()) {
                        while (cursor.hasNext()) {
                            ChangeStreamDocument<Document> change = cursor.next();

                            if (change
                                    .getOperationType() != com.mongodb.client.model.changestream.OperationType.INSERT) {
                                continue;
                            }

                            Document newMessageDoc = change.getFullDocument();
                            if (newMessageDoc != null) {
                                Message newMessage = new Message(
                                        newMessageDoc.getString("senderId"),
                                        newMessageDoc.getString("receiverId"),
                                        newMessageDoc.getString("content"));
                                newMessage.setId(newMessageDoc.getString("id"));
                                newMessage.setTimestamp(newMessageDoc.getLong("timestamp"));

                                SwingUtilities.invokeLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onMessageReceived(newMessage);
                                    }
                                });
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("Real-time listener error: " + e.getMessage());
                        // Wait before reconnecting to avoid spamming
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                }
            }
        });
        listenerThread.setDaemon(true); // Ensure thread dies when app closes
        listenerThread.start();
    }

    @Override
    public void stopRealTimeListener() {
        if (listenerThread != null) {
            listenerThread.interrupt();
            listenerThread = null;
        }
    }





    @Override
    public List<String> getConversationList(String userId) {
        List<String> conversations = new java.util.ArrayList<>();
        for (Message m : messageDAO.getAll()) {
            if (m.getSenderId().equals(userId) || m.getReceiverId().equals(userId)) {
                String otherId = m.getSenderId().equals(userId) ? m.getReceiverId() : m.getSenderId();
                if (!conversations.contains(otherId)) {
                    conversations.add(otherId);
                }
            }
        }
        return conversations;
    }

    @Override
    public boolean updateMessage(String messageId, String newContent) {
        Message message = messageDAO.findById(messageId);
        if (message == null)
            return false;
        message.setContent(newContent);
        return messageDAO.update(message);
    }

    @Override
    public Message getMessageById(String messageId) {
        return messageDAO.findById(messageId);
    }
}
