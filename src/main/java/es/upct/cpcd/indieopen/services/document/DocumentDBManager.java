package es.upct.cpcd.indieopen.services.document;

import java.util.Optional;

import org.bson.Document;

/**
 * Interface defining a document-based database manager for INDIeOpen
 *
 * @author MARIO
 */
public interface DocumentDBManager {
    /**
     * Find a Mongo Document in a collection given the Document ID.
     *
     * @param collection Mongo Collection to recieve the query
     * @param documentId Document id
     * @return Optional describing the Document
     */
    Optional<Document> findDocument(DocumentDBCollection collection, String documentId);


    /**
     * Replace an existing Mongo Document in a collection given its document Id with
     * an updated Mongo Document instance.
     *
     * @param collection Mongo Collection to recieve the query
     * @param documentId Document id
     * @param document   Mongo Document
     * @throws DocumentDataException If a MongoException is thrown during the
     *                               replacement the document
     */
    void replaceDocument(DocumentDBCollection collection, String documentId, Document document)
            throws DocumentDataException;

    /**
     * Store a new Mongo Document in a collection.
     *
     * @param collection Mongo Collection to recieve the query
     * @param document   Mongo Document instance
     * @return String containint the new Document Id
     * @throws DocumentDataException If a MongoException is thrown storing the
     *                               document
     */
    String storeDocument(DocumentDBCollection collection, Document document) throws DocumentDataException;

    /**
     * Delete a document of a collection given its id
     *
     * @param collection Mongo Collection to recieve the query
     * @param documentid Document id
     * @throws DocumentDataException If a MongoException is thrown deleting the
     *                               document
     */
    void deleteDocument(DocumentDBCollection collection, String documentid) throws DocumentDataException;


    /**
     * Close the DocumentDBManager
     */
    void close();
}
