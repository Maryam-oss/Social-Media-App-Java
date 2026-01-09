package dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import static com.mongodb.client.model.Filters.eq;

public class FollowDAO {

    private final MongoCollection<Document> userCollection;

    public FollowDAO(MongoDatabase userDB) {
        this.userCollection = userDB.getCollection("users");
    }

    private Document getUserDoc(String userId) {
        return userCollection.find(eq("id", userId)).first();
    }

    public void followUser(String followerId, String targetId) {
        userCollection.updateOne(
                eq("id", followerId),
                new Document("$addToSet", new Document("following", targetId)));

        userCollection.updateOne(
                eq("id", targetId),
                new Document("$addToSet", new Document("followers", followerId)));
    }

    public void unfollowUser(String followerId, String targetId) {
        userCollection.updateOne(
                eq("id", followerId),
                new Document("$pull", new Document("following", targetId)));

        userCollection.updateOne(
                eq("id", targetId),
                new Document("$pull", new Document("followers", followerId)));
    }

    public void addPendingRequest(String targetId, String followerId) {
        userCollection.updateOne(
                eq("id", targetId),
                new Document("$addToSet", new Document("pendingFollowRequests", followerId)));
    }

    public void removePendingRequest(String targetId, String followerId) {
        userCollection.updateOne(
                eq("id", targetId),
                new Document("$pull", new Document("pendingFollowRequests", followerId)));
    }

    public Document getRealtimeUserData(String userId) {
        return getUserDoc(userId);
    }
}
