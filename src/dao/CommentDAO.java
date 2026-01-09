package dao;

import model.Comment;
import org.bson.Document;
import com.mongodb.client.MongoCursor;
import java.util.ArrayList;
import java.util.List;

public class CommentDAO extends BaseDAO<Comment> implements IComR {

    public CommentDAO() {
        super("comments");
    }

    // CREATE COMMENT
    @Override
    public String create(Comment comment) {
        String id = java.util.UUID.randomUUID().toString();
        comment.setId(id);

        Document doc = commentToDocument(comment);
        collection.insertOne(doc);

        return id;
    }

    // FIND COMMENT BY ID
    @Override
    public Comment findById(String id) {
        Document query = new Document("_id", id);
        Document doc = collection.find(query).first();

        if (doc != null) {
            return documentToComment(doc);
        }
        return null;
    }

    // GET ALL COMMENTS
    @Override
    public List<Comment> getAll() {
        List<Comment> comments = new ArrayList<>();

        MongoCursor<Document> cursor = collection.find().iterator();
        while (cursor.hasNext()) {
            comments.add(documentToComment(cursor.next()));
        }
        cursor.close();

        return comments;
    }

    // UPDATE COMMENT
    @Override
    public boolean update(Comment comment) {
        Document query = new Document("_id", comment.getId());
        Document updatedDoc = commentToDocument(comment);

        long modified = collection.replaceOne(query, updatedDoc).getModifiedCount();
        return modified > 0;
    }

    // DELETE COMMENT
    @Override
    public boolean delete(String id) {
        Document query = new Document("_id", id);
        long deleted = collection.deleteOne(query).getDeletedCount();
        return deleted > 0;
    }

    // GET COMMENTS BY POST ID
    @Override
    public List<Comment> getCommentsByPostId(String postId) {
        List<Comment> comments = new ArrayList<>();

        Document query = new Document("postId", postId);
        MongoCursor<Document> cursor = collection.find(query).iterator();

        while (cursor.hasNext()) {
            comments.add(documentToComment(cursor.next()));
        }
        cursor.close();

        return comments;
    }

    // GET COMMENTS BY USER ID
    @Override
    public List<Comment> getCommentsByUserId(String userId) {
        List<Comment> comments = new ArrayList<>();

        Document query = new Document("userId", userId);
        MongoCursor<Document> cursor = collection.find(query).iterator();

        while (cursor.hasNext()) {
            comments.add(documentToComment(cursor.next()));
        }
        cursor.close();

        return comments;
    }

    // JAVA → MONGODB
    private Document commentToDocument(Comment comment) {
        Document doc = new Document();
        doc.append("_id", comment.getId());
        doc.append("postId", comment.getPostId());
        doc.append("userId", comment.getUserId());
        doc.append("text", comment.getText());
        doc.append("createdAt", comment.getCreatedAt());
        doc.append("updatedAt", comment.getUpdatedAt());
        return doc;
    }

    // MONGODB → JAVA
    private Comment documentToComment(Document doc) {
        Comment comment = new Comment();

        comment.setId(doc.getString("_id"));
        comment.setPostId(doc.getString("postId"));
        comment.setUserId(doc.getString("userId"));
        comment.setText(doc.getString("text"));
        java.util.Date createdAtDate = doc.getDate("createdAt");
        if (createdAtDate != null) {
            comment.setCreatedAt(
                    createdAtDate.toInstant()
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDateTime());
        }

        java.util.Date updatedAtDate = doc.getDate("updatedAt");
        if (updatedAtDate != null) {
            comment.setUpdatedAt(
                    updatedAtDate.toInstant()
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDateTime());
        }
        return comment;
    }
}
