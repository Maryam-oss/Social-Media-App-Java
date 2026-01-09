package dao;

import model.Group;
import org.bson.Document;
import java.util.ArrayList;
import java.util.List;

public class GroupDAO extends BaseDAO<Group> {

    public GroupDAO() {
        super("groups"); // Connect to "groups" collection
    }

    // CREATE GROUP
    @Override
    public String create(Group group) {
        Document doc = groupToDocument(group);
        collection.insertOne(doc);
        return group.getId();
    }

    // FIND GROUP BY ID
    @Override
    public Group findById(String id) {
        Document query = new Document("id", id);
        Document doc = collection.find(query).first();

        if (doc != null) {
            return documentToGroup(doc);
        }
        return null;
    }

    // GET ALL GROUPS
    @Override
    public List<Group> getAll() {
        List<Group> groups = new ArrayList<>();

        for (Document doc : collection.find()) {
            groups.add(documentToGroup(doc));
        }

        return groups;
    }

    // UPDATE GROUP
    @Override
    public boolean update(Group group) {
        Document query = new Document("id", group.getId());
        Document updatedDoc = groupToDocument(group);

        long modifiedCount = collection.replaceOne(query, updatedDoc).getModifiedCount();
        return modifiedCount > 0;
    }

    // DELETE GROUP
    @Override
    public boolean delete(String id) {
        Document query = new Document("id", id);
        long deletedCount = collection.deleteOne(query).getDeletedCount();
        return deletedCount > 0;
    }

    // GET GROUPS FOR USER
    public List<Group> getGroupsForUser(String userId) {
        List<Group> groups = new ArrayList<>();

        Document query = new Document("memberIds", userId);

        for (Document doc : collection.find(query)) {
            groups.add(documentToGroup(doc));
        }

        return groups;
    }

    // JAVA → MONGODB
    private Document groupToDocument(Group group) {
        Document doc = new Document();
        doc.append("id", group.getId());
        doc.append("name", group.getName());
        doc.append("ownerId", group.getOwnerId());
        doc.append("memberIds", group.getMemberIds());
        doc.append("createdAt", group.getCreatedAt());
        return doc;
    }

    // MONGODB → JAVA
    private Group documentToGroup(Document doc) {
        Group group = new Group();

        group.setId(doc.getString("id"));
        group.setName(doc.getString("name"));
        group.setOwnerId(doc.getString("ownerId"));
        group.setCreatedAt(doc.getLong("createdAt"));

        List<String> members = doc.getList("memberIds", String.class);
        if (members == null) {
            members = new ArrayList<>();
        }
        group.setMemberIds(members);

        return group;
    }
}
