package main.java.com.simra.app.csvimporter.dbservice;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.log4j.Logger;
import org.bson.Document;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

public class DBService {
    private static final Logger logger = Logger.getLogger(DBService.class);

    private MongoClient dbClient;
    private MongoDatabase rideDatabase;
    private MongoCollection<Document> collection;
    private String host = "localhost";
    private int port = 27017;
    private String database = "simradb";



    public void DbRideConnect() {
        this.readProperties();
        this.collection = this.rideDatabase.getCollection("rides");
    }

    public void DbProfileConnect() {
        this.readProperties();
        this.collection = this.rideDatabase.getCollection("profiles");
    }

    private void readProperties() {
        try (InputStream input = DBService.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();

            if (input == null) {
                logger.info("Sorry, unable to find config.properties, using default values");
                this.dbClient = new MongoClient(this.host, this.port);
                this.rideDatabase = this.dbClient.getDatabase(this.database);
                return;
            }
            //load a properties file from class path, inside static method
            prop.load(input);
            //get the property value and set for database connection
            this.host = prop.getProperty("db.host", "localhost");
            this.port = Integer.parseInt(prop.getProperty("db.port", "27017"));
            String user = "";
            String password = "";
            user = prop.getProperty("db.user", "");
            password = prop.getProperty("db.password", "");
            MongoCredential credential = MongoCredential.createCredential(user, this.database, password.toCharArray());
            if (!user.isEmpty())
                this.dbClient = new MongoClient(new ServerAddress(this.host, this.port), Arrays.asList(credential));
            else
                this.dbClient = new MongoClient(this.host, this.port);
            this.rideDatabase = this.dbClient.getDatabase(this.database);

        } catch (IOException ex) {
            logger.error(ex);
        }
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
