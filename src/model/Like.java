package model;

public class Like implements Displayable {
    private String id;
    private String userId;
    private String postId;
    private long timestamp;

    // Constructor
    public Like(String userId, String postId) {
        this.id = java.util.UUID.randomUUID().toString();
        this.userId = userId;
        this.postId = postId;
        this.timestamp = System.currentTimeMillis();
    }

    // Display methods
    @Override
    public void display() {
        System.out.println("Like from " + userId);
    }

    @Override
    public String getSummary() {
        return userId + " liked this";
    }

    // Getters
    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getPostId() { return postId; }
    public long getTimestamp() { return timestamp; }

    // Setters (needed for DAO)
    public void setId(String id) { this.id = id; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setPostId(String postId) { this.postId = postId; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
