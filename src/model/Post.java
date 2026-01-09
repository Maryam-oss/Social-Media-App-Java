package model;

import java.util.*;
import java.time.LocalDateTime;

public class Post implements Displayable {
    private String id;
    private String userId;
    private String caption;
    private String imageUrl; // Kept for backward compatibility
    private List<String> imageUrls = new ArrayList<>(); // New: List of images
    private List<String> videoUrls = new ArrayList<>(); // New: List of videos
    private long timestamp;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<Like> likes = new ArrayList<>();
    private List<Comment> comments = new ArrayList<>();
    private String privacyLevel; // PUBLIC, FRIENDS_ONLY, PRIVATE
    private List<String> allowedViewers = new ArrayList<>(); // For PRIVATE posts
    private List<String> taggedUserIds = new ArrayList<>(); // User tagging
    private List<String> hashtags = new ArrayList<>(); // Hashtags in post

    public Post() {
        this.id = UUID.randomUUID().toString();
        this.timestamp = System.currentTimeMillis();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.imageUrls = new ArrayList<>();
        this.videoUrls = new ArrayList<>();
        this.likes = new ArrayList<>();
        this.comments = new ArrayList<>();
        this.privacyLevel = "PUBLIC";
        this.allowedViewers = new ArrayList<>();
        this.taggedUserIds = new ArrayList<>();
        this.hashtags = new ArrayList<>();
    }

    public Post(String userId, String caption, String imageUrl) {
        this.id = UUID.randomUUID().toString();
        this.userId = userId;
        this.caption = caption;
        this.caption = caption;
        this.imageUrl = imageUrl;
        this.imageUrls = new ArrayList<>();
        this.videoUrls = new ArrayList<>();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            this.imageUrls.add(imageUrl);
        }
        this.timestamp = System.currentTimeMillis();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.likes = new ArrayList<>();
        this.comments = new ArrayList<>();
        this.privacyLevel = "PUBLIC"; // Default to public
        this.allowedViewers = new ArrayList<>();
        this.taggedUserIds = new ArrayList<>();
        this.hashtags = new ArrayList<>();
    }

    @Override
    public void display() {
        System.out.println("Post: " + caption + " | Likes: " + likes.size() + " | Comments: " + comments.size());
    }

    @Override
    public String getSummary() {
        return caption + " (" + likes.size() + " likes, " + comments.size() + " comments)";
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getCaption() {
        return caption;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public List<String> getVideoUrls() {
        return videoUrls;
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

    public List<Like> getLikes() {
        return likes;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public String getPrivacyLevel() {
        return privacyLevel;
    }

    public List<String> getAllowedViewers() {
        return allowedViewers;
    }

    public List<String> getTaggedUserIds() {
        return taggedUserIds;
    }

    public List<String> getHashtags() {
        return hashtags;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
        // Update the main image url to be the first one if available
        if (imageUrls != null && !imageUrls.isEmpty()) {
            this.imageUrl = imageUrls.get(0);
        }
    }

    public void setVideoUrls(List<String> videoUrls) {
        this.videoUrls = videoUrls;
    }

    public void addImageUrl(String path) {
        if (this.imageUrls == null) {
            this.imageUrls = new ArrayList<>();
        }
        this.imageUrls.add(path);
        // If this is the first image, set it as main image too
        if (this.imageUrl == null || this.imageUrl.isEmpty()) {
            this.imageUrl = path;
        }
    }

    public void addVideoUrl(String path) {
        if (this.videoUrls == null) {
            this.videoUrls = new ArrayList<>();
        }
        this.videoUrls.add(path);
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public void setPrivacyLevel(String privacyLevel) {
        this.privacyLevel = privacyLevel;
    }

    public void setAllowedViewers(List<String> allowedViewers) {
        this.allowedViewers = allowedViewers;
    }

    public void setTaggedUserIds(List<String> taggedUserIds) {
        this.taggedUserIds = taggedUserIds;
    }

    public void setHashtags(List<String> hashtags) {
        this.hashtags = hashtags;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public boolean addLike(Like like) {
        for (Like l : likes) {
            if (l.getUserId().equals(like.getUserId()))
                return false; // Already liked
        }
        likes.add(like);
        return true;
    }

    public boolean removeLike(String userId) {
        Iterator<Like> iterator = likes.iterator();
        boolean removed = false;
        while (iterator.hasNext()) {
            Like l = iterator.next();
            if (l.getUserId().equals(userId)) {
                iterator.remove();
                removed = true;
            }
        }
        return removed;
    }

    public void addComment(Comment comment) {
        comments.add(comment);
    }

    public boolean removeComment(String commentId) {
        Iterator<Comment> iterator = comments.iterator();
        boolean removed = false;
        while (iterator.hasNext()) {
            Comment c = iterator.next();
            if (c.getId().equals(commentId)) {
                iterator.remove();
                removed = true;
            }
        }
        return removed;
    }

    public void tagUser(String userId) {
        if (!taggedUserIds.contains(userId)) {
            taggedUserIds.add(userId);
        }
    }

    public void untagUser(String userId) {
        taggedUserIds.remove(userId);
    }

    public void addHashtag(String hashtag) {
        if (!hashtags.contains(hashtag)) {
            hashtags.add(hashtag);
        }
    }

    public boolean isViewableBy(String userId) {
        if (this.userId.equals(userId)) {
            return true; // Post owner can always see
        }

        if ("PUBLIC".equals(privacyLevel)) {
            return true;
        }

        if ("PRIVATE".equals(privacyLevel)) {
            return allowedViewers.contains(userId);
        }

        // FRIENDS_ONLY: would require friendship check from UserService
        return false;
    }
}
