package model;

import java.time.LocalDateTime;

public class Comment implements Displayable {
    private String id;
    private String userId;
    private String postId;
    private String text;
    private long timestamp;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Comment() {
        this.id = java.util.UUID.randomUUID().toString();
        this.timestamp = System.currentTimeMillis();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Constructor for creating a new comment
    public Comment(String userId, String text) {
        this();
        this.userId = userId;
        this.text = text;
    }

    // Constructor for DAO / full initialization (if needed)
    public Comment(String id, String userId, String postId, String text, long timestamp) {
        this();
        this.id = id;
        this.userId = userId;
        this.postId = postId;
        this.text = text;
        this.timestamp = timestamp;
    }

    // Displayable implementation
    @Override
    public void display() {
        System.out.println(userId + ": " + text);
    }

    @Override
    public String getSummary() {
        return userId + ": " + text;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getPostId() {
        return postId;
    }

    public String getText() {
        return text;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // Setters (for DAO or updates)
    public void setId(String id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
