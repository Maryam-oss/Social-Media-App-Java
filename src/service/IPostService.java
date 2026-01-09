package service;

import model.Post;
import java.util.List;

/**
 * Interface for Post Service operations
 * Provides abstraction for post-related business logic
 */
public interface IPostService {

    // Post CRUD
    Post createPost(String userId, String caption, String imageUrl);

    Post getPostById(String postId);

    List<Post> getPostsByUserId(String userId);

    boolean updatePost(String postId, String caption, String imageUrl);

    boolean deletePost(String postId);

    // Feed
    List<Post> getFeed(String userId);

    List<Post> getFollowingFeed(String userId);

    // Like Operations
    boolean likePost(String userId, String postId);

    boolean unlikePost(String userId, String postId);

    int getLikeCount(String postId);

    // Comment Operations
    boolean addComment(String userId, String postId, String text);

    int getCommentCount(String postId);

    boolean updateComment(String commentId, String newText);

    boolean deleteComment(String commentId);

    // Search & Explore
    List<Post> searchPosts(String query);

    List<Post> getPopularPosts();
}
