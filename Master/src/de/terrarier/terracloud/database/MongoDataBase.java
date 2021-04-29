package de.terrarier.terracloud.database;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import de.terrarier.terracloud.lib.DBSetting;
import org.bson.Document;

public final class MongoDataBase {

    private MongoClient client;
    private MongoDatabase db;

    public void connect(DBSetting dbSetting) {
        if(dbSetting.getPassword() != null && !dbSetting.getPassword().isEmpty()
                && dbSetting.getUser() != null && !dbSetting.getUser().isEmpty()) {
            final String conProps = "mongodb://" + dbSetting.getUser() + ":" + dbSetting.getPassword() +
                    "@" + dbSetting.getHost() + ":" + dbSetting.getPort() + "/?ssl=true&retryWrites=true&w=majority";
            client = new MongoClient(new MongoClientURI(conProps));
        }else {
            client = new MongoClient(dbSetting.getHost(), dbSetting.getPort());
        }
        db = client.getDatabase(dbSetting.getDatabase());
    }

    public void disconnect() {
        client.close();
    }

    public MongoClient getConnection() {
        return client;
    }

    public boolean isConnected() {
        return client != null;
    }

    public MongoCollection<Document> getCollection(String collection) {
        return db.getCollection(collection);
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(Document object, String name) {
        return (T) object.get(name);
    }

}
