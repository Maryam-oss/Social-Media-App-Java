package dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import java.util.*;

public abstract class BaseDAO<T> {
    protected MongoCollection<Document> collection;

    public BaseDAO(String collectionName) {
        MongoDatabase db = DBConnection.getInstance();
        this.collection = db.getCollection(collectionName);
    }

    public abstract String create(T entity);

    public abstract T findById(String id);

    public abstract List<T> getAll();

    public abstract boolean update(T entity);

    public abstract boolean delete(String id);
}
