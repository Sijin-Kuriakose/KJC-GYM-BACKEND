package in.edu.kristujayanti.dbaccess;

import com.mongodb.MongoException;
import com.mongodb.TransactionOptions;
import com.mongodb.WriteConcern;
import com.mongodb.client.*;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.client.result.DeleteResult;
import in.edu.kristujayanti.exception.DataAccessException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.List;

public abstract class MongoDataAccess {

    protected static final Logger LOGGER = LogManager.getLogger(MongoDataAccess.class);

    protected boolean saveDocument(String collectionName, Document savingDocument, ClientSession clientSession, MongoDatabase mongoDatabase) throws IllegalStateException, DataAccessException {
        if (!StringUtils.isEmpty(collectionName) && savingDocument != null && clientSession != null && mongoDatabase != null) {
            try {
                MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
                return collection.insertOne(clientSession, savingDocument).wasAcknowledged();
            } catch (MongoException mongoException) {
                LOGGER.error("Mongo exception occurred while saving document {} ", mongoException.getMessage());
                throw new DataAccessException("Mongo exception occurred while saving document and exception is " + mongoException.getMessage());
            }
        } else {
            throw new IllegalStateException("Invalid params found while save document");
        }
    }

    /**
     * Saves a list of documents into a specified MongoDB collection using a client session.
     *
     * @param collectionName The name of the MongoDB collection where documents will be saved.
     * @param docsList       The list of Document objects to be saved.
     * @param clientSession  The client session to use for transaction management.
     * @param mongoDatabase  The MongoDB database where the collection resides.
     * @return true if the operation was acknowledged, false otherwise.
     * @throws IllegalStateException if any required parameter is invalid or null.
     * @throws DataAccessException   if a MongoDB exception occurs during the save operation.
     */
    protected boolean saveDocuments(String collectionName, List<Document> docsList, ClientSession clientSession, MongoDatabase mongoDatabase)
            throws IllegalStateException, DataAccessException {
        // Check if all required parameters are valid
        if (!StringUtils.isEmpty(collectionName) && docsList != null && clientSession != null && mongoDatabase != null) {
            try {
                // Get the MongoDB collection
                MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);

                // Insert the documents using the client session
                return collection.insertMany(clientSession, docsList).wasAcknowledged();
            } catch (MongoException mongoException) {
                // Catch any MongoDB exceptions
                LOGGER.error("Mongo exception occurred while saving documents: {}", mongoException.getMessage(), mongoException);
                throw new DataAccessException("Mongo exception occurred while saving documents: " + mongoException.getMessage());
            }
        } else {
            // Throw IllegalStateException if any required parameter is invalid
            throw new IllegalStateException("Invalid params found while saving documents");
        }
    }

    /**
     * @param collectionName   the name of the MongoDB collection
     * @param mongoDatabase    the MongoDB database
     *
     * @return {@link FindIterable<Document>}  containing the result set
     * @deprecated This method is deprecated. Use {@link #findDocuments(MongoDatabase, String)} or {@link #findDocuments(MongoDatabase, String, Bson, Bson)} instead.
     */
    @Deprecated(forRemoval = true)
    protected FindIterable<Document> findDocument(String collectionName, MongoDatabase mongoDatabase) throws IllegalStateException, DataAccessException {
        try {
            MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
            return collection.find();
        } catch (MongoException mongoException) {
            LOGGER.error("Mongo exception occurred while fetching document {} ", mongoException.getMessage());
            throw new DataAccessException("Mongo exception occurred while fetching document and exception is " + mongoException.getMessage());
        }
    }

    /**
     * @param collectionName   the name of the MongoDB collection
     * @param mongoDatabase    the MongoDB database
     * @param query  query to filter documents
     *
     * @return {@link FindIterable<Document>} containing the result set
     * @deprecated This method is deprecated. Use {@link #findSingleDocument(MongoDatabase, String, Bson)} instead.
     */
    @Deprecated(forRemoval = true)
    protected Document findSingleDocument(String collectionName, Bson query, MongoDatabase mongoDatabase) throws IllegalStateException, DataAccessException {
        try {
            MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
            FindIterable<Document> documents = collection.find(query);
            if (documents.iterator().hasNext()) {
                return documents.first();
            } else {
                return null; // Return null if no document matches the query
            }
        } catch (MongoException mongoException) {
            LOGGER.error("Mongo exception occurred while fetching document {} ", mongoException.getMessage());
            throw new DataAccessException("Mongo exception occurred while fetching document and exception is " + mongoException.getMessage());
        }
    }

    /**
     * @param collectionName   the name of the MongoDB collection
     * @param mongoDatabase    the MongoDB database
     * @param query  query to filter documents
     *
     * @return {@link FindIterable<Document>} containing the result set
     * @deprecated This method is deprecated. Use {@link #findDocumentsWithFilter(MongoDatabase, String, Bson)} or {@link #findDocuments(MongoDatabase, String, Bson, Bson)} instead.
     */
    @Deprecated(forRemoval = true)
    protected static FindIterable<Document> findAllDocumentsMatchingQuery(String collectionName, Bson query, MongoDatabase mongoDatabase) {
        try {
            MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
            return collection.find(query);
        } catch (Exception e) {
            System.err.println("An error occurred while querying MongoDB: " + e.getMessage());
            throw new RuntimeException("An error occurred while querying MongoDB: " + e.getMessage(), e);
        }
    }
    protected static FindIterable<Document> findAllDocumentsMatchingQuery(String collectionName, Bson query, MongoDatabase mongoDatabase,Document sortOrder) {
        try {
            MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
            return collection.find(query).sort(sortOrder);
        } catch (Exception e) {
            System.err.println("An error occurred while querying MongoDB: " + e.getMessage());
            throw new RuntimeException("An error occurred while querying MongoDB: " + e.getMessage(), e);
        }
    }

    protected boolean updateDocument(String collectionName, Bson filter, Document update, UpdateOptions updateOptions, ClientSession clientSession, MongoDatabase mongoDatabase) throws IllegalStateException, DataAccessException {
        if (!StringUtils.isEmpty(collectionName) && filter != null && update != null && updateOptions != null && clientSession != null && mongoDatabase != null) {
            try {
                MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
                UpdateResult result = collection.updateOne(clientSession, filter, update, updateOptions);
                return result.wasAcknowledged();
            } catch (MongoException mongoException) {
                LOGGER.error("Mongo exception occurred while updating document {} ", mongoException.getMessage());
                throw new DataAccessException("Mongo exception occurred while updating document and exception is " + mongoException.getMessage());
            }
        } else {
            throw new IllegalStateException("Invalid params found while updating document");
        }
    }

    protected boolean updateDocument(String collectionName, Bson filter, Document update, ClientSession clientSession, MongoDatabase mongoDatabase) throws IllegalStateException, DataAccessException {
        if (!StringUtils.isEmpty(collectionName) && filter != null && update != null && clientSession != null && mongoDatabase != null) {
            try {
                MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
                UpdateOptions updateOptions = new UpdateOptions().upsert(true);
                UpdateResult result = collection.updateOne(clientSession, filter, update, updateOptions);
                return result.wasAcknowledged();
            } catch (MongoException mongoException) {
                LOGGER.error("Mongo exception occurred while updating document {} ", mongoException.getMessage());
                throw new DataAccessException("Mongo exception occurred while updating document and exception is " + mongoException.getMessage());
            }
        } else {
            throw new IllegalStateException("Invalid params found while updating document");
        }
    }

    protected boolean updateDocuments(String collectionName, Bson filter, List<Document> updates, ClientSession clientSession, MongoDatabase mongoDatabase) throws IllegalStateException, DataAccessException {
        if (!StringUtils.isEmpty(collectionName) && filter != null && updates != null && clientSession != null && mongoDatabase != null) {
            try {
                MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
                UpdateOptions updateOptions = new UpdateOptions().upsert(true);
                UpdateResult result = collection.updateMany(clientSession, filter, updates, updateOptions);
                return result.wasAcknowledged();
            } catch (MongoException mongoException) {
                LOGGER.error("Mongo exception occurred while updating documents {} ", mongoException.getMessage());
                throw new DataAccessException("Mongo exception occurred while updating document and exception is " + mongoException.getMessage());
            }
        } else {
            throw new IllegalStateException("Invalid params found while updating documents");
        }
    }

    protected boolean updateDocuments(String collectionName, Bson filter, Document updates, ClientSession clientSession, MongoDatabase mongoDatabase) throws IllegalStateException, DataAccessException {
        if (!StringUtils.isEmpty(collectionName) && filter != null && updates != null && clientSession != null && mongoDatabase != null) {
            try {
                MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
                UpdateOptions updateOptions = new UpdateOptions().upsert(true);
                UpdateResult result = collection.updateMany(clientSession, filter, updates, updateOptions);
                return result.wasAcknowledged();
            } catch (MongoException mongoException) {
                LOGGER.error("Mongo exception occurred while updating documents {} ", mongoException.getMessage());
                throw new DataAccessException("Mongo exception occurred while updating document and exception is " + mongoException.getMessage());
            }
        } else {
            throw new IllegalStateException("Invalid params found while updating documents");
        }
    }

    /**
     * @param collectionName   the name of the MongoDB collection
     * @param mongoDatabase    the MongoDB database
     * @param fieldsToExclude  list of fields to exclude
     *
     * @return {@link FindIterable<Document>} containing the result set
     * @deprecated This method is deprecated. Use {@link #findDocumentsWithProjection(MongoDatabase, String, Bson)} or {@link #findDocuments(MongoDatabase, String, Bson, Bson)} instead.
     */
    @Deprecated(forRemoval = true)
    protected FindIterable<Document> findDocument(String collectionName, MongoDatabase mongoDatabase, List<String> fieldsToExclude) throws IllegalStateException, DataAccessException {
        {
            try {
                MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
                return collection.find().projection(Projections.exclude(fieldsToExclude));
            } catch (MongoException var6) {
                MongoException e = var6;
                LOGGER.error("Mongo exception occurred while fetching document {} ", e.getMessage());
                throw new DataAccessException("Mongo exception occurred while fetching document and exception is " + e.getMessage());
            }
        }
    }

    protected void startTransaction(ClientSession clientSession) {
        clientSession.startTransaction(TransactionOptions.builder().writeConcern(WriteConcern.MAJORITY).build());
    }

    protected void commitTransaction(ClientSession clientSession) {
        clientSession.commitTransaction();
    }

    protected void abortTransaction(ClientSession clientSession) {
        if (clientSession.hasActiveTransaction()) {
            clientSession.abortTransaction();
        }
    }

    protected ClientSession getMongoDbSession(MongoClient mongoClient) {
        try {
            return mongoClient.startSession();
        } catch (Exception var3) {
            LOGGER.error("Session cannot be created due to {}", var3.getMessage());
            throw new IllegalStateException("Session cannot be created due to: " + var3.getMessage());
        }
    }

    protected boolean deleteDocument(String collectionName, Bson filter, ClientSession clientSession, MongoDatabase mongoDatabase) throws IllegalStateException, DataAccessException {
        if (!StringUtils.isEmpty(collectionName) && filter != null && clientSession != null && mongoDatabase != null) {
            try {
                MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
                DeleteResult result = collection.deleteOne(clientSession, filter);
                return result.wasAcknowledged();
            } catch (MongoException mongoException) {
                LOGGER.error("Mongo exception occurred while deleting document {} ", mongoException.getMessage());
                throw new DataAccessException("Mongo exception occurred while deleting document and exception is " + mongoException.getMessage());
            }
        } else {
            throw new IllegalStateException("Invalid params found while deleting document");
        }
    }

    protected boolean deleteDocumentMany(String collectionName, Bson filter, ClientSession clientSession, MongoDatabase mongoDatabase) throws IllegalStateException, DataAccessException {
        if (!StringUtils.isEmpty(collectionName) && filter != null && clientSession != null && mongoDatabase != null) {
            try {
                MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
                DeleteResult result = collection.deleteMany(clientSession, filter);
                return result.wasAcknowledged();
            } catch (MongoException mongoException) {
                LOGGER.error("Mongo exception occurred while deleting document {} ", mongoException.getMessage());
                throw new DataAccessException("Mongo exception occurred while deleting document and exception is " + mongoException.getMessage());
            }
        } else {
            throw new IllegalStateException("Invalid params found while deleting document");
        }
    }

    /**
     * Fetches a single document from the specified MongoDB collection based on the provided filters.
     * .
     * This method retrieves a document from a MongoDB collection using the given filters.
     * It handles any MongoDB-related exceptions and throws a custom exception for data access issues.
     *
     * @param mongoDatabase  The MongoDatabase instance to interact with.
     * @param collectionName The name of the MongoDB collection from which documents will be fetched.
     * @param filters        A Bson filter to specify the selection criteria for the document.
     * @return The first document from the filtered documents.
     * @throws IllegalStateException if the database is in an invalid state.
     * @throws DataAccessException   if a MongoDB exception occurs during the operation.
     */
    protected Document findSingleDocument(MongoDatabase mongoDatabase, String collectionName, Bson filters) throws IllegalStateException, DataAccessException {
        return findDocuments(mongoDatabase, collectionName, filters, new Document()).first();
    }

    /**
     * Fetches documents from the specified MongoDB collection based on the provided filters.
     * .
     * This method retrieves documents from a MongoDB collection using the given filters.
     * It handles any MongoDB-related exceptions and throws a custom exception for data access issues.
     *
     * @param mongoDatabase  The MongoDatabase instance to interact with.
     * @param collectionName The name of the MongoDB collection from which documents will be fetched.
     * @param filters        A Bson filter to specify the selection criteria for documents.
     * @return A FindIterable containing the filtered documents.
     * @throws IllegalStateException if the database is in an invalid state.
     * @throws DataAccessException   if a MongoDB exception occurs during the operation.
     */
    protected FindIterable<Document> findDocumentsWithFilter(MongoDatabase mongoDatabase, String collectionName, Bson filters) throws IllegalStateException, DataAccessException {
        return findDocuments(mongoDatabase, collectionName, filters, new Document());
    }

    /**
     * Fetches documents from the specified MongoDB collection based on the provided projections.
     * .
     * This method retrieves documents from a MongoDB collection using the given projections.
     * It handles any MongoDB-related exceptions and throws a custom exception for data access issues.
     *
     * @param mongoDatabase  The MongoDatabase instance to interact with.
     * @param collectionName The name of the MongoDB collection from which documents will be fetched.
     * @param projections    A Bson projection to specify which fields should be returned in the result.
     * @return A FindIterable containing the projected documents.
     * @throws IllegalStateException if the database is in an invalid state.
     * @throws DataAccessException   if a MongoDB exception occurs during the operation.
     */
    protected FindIterable<Document> findDocumentsWithProjection(MongoDatabase mongoDatabase, String collectionName, Bson projections) throws IllegalStateException, DataAccessException {
        return findDocuments(mongoDatabase, collectionName, new Document(), projections);
    }

    /**
     * Fetches documents from the specified MongoDB collection.
     * .
     * This method retrieves documents from a MongoDB collection.
     * It handles any MongoDB-related exceptions and throws a custom exception for data access issues.
     *
     * @param mongoDatabase  The MongoDatabase instance to interact with.
     * @param collectionName The name of the MongoDB collection from which documents will be fetched.
     * @return A FindIterable containing the documents.
     * @throws IllegalStateException if the database is in an invalid state.
     * @throws DataAccessException   if a MongoDB exception occurs during the operation.
     */
    protected FindIterable<Document> findDocuments(MongoDatabase mongoDatabase, String collectionName) throws IllegalStateException, DataAccessException {
        return findDocuments(mongoDatabase, collectionName, new Document(), new Document());
    }

    /**
     * Fetches documents from the specified MongoDB collection based on the provided filters and projections.
     * .
     * This method retrieves documents from a MongoDB collection using the given filters and projections.
     * It handles any MongoDB-related exceptions and throws a custom exception for data access issues.
     *
     * @param mongoDatabase  The MongoDatabase instance to interact with.
     * @param collectionName The name of the MongoDB collection from which documents will be fetched.
     * @param filters        A Bson filter to specify the selection criteria for documents.
     * @param projections    A Bson projection to specify which fields should be returned in the result.
     * @return A FindIterable containing the filtered and projected documents.
     * @throws IllegalStateException if the database is in an invalid state.
     * @throws DataAccessException   if a MongoDB exception occurs during the operation.
     */
    protected FindIterable<Document> findDocuments(MongoDatabase mongoDatabase, String collectionName, Bson filters, Bson projections) throws IllegalStateException, DataAccessException {
        try {
            // Log the start of the operation, including the collection name
            LOGGER.debug("Attempting to fetch documents from collection: '{}' with filters: '{}' and projections: '{}'",
                    collectionName, filters.toBsonDocument(), projections.toBsonDocument());

            // Fetch the MongoDB collection
            MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);

            // Perform the query using the provided filters and projections
            FindIterable<Document> documents = collection.find(filters).projection(projections);

            // Log success with additional information on filters and projections
            LOGGER.debug("Successfully fetched documents from collection: '{}'. Filters: '{}', Projections: '{}'",
                    collectionName, filters.toBsonDocument(), projections.toBsonDocument());

            return documents;
        } catch (MongoException e) {
            // Log a detailed error message with the exception cause, collection name, and filters used
            LOGGER.error("Error fetching documents from collection: '{}'. Filters: '{}', Projections: '{}'. Exception: {}",
                    collectionName, filters.toBsonDocument(), projections.toBsonDocument(), e.getMessage(), e);

            // Throw a custom DataAccessException with detailed error message
            throw new DataAccessException("Error fetching documents from collection '" + collectionName +
                    "' with filters: " + filters.toBsonDocument() +
                    " and projections: " + projections.toBsonDocument() +
                    ". Exception: " + e.getMessage());
        }
    }
}