package dao;

import model.Post;
import model.Like;

import org.bson.Document;
import java.util.*;

public class PostDAO extends BaseDAO<Post> implements IPostR {

    public PostDAO() {
        super("posts");
    }

    @Override
    public String create(Post post) {
        Document doc = postToDocument(post);
        collection.insertOne(doc);
        return post.getId();
    }

    @Override
    public Post findById(String id) {
        Document doc = collection.find(new Document("id", id)).first();
        return doc != null ? documentToPost(doc) : null;
    }

    @Override
    public List<Post> getAll() {
        List<Post> posts = new ArrayList<>();
        for (Document doc : collection.find().sort(new Document("timestamp", -1))) {
            posts.add(documentToPost(doc));
        }
        return posts;
    }

    @Override
    public boolean update(Post post) {
        Document doc = postToDocument(post);
        return collection.replaceOne(new Document("id", post.getId()), doc).getModifiedCount() > 0;
    }

    @Override
    public boolean delete(String id) {
        return collection.deleteOne(new Document("id", id)).getDeletedCount() > 0;
    }

    public List<Post> getFeedForUser(List<String> followingList, List<String> blockedUsers) {
        List<Post> feed = new ArrayList<>();
        if (!followingList.isEmpty()) {
            Document query = new Document("userId", new Document("$in", followingList));

            if (blockedUsers != null && !blockedUsers.isEmpty()) {
                query.append("userId", new Document("$in", followingList).append("$nin", blockedUsers));
            }

            for (Document doc : collection.find(query).sort(new Document("timestamp", -1))) {
                feed.add(documentToPost(doc));
            }
        }
        return feed;
    }

    public List<Post> getPostsByUser(String userId) {
        List<Post> posts = new ArrayList<>();
        for (Document doc : collection.find(new Document("userId", userId)).sort(new Document("timestamp", -1))) {
            posts.add(documentToPost(doc));
        }
        return posts;
    }

    // ------------------- Convert Post to Document -------------------
    private Document postToDocument(Post post) {
        List<Document> likesDocs = new ArrayList<>();
        for (Like like : post.getLikes()) {
            likesDocs.add(new Document("id", like.getId())
                    .append("userId", like.getUserId())
                    .append("postId", like.getPostId())
                    .append("timestamp", like.getTimestamp()));
        }

        // Comments are now stored in a separate collection

        return new Document()
                .append("id", post.getId())
                .append("userId", post.getUserId())
                .append("caption", post.getCaption())
                .append("imageUrl", post.getImageUrl())
                .append("imageUrls", post.getImageUrls()) // Save list of images
                .append("videoUrls", post.getVideoUrls()) // Save list of videos
                .append("timestamp", post.getTimestamp())
                .append("likes", likesDocs);
    }

    // ------------------- Convert Document to Post -------------------
    private Post documentToPost(Document doc) {
        Post post = new Post(
                doc.getString("userId"),
                doc.getString("caption"),
                doc.getString("imageUrl"));
        post.setId(doc.getString("id"));
        post.setTimestamp(doc.getLong("timestamp"));

        // Load multiple images
        List<String> loadedImageUrls = doc.getList("imageUrls", String.class);
        if (loadedImageUrls != null) {
            post.setImageUrls(loadedImageUrls);
        } else {
            // Logic for old posts that only have one image
            String singleImage = doc.getString("imageUrl");
            if (singleImage != null && !singleImage.isEmpty()) {
                post.addImageUrl(singleImage);
            }
        }

        // Load multiple videos
        List<String> loadedVideoUrls = doc.getList("videoUrls", String.class);
        if (loadedVideoUrls != null) {
            post.setVideoUrls(loadedVideoUrls);
        }

        List<Document> likesDocs = doc.getList("likes", Document.class);
        if (likesDocs != null) {
            for (Document likeDoc : likesDocs) {
                Like like = new Like(likeDoc.getString("userId"), likeDoc.getString("postId"));
                like.setId(likeDoc.getString("id"));
                like.setTimestamp(likeDoc.getLong("timestamp"));
                post.getLikes().add(like);
            }
        }

        // Comments are loaded separately via CommentService

        return post;
    }

    @Override
    public List<Post> getFeedPosts(List<String> followingList, List<String> blockedUsers) {
        Document query = new Document("userId", new Document("$in", followingList));
        if (blockedUsers != null && !blockedUsers.isEmpty()) {
            query.append("userId", new Document("$in", followingList).append("$nin", blockedUsers));
        }
        List<Post> posts = new ArrayList<>();
        for (Document doc : collection.find(query).sort(new Document("timestamp", -1))) {
            posts.add(documentToPost(doc));
        }
        return posts;
    }

    @Override
    public List<Post> getPostsByUserId(String userId) {
        List<Post> posts = new ArrayList<>();
        for (Document doc : collection.find(new Document("userId", userId)).sort(new Document("timestamp", -1))) {
            posts.add(documentToPost(doc));
        }
        return posts;
    }
}
