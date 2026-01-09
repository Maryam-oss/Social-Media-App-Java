package dao;

import model.Comment;
import java.util.List;

public interface IComR {
    /**
     * Create a new comment
     */
    String create(Comment comment);

    /**
     * Find comment by ID
     */
    Comment findById(String id);

    /**
     * Get all comments
     */
    List<Comment> getAll();

    /**
     * Update a comment
     */
    boolean update(Comment comment);

    /**
     * Delete a comment
     */
    boolean delete(String id);

    /**
     * Get comments for a post
     */
    List<Comment> getCommentsByPostId(String postId);

    /**
     * Get comments by user
     */
    List<Comment> getCommentsByUserId(String userId);
}
