package main.java.com.simra.app.csvimporter.services;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.log4j.Logger;
import org.bson.Document;

import java.util.Arrays;

public class DBService {
    private static final Logger logger = Logger.getLogger(DBService.class);

    private MongoClient dbClient;
    private MongoDatabase rideDatabase;
    private MongoCollection<Document> collection;
    private String host = "localhost";
    private int port = 27017;
    private String database = "SimraDb";


    public void DbRideConnect() {
        this.readProperties();
        this.collection = this.rideDatabase.getCollection(ConfigService.config.getProperty("db.ridesCollection", "rides"));
    }

    public void DbProfileConnect() {
        this.readProperties();
        this.collection = this.rideDatabase.getCollection(ConfigService.config.getProperty("db.profilesCollection", "profiles"));
    }

    public MongoCollection<Document> getIncidentCollection() {
        return this.rideDatabase.getCollection(ConfigService.config.getProperty("db.incidentsCollection", "incidents"));
    }

    private void readProperties() {

        this.host = ConfigService.config.getProperty("db.host", "localhost");
        this.port = Integer.parseInt(ConfigService.config.getProperty("db.port", "27017"));
        String user = "";
        String password = "";
        user = ConfigService.config.getProperty("db.user", "");
        password = ConfigService.config.getProperty("db.password", "");
        this.database = ConfigService.config.getProperty("db.database", "SimraDb");

        MongoCredential credential = MongoCredential.createCredential(user, this.database, password.toCharArray());
        if (!user.isEmpty() && !password.isEmpty())
            this.dbClient = new MongoClient(new ServerAddress(this.host, this.port), Arrays.asList(credential));
        else
            this.dbClient = new MongoClient(this.host, this.port);
        this.rideDatabase = this.dbClient.getDatabase(this.database);

    }


    public MongoClient getDbClient() {
        return dbClient;
    }

    public void setDbClient(MongoClient dbClient) {
        this.dbClient = dbClient;
    }

    public MongoDatabase getRideDatabase() {
        return rideDatabase;
    }

    public void setRideDatabase(MongoDatabase rideDatabase) {
        this.rideDatabase = rideDatabase;
    }

    public MongoCollection<Document> getCollection() {
        return collection;
    }

    public void setRides(MongoCollection<Document> collection) {
        this.collection = collection;
    }

}
