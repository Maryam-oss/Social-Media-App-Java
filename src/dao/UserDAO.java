package dao;

import model.User;
import org.bson.Document;
import com.mongodb.client.MongoCursor; // Import MongoCursor for manual iteration

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserDAO extends BaseDAO<User> implements IUserR {

    public UserDAO() {
        super("users");
    }

    @Override
    public String create(User user) {
        Document doc = userToDocument(user);
        collection.insertOne(doc);
        return user.getId();
    }

    @Override
    public User findById(String id) {
        Document doc = collection.find(new Document("id", id)).first();
        if (doc != null) {
            return documentToUser(doc);
        } else {
            return null;
        }
    }

    public User findByUsername(String username) {
        Document doc = collection.find(new Document("username", username)).first();
        if (doc != null) {
            return documentToUser(doc);
        } else {
            return null;
        }
    }

    @Override
    public List<User> getAll() {
        List<User> users = new ArrayList<>();
        MongoCursor<Document> cursor = collection.find().iterator();
        try {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                users.add(documentToUser(doc));
            }
        } finally {
            cursor.close();
        }
        return users;
    }

    @Override
    public boolean update(User user) {
        Document doc = userToDocument(user);
        long modifiedCount = collection.replaceOne(new Document("id", user.getId()), doc).getModifiedCount();
        if (modifiedCount > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(String id) {
        long deletedCount = collection.deleteOne(new Document("id", id)).getDeletedCount();
        if (deletedCount > 0) {
            return true;
        }
        return false;
    }

    public List<User> searchByUsername(String query) {
        List<User> results = new ArrayList<>();
        
        MongoCursor<Document> cursor = collection.find().iterator();
        try {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                String username = doc.getString("username");
                if (username != null && query != null) {
                    // Manual case-insensitive contains check
                    if (username.toLowerCase().contains(query.toLowerCase())) {
                        results.add(documentToUser(doc));
                    }
                }
            }
        } finally {
            cursor.close();
        }
        return results;
    }

    // Convert User to Document

    private Document userToDocument(User user) {
        List<String> followersList = user.getFollowers();
        if (followersList == null) {
            followersList = Collections.emptyList();
        }

        List<String> followingList = user.getFollowing();
        if (followingList == null) {
            followingList = Collections.emptyList();
        }

        List<String> pendingList = user.getPendingFollowRequests();
        if (pendingList == null) {
            pendingList = Collections.emptyList();
        }

        List<String> blockedList = user.getBlockedUsers();
        if (blockedList == null) {
            blockedList = Collections.emptyList();
        }

        Document doc = new Document();
        doc.append("id", user.getId());
        doc.append("username", user.getUsername());
        doc.append("email", user.getEmail());
        doc.append("password", user.getPassword());
        doc.append("fullName", user.getFullName());
        doc.append("gender", user.getGender());
        doc.append("dateOfBirth", user.getDateOfBirth());
        doc.append("bio", user.getBio());
        doc.append("profilePic", user.getProfilePic());
        doc.append("isPrivateAccount", user.isPrivateAccount());
        doc.append("followers", followersList);
        doc.append("following", followingList);
        doc.append("pendingFollowRequests", pendingList);
        doc.append("blockedUsers", blockedList);
        return doc;
    }

    // Convert Document to User

    private User documentToUser(Document doc) {
        User user = new User(
                doc.getString("username"),
                doc.getString("email"),
                doc.getString("password"),
                doc.getString("fullName"),
                doc.getString("gender"),
                doc.getString("dateOfBirth"));

        user.setId(doc.getString("id"));

        String bio = doc.getString("bio");
        if (bio != null) {
            user.setBio(bio);
        } else {
            user.setBio("");
        }

        String profilePic = doc.getString("profilePic");
        if (profilePic != null) {
            user.setProfilePic(profilePic);
        } else {
            user.setProfilePic("");
        }

        Boolean isPrivate = doc.getBoolean("isPrivateAccount");
        if (isPrivate != null && isPrivate) {
            user.setPrivateAccount(true);
        } else {
            user.setPrivateAccount(false);
        }

        // Safe initialization of lists manually
        user.getFollowers().clear();
        List<String> followers = doc.getList("followers", String.class);
        if (followers != null) {
            for (String s : followers) {
                user.addFollower(s);
            }
        }

        user.getFollowing().clear();
        List<String> following = doc.getList("following", String.class);
        if (following != null) {
            for (String s : following) {
                user.addFollowing(s);
            }
        }

        user.getPendingFollowRequests().clear();
        List<String> pending = doc.getList("pendingFollowRequests", String.class);
        if (pending != null) {
            for (String s : pending) {
                user.addPendingFollowRequest(s);
            }
        }

        user.getBlockedUsers().clear();
        List<String> blocked = doc.getList("blockedUsers", String.class);
        if (blocked != null) {
            for (String s : blocked) {
                user.blockUser(s);
            }
        }

        return user;
    }

    @Override
    public User findByEmail(String email) {
        Document doc = collection.find(new Document("email", email)).first();
        if (doc != null) {
            return documentToUser(doc);
        }
        return null;
    }
}
