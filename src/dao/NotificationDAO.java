package dao;

import model.Notification;
import org.bson.Document;
import org.bson.conversions.Bson;
import com.mongodb.client.FindIterable;
import com.mongodb.client.result.DeleteResult;


import java.util.*;
// callsthe parent constructor in base dao with notifications
public class NotificationDAO extends BaseDAO<Notification> implements INotifR {
    public NotificationDAO() {
        super("notifications");
    }

    public String create(Notification notification) {
        try {
            Document doc = notificationToDocument(notification);
            //notif to doc for db
            collection.insertOne(doc);

            String id = notification.getNotificationId();
            return id;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public Notification findById(String id) {
        try {
           //query 
            Bson filter = new Document("notificationId", id);
            FindIterable<Document> iter = collection.find(filter);
            Document doc = iter.first();

            if (doc != null) {
                Notification notification = documentToNotification(doc);
                return notification;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    
    public List<Notification> getAll() {
        List<Notification> notifications = new ArrayList<>();
      //will get all of them
        FindIterable<Document> iter = collection.find();

        for (Document doc : iter) {
            Notification notification = documentToNotification(doc);
            notifications.add(notification);
        }

        return notifications;
    }

    public boolean update(Notification notification) {
        return false;
    }

    public boolean delete(String id) {
        try {
            Document query = new Document("notificationId", id);
            com.mongodb.client.result.DeleteResult result = collection.deleteOne(query);
            //returns a delete obj 
            long deletedCount = result.getDeletedCount();
            return deletedCount > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
//to store in db
    private Document notificationToDocument(Notification notification) {
        Document doc = new Document();

        String id = notification.getNotificationId();
        doc.append("notificationId", id);

        String userId = notification.getUserId();
        doc.append("userId", userId);

        String message = notification.getMessage();
        doc.append("message", message);

        long createdAt = notification.getCreatedAt();
        doc.append("createdAt", createdAt);

        String type = notification.getType();
        doc.append("type", type);

        String relatedUserId = notification.getRelatedUserId();
        doc.append("relatedUserId", relatedUserId);

        String relatedPostId = notification.getRelatedPostId();
        doc.append("relatedPostId", relatedPostId);

        String relatedCommentId = notification.getRelatedCommentId();
        doc.append("relatedCommentId", relatedCommentId);

        return doc;
    }

    private Notification documentToNotification(Document doc) {
        String userId = doc.getString("userId");
        String message = doc.getString("message");
        String type = doc.getString("type");

        Notification notification = new Notification(userId, message, type);

        String notificationId = doc.getString("notificationId");
        notification.setNotificationId(notificationId);

        Long createdAt = doc.getLong("createdAt");
        notification.setCreatedAt(createdAt);

        String relatedUserId = doc.getString("relatedUserId");
        notification.setRelatedUserId(relatedUserId);

        String relatedPostId = doc.getString("relatedPostId");
        notification.setRelatedPostId(relatedPostId);

        String relatedCommentId = doc.getString("relatedCommentId");
        notification.setRelatedCommentId(relatedCommentId);

        return notification;
    }

    //for a specific user
    public List<Notification> getUserNotifications(String userId) {
        List<Notification> list = new ArrayList<>();

        Document query = new Document("userId", userId);
        FindIterable<Document> iter = collection.find(query);

        for (Document doc : iter) {
            Notification notification = documentToNotification(doc);
            list.add(notification);
        }

        return list;
    }

}
