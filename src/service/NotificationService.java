package service;

import dao.NotificationDAO;
import java.time.*;
import java.util.*;
import model.Notification;

public class NotificationService implements INotificationService {

    private final NotificationDAO notificationDAO;

    public NotificationService() {
        this.notificationDAO = new NotificationDAO();
    }

    public Notification createNotification(String userId, String message) {
        Notification n = createNotification(userId, message, "general", null);
        return n;
    }
//overloaded methods
    public Notification createNotification(String userId, String message, String type, String relatedUserId) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRelatedUserId(relatedUserId);

        String uuid = UUID.randomUUID().toString();
        notification.setNotificationId(uuid);

        long currentTime = System.currentTimeMillis();
        notification.setCreatedAt(currentTime);

        notificationDAO.create(notification);
        return notification;
    }

    public List<Notification> getNotifications(String userId) {
        List<Notification> list = notificationDAO.getUserNotifications(userId);
        return list;
    }
//done in dao
    public boolean deleteNotification(String notificationId) {
        boolean deleted = notificationDAO.delete(notificationId);
        return deleted;
    }

    public Notification getNotificationById(String id) {
        Notification n = notificationDAO.findById(id);
        return n;
    }



    public void notifyPostCommented(String postOwnerId, String commenterId, String postId, String commentText) {
        try {
            String message = "Someone commented on your post: " + commentText.substring(0, Math.min(30, commentText.length()));
            Notification notification = new Notification();
            notification.setUserId(postOwnerId);
            notification.setMessage(message);
            notification.setType("COMMENT");
            notification.setRelatedUserId(commenterId);
            notification.setRelatedPostId(postId);
            notification.setNotificationId(UUID.randomUUID().toString());
            notification.setCreatedAt(System.currentTimeMillis());
            notification.setCreatedAtDateTime(LocalDateTime.now());
//saves in db
            notificationDAO.create(notification);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public List<Notification> getUserNotifications(String userId) {
        return notificationDAO.getUserNotifications(userId);
    }

    
    public Notification createNotification(String userId, String message, String type) {
        Notification notification = new Notification(userId, message, type);
        notificationDAO.create(notification);
        return notification;
    }
}
