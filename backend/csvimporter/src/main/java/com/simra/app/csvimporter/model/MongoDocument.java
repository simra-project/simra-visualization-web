package main.java.com.simra.app.csvimporter.model;

import org.bson.Document;

/**
 * The interface Mongo document.
 */
public interface MongoDocument {

    /**
     * To document object document.
     *
     * @return the document
     */
    Document toDocumentObject();
}
