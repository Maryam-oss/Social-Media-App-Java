package model;

import java.util.Date;

public class Message implements Displayable {
    private String id;
    private String senderId;
    private String receiverId;
    private String content;
    private long timestamp;
    private java.util.List<String> deletedBy;

    public Message(String senderId, String receiverId, String content) {
        this.id = java.util.UUID.randomUUID().toString();
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.timestamp = System.currentTimeMillis();
        this.deletedBy = new java.util.ArrayList<>();
    }

    @Override
    public void display() {
        System.out.println(senderId + ": " + content);
    }

    @Override
    public String getSummary() {
        return senderId + ": " + content;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public String getContent() {
        return content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    // Setters (needed for DAO to update object from DB)
    public void setId(String id) {
        this.id = id;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public java.util.List<String> getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(java.util.List<String> deletedBy) {
        this.deletedBy = deletedBy;
    }
}
