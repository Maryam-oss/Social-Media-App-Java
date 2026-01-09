package dao;

import model.Message;
import org.bson.Document;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessageDAO extends BaseDAO<Message> implements IMsgR {

    public MessageDAO() {
        super("messages");
    }

    @Override
    public String create(Message message) {
        Document doc = messageToDocument(message);
        collection.insertOne(doc);
        return message.getId();
    }

    @Override
    public Message findById(String id) {
        Document query = new Document("id", id);
        Document doc = collection.find(query).first();

        if (doc != null) {
            return documentToMessage(doc);
        }
        return null;
    }

    @Override
    public List<Message> getAll() {
        List<Message> messages = new ArrayList<>();

        for (Document doc : collection.find()) {
            messages.add(documentToMessage(doc));
        }

        return messages;
    }

    @Override
    public boolean update(Message message) {
        Document query = new Document("id", message.getId());
        Document updatedDoc = messageToDocument(message);

        long modifiedCount = collection.replaceOne(query, updatedDoc).getModifiedCount();
        return modifiedCount > 0;
    }

    @Override
    public boolean delete(String id) {
        Document query = new Document("id", id);
        long deletedCount = collection.deleteOne(query).getDeletedCount();
        return deletedCount > 0;
    }

    public List<Message> getConversation(String userId1, String userId2) {
        List<Message> messages = new ArrayList<>();

        Document firstCase = new Document("senderId", userId1)
                .append("receiverId", userId2);

        Document secondCase = new Document("senderId", userId2)
                .append("receiverId", userId1);

        Document query = new Document("$or", Arrays.asList(firstCase, secondCase));

        for (Document doc : collection.find(query).sort(new Document("timestamp", 1))) {
            messages.add(documentToMessage(doc));
        }

        return messages;
    }

    public void deleteForUser(String messageId, String userId) {
        Document query = new Document("id", messageId);
        Document update = new Document("$addToSet", new Document("deletedBy", userId));

        collection.updateOne(query, update);
    }

    public List<Message> getGroupMessages(String groupId) {
        List<Message> messages = new ArrayList<>();

        Document query = new Document("receiverId", groupId);

        for (Document doc : collection.find(query)) {
            messages.add(documentToMessage(doc));
        }

        return messages;
    }

    private Document messageToDocument(Message message) {
        Document doc = new Document();
        doc.append("id", message.getId());
        doc.append("senderId", message.getSenderId());
        doc.append("receiverId", message.getReceiverId());
        doc.append("content", message.getContent());
        doc.append("timestamp", message.getTimestamp());
        doc.append("deletedBy", message.getDeletedBy());
        return doc;
    }

    private Message documentToMessage(Document doc) {
        Message message = new Message(
                doc.getString("senderId"),
                doc.getString("receiverId"),
                doc.getString("content"));

        message.setId(doc.getString("id"));
        message.setTimestamp(doc.getLong("timestamp"));

        List<String> deletedBy = doc.getList("deletedBy", String.class);
        if (deletedBy != null) {
            message.setDeletedBy(deletedBy);
        }

        return message;
    }
}
