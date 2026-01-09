package service;

import model.Post;
import model.Like;
import model.Comment;
import model.Notification;
import dao.PostDAO;
import dao.NotificationDAO;
import dao.UserDAO;
import model.User;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;

public class PostService implements IPostService {
    private final PostDAO postDAO;
    private final NotificationDAO notificationDAO;
    private final UserDAO userDAO;
    private final CommentService commentService;
    private final NotificationService notificationService;
    private final ActivityService activityService;

    public PostService() {
        this.postDAO = new PostDAO();
        this.notificationDAO = new NotificationDAO();
        this.userDAO = new UserDAO();
        this.commentService = new CommentService();
        this.notificationService = new NotificationService();
        this.activityService = new ActivityService();
    }

    // ----------------------
    // Post CRUD
    // ----------------------
    // ----------------------
    // Post CRUD
    // ----------------------
    public Post createPost(String userId, String caption, String imagePath) {
        List<String> images = new ArrayList<>();
        if (imagePath != null)
            images.add(imagePath);
        return createPost(userId, caption, images, new ArrayList<>(), "PUBLIC", null, null, null);
    }

    public Post createPost(String userId, String caption, List<String> imagePaths) {
        return createPost(userId, caption, imagePaths, new ArrayList<>(), "PUBLIC", null, null, null);
    }

    public Post createPost(String userId, String caption, List<String> imagePaths, List<String> videoPaths) {
        return createPost(userId, caption, imagePaths, videoPaths, "PUBLIC", null, null, null);
    }

    public Post createPost(String userId, String caption, List<String> imagePaths, List<String> videoPaths,
            String privacyLevel,
            List<String> allowedViewers, List<String> taggedUsers, List<String> hashtags) {
        List<String> storedImageIds = new ArrayList<>();
        List<String> storedVideoIds = new ArrayList<>();

        if (imagePaths != null && !imagePaths.isEmpty()) {
            for (String path : imagePaths) {
                String fileId = uploadFileToGridFS(path);
                if (fileId != null) {
                    storedImageIds.add(fileId);
                } else {
                    throw new RuntimeException("Failed to upload image: " + path);
                }
            }
        }

        if (videoPaths != null && !videoPaths.isEmpty()) {
            for (String path : videoPaths) {
                String fileId = uploadFileToGridFS(path);
                if (fileId != null) {
                    storedVideoIds.add(fileId);
                } else {
                    throw new RuntimeException("Failed to upload video: " + path);
                }
            }
        }

        Post post = new Post();
        post.setUserId(userId);
        post.setCaption(caption);
        post.setImageUrls(storedImageIds);
        post.setVideoUrls(storedVideoIds);
        post.setPrivacyLevel(privacyLevel != null ? privacyLevel : "PUBLIC");
        post.setAllowedViewers(allowedViewers != null ? allowedViewers : new ArrayList<>());
        post.setTaggedUserIds(taggedUsers != null ? taggedUsers : new ArrayList<>());
        post.setHashtags(hashtags != null ? hashtags : new ArrayList<>());

        postDAO.create(post); // Save to DB

        // Create Activity
        activityService.createActivity(userId, "POSTED", null, post.getId(), "Created a new post");

        // Notify tagged users
        if (taggedUsers != null && !taggedUsers.isEmpty()) {
            for (String taggedUserId : taggedUsers) {
                notificationService.createNotification(taggedUserId,
                        "You were tagged in a post", "TAG", userId);
            }
        }

        return post; // Return the full Post object
    }

