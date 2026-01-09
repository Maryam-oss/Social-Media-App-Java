package model;

import java.time.*;
import java.util.*;

public class Notification implements Displayable {
    private String notificationId;
    private String userId;
    private String message;
    private long createdAt;
    private LocalDateTime createdAtDateTime;

    private String type;
    private String relatedUserId;
    private String relatedPostId;
    private String relatedCommentId;

    public Notification(String userId, String message, String type) {
        this.notificationId = UUID.randomUUID().toString();
        this.userId = userId;
        this.message = message;
        this.type = type;
        this.createdAt = System.currentTimeMillis();
        this.createdAtDateTime = LocalDateTime.now();

    }

    public Notification() {
        this.notificationId = UUID.randomUUID().toString();
        this.type = "general";
        this.createdAt = System.currentTimeMillis();
        this.createdAtDateTime = LocalDateTime.now();

    }

    public void display() {
        System.out.println("[" + type + "] " + message);
    }

    
    public String getSummary() {
        return message;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public String getUserId() {
        return userId;
    }

    public String getMessage() {
        return message;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getCreatedAtDateTime() {
        return createdAtDateTime;
    }

    public String getType() {
        return type;
    }

    public String getRelatedUserId() {
        return relatedUserId;
    }

    public String getRelatedPostId() {
        return relatedPostId;
    }

    public String getRelatedCommentId() {
        return relatedCommentId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public void setCreatedAtDateTime(LocalDateTime createdAtDateTime) {
        this.createdAtDateTime = createdAtDateTime;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setRelatedUserId(String relatedUserId) {
        this.relatedUserId = relatedUserId;
    }

    public void setRelatedPostId(String relatedPostId) {
        this.relatedPostId = relatedPostId;
    }

    public void setRelatedCommentId(String relatedCommentId) {
        this.relatedCommentId = relatedCommentId;
    }

}
