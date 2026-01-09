package model;

import java.io.Serializable;
import java.time.*;

public class Activity implements Serializable, Displayable {
    private String id;
    private String userId;
    private String activityType;
    private String targetUserId;
    private String targetPostId;
    private String targetCommentId;
    private LocalDateTime timestamp;
    private String description;

    public Activity() {
    }

    public Activity(String userId, String activityType, LocalDateTime timestamp) {
        this.userId = userId;
        this.activityType = activityType;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public String getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(String targetUserId) {
        this.targetUserId = targetUserId;
    }

    public String getTargetPostId() {
        return targetPostId;
    }

    public void setTargetPostId(String targetPostId) {
        this.targetPostId = targetPostId;
    }

    public String getTargetCommentId() {
        return targetCommentId;
    }

    public void setTargetCommentId(String targetCommentId) {
        this.targetCommentId = targetCommentId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void display() {
        System.out.println("Activity: " + activityType + " - " + description + " at " + timestamp);
    }

    
    public String getSummary() {
        return activityType + ": " + description;
    }

    
    public String toString() {
        return "Activity{" +"id='" + id + '\'' + ", userId='" + userId + '\'' + ", activityType='" + activityType + '\'' +
 ", timestamp=" + timestamp +", description='" + description + '\'' +
                '}';
    }
}