    private String uploadFileToGridFS(String filePath) {
        try {
            java.io.File file = new java.io.File(filePath);
            if (!file.exists()) {
                System.err.println("ERROR: File not found for upload: " + filePath);
                return null;
            }
            java.io.InputStream streamToUploadFrom = new java.io.FileInputStream(file);
            com.mongodb.client.gridfs.GridFSBucket gridFSBucket = dao.DBConnection.getGridFSBucket();
            org.bson.types.ObjectId fileId = gridFSBucket.uploadFromStream(file.getName(), streamToUploadFrom);
            streamToUploadFrom.close();
            return fileId.toHexString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void playVideo(String videoId) throws Exception {
        if (videoId == null || videoId.isEmpty())
            throw new IllegalArgumentException("Video ID is missing");

        java.io.File tempFile = downloadVideo(videoId);

        // Open with default system player
        if (java.awt.Desktop.isDesktopSupported()
                && java.awt.Desktop.getDesktop().isSupported(java.awt.Desktop.Action.OPEN)) {
            java.awt.Desktop.getDesktop().open(tempFile);
        } else {
            throw new UnsupportedOperationException("Desktop/Open not supported on this system.");
        }
    }

    public java.io.File downloadVideo(String videoId) throws Exception {
        // Create a temp file
        java.io.File tempFile = java.io.File.createTempFile("video_" + videoId, ".mp4");
        tempFile.deleteOnExit();

        // Download from GridFS
        com.mongodb.client.gridfs.GridFSBucket gridFSBucket = dao.DBConnection.getGridFSBucket();
        java.io.FileOutputStream fileOutputStream = new java.io.FileOutputStream(tempFile);
        try {
            gridFSBucket.downloadToStream(new org.bson.types.ObjectId(videoId), fileOutputStream);
        } finally {
            fileOutputStream.close();
        }
        return tempFile;
    }

    public boolean updatePost(String postId, String caption, String imageUrl) {
        Post post = postDAO.findById(postId);
        if (post == null)
            return false;
        post.setCaption(caption);
        post.setImageUrl(imageUrl);
        return postDAO.update(post);
    }

    public boolean updateCaption(String postId, String caption) {
        Post post = postDAO.findById(postId);
        if (post == null)
            return false;
        post.setCaption(caption);
        return postDAO.update(post);
    }

    public boolean deletePost(String postId) {
        return postDAO.delete(postId);
    }

    public Post getPostById(String postId) {
        Post post = postDAO.findById(postId);
        hydratePost(post);
        return post;
    }

    public List<Post> getPostsByUser(String userId) {
        List<Post> posts = postDAO.getPostsByUser(userId);
        posts.forEach(this::hydratePost);
        return posts;
    }

    public List<Post> getFeedForUser(List<String> followingList, List<String> blockedUsers) {
        List<Post> posts = postDAO.getFeedForUser(followingList, blockedUsers);
        posts.forEach(this::hydratePost);
        return posts;
    }

    // ----------------------
    // Likes
    // ----------------------
    public boolean addLike(String postId, String userId, String posterUserId) {
        Post post = postDAO.findById(postId);
        if (post == null)
            return false;

        Like like = new Like(userId, postId);
        if (post.addLike(like)) {
            postDAO.update(post);
            if (!userId.equals(posterUserId)) {
                User liker = userDAO.findById(userId);
                String likerName = (liker != null) ? liker.getUsername() : "Unknown";
                Notification notif = new Notification(posterUserId, likerName + " liked your post", "like");
                notificationDAO.create(notif);
            }
            // Create Activity
            activityService.createActivity(userId, "LIKED", posterUserId, postId, "Liked a post");
            return true;
        }
        return false;
    }

    public boolean removeLike(String postId, String userId) {
        Post post = postDAO.findById(postId);
        if (post == null)
            return false;

        if (post.removeLike(userId)) {
            return postDAO.update(post);
        }
        return false;
    }

    // ----------------------
    // Comments
    // ----------------------

    @Override
    public List<Post> getPopularPosts() {
        List<Post> posts = new ArrayList<>(postDAO.getAll());
        posts.sort(new java.util.Comparator<Post>() {
            @Override
            public int compare(Post p1, Post p2) {
                return Integer.compare(p2.getLikes().size(), p1.getLikes().size());
            }
        });

        List<Post> topPosts = new ArrayList<>();
        int limit = Math.min(10, posts.size());
        for (int i = 0; i < limit; i++) {
            topPosts.add(posts.get(i));
        }

        for (Post p : topPosts) {
            hydratePost(p);
        }
        return topPosts;
    }

    @Override
    public List<Post> searchPosts(String query) {
        List<Post> posts = new ArrayList<>();
        String lowerQuery = query.toLowerCase();
        for (Post p : postDAO.getAll()) {
            if (p.getCaption().toLowerCase().contains(lowerQuery)) {
                posts.add(p);
            }
        }
        for (Post p : posts) {
            hydratePost(p);
        }
        return posts;
    }

    @Override
    public int getCommentCount(String postId) {
        return commentService.getCommentCount(postId);
    }

    @Override
    public boolean addComment(String userId, String postId, String text) {
        return addCommentToPost(userId, postId, text);
    }

    @Override
    public int getLikeCount(String postId) {
        Post post = postDAO.findById(postId);
        return post != null ? post.getLikes().size() : 0;
    }

    @Override
    public boolean unlikePost(String userId, String postId) {
        return removeLike(postId, userId);
    }

    @Override
    public boolean likePost(String userId, String postId) {
        Post post = postDAO.findById(postId);
        if (post == null)
            return false;

        return addLike(postId, userId, post.getUserId());
    }

    @Override
    public List<Post> getFollowingFeed(String userId) {
        User user = userDAO.findById(userId);
        if (user == null)
            return new ArrayList<>();

        List<Post> feed = postDAO.getFeedPosts(user.getFollowing(), user.getBlockedUsers());

        // Filter by privacy level and hydrate
        List<Post> result = new ArrayList<>();
        for (Post p : feed) {
            if (canViewPost(p, userId, user)) {
                hydratePost(p);
                result.add(p);
            }
        }
        return result;
    }

    @Override
    public List<Post> getFeed(String userId) {
        User user = userDAO.findById(userId);
        if (user == null)
            return new ArrayList<>();

        List<Post> feed = postDAO.getFeedPosts(user.getFollowing(), user.getBlockedUsers());

        // Filter by privacy level and hydrate
        List<Post> result = new ArrayList<>();
        for (Post p : feed) {
            if (canViewPost(p, userId, user)) {
                hydratePost(p);
                result.add(p);
            }
        }
        return result;
    }

    @Override
    public List<Post> getPostsByUserId(String userId) {
        List<Post> posts = postDAO.getPostsByUserId(userId);
        for (Post p : posts) {
            hydratePost(p);
        }
        return posts;
    }

    private void hydratePost(Post post) {
        if (post != null) {
            List<Comment> comments = commentService.getPostComments(post.getId());
            post.setComments(comments);
        }
    }

    // ===== PRIVACY CONTROLS =====

    public boolean setPostPrivacy(String postId, String privacyLevel) {
        Post post = postDAO.findById(postId);
        if (post == null)
            return false;

        post.setPrivacyLevel(privacyLevel);
        post.setUpdatedAt(LocalDateTime.now());
        return postDAO.update(post);
    }

    public boolean allowUserToViewPrivatePost(String postId, String userId) {
        Post post = postDAO.findById(postId);
        if (post == null)
            return false;

        if ("PRIVATE".equals(post.getPrivacyLevel())) {
            if (!post.getAllowedViewers().contains(userId)) {
                post.getAllowedViewers().add(userId);
                return postDAO.update(post);
            }
        }
        return false;
    }

    public boolean denyUserFromViewingPrivatePost(String postId, String userId) {
        Post post = postDAO.findById(postId);
        if (post == null)
            return false;

        if ("PRIVATE".equals(post.getPrivacyLevel())) {
            return post.getAllowedViewers().remove(userId) && postDAO.update(post);
        }
        return false;
    }

    // ===== TAGGING SYSTEM =====

    public boolean tagUserInPost(String postId, String userId) {
        Post post = postDAO.findById(postId);
        if (post == null)
            return false;

        post.tagUser(userId);
        boolean updated = postDAO.update(post);

        if (updated) {
            notificationService.createNotification(userId, "You were tagged in a post", "TAG", post.getUserId());
        }

        return updated;
    }

    public boolean untagUserFromPost(String postId, String userId) {
        Post post = postDAO.findById(postId);
        if (post == null)
            return false;

        post.untagUser(userId);
        return postDAO.update(post);
    }

    public List<String> getTaggedUsers(String postId) {
        Post post = postDAO.findById(postId);
        return post != null ? post.getTaggedUserIds() : new ArrayList<>();
    }

    // ===== HASHTAG SYSTEM =====

    public boolean addHashtagToPost(String postId, String hashtag) {
        Post post = postDAO.findById(postId);
        if (post == null)
            return false;

        post.addHashtag(hashtag);
        return postDAO.update(post);
    }

    public List<Post> getPostsByHashtag(String hashtag) {
        List<Post> posts = new ArrayList<>();
        for (Post p : postDAO.getAll()) {
            if (p.getHashtags() != null && p.getHashtags().contains(hashtag)) {
                posts.add(p);
            }
        }
        for (Post p : posts) {
            hydratePost(p);
        }
        return posts;
    }

    public List<String> getHashtagsForPost(String postId) {
        Post post = postDAO.findById(postId);
        return post != null ? post.getHashtags() : new ArrayList<>();
    }

    // ===== COMMENT INTEGRATION =====

    public boolean addCommentToPost(String userId, String postId, String text) {
        Post post = postDAO.findById(postId);
        if (post == null)
            return false;

        boolean commentAdded = commentService.addComment(userId, postId, text);

        if (commentAdded) {
            // Trigger comment notification
            notificationService.notifyPostCommented(post.getUserId(), userId, postId, text);
        }

        return commentAdded;
    }

    public List<Comment> getPostComments(String postId) {
        return commentService.getPostComments(postId);
    }

    public boolean updateComment(String commentId, String newText) {
        return commentService.updateComment(commentId, newText);
    }

    public boolean deleteComment(String commentId) {
        return commentService.deleteComment(commentId);
    }

    private boolean canViewPost(Post post, String viewerId, User viewer) {
        if (post.getUserId().equals(viewerId))
            return true;

        switch (post.getPrivacyLevel()) {
            case "PUBLIC":
                return true;
            case "PRIVATE":
                return post.getAllowedViewers().contains(viewerId);
            case "FRIENDS_ONLY":
                // Check if viewer is following the author (assuming following = friends in this
                // context
                // or we need specific friend logic. Usually friends means mutual follow or
                // explicitly in 'friends' list)
                // If the app is follower-based (like Instagram), friends usually implies
                // 'Follows You' or 'Mutual'.
                // Let's assume 'Following' is sufficient for now, or check if viewer's
                // following list contains author.
                return viewer.getFollowing().contains(post.getUserId());
            default:
                return true;
        }
    }
}
