package in.edu.kristujayanti.util;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import in.edu.kristujayanti.dbaccess.MongoDataAccess;
import in.edu.kristujayanti.propertyBinder.KJUSYSCommonKeysPBinder;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

public class ActivityLoggingService extends MongoDataAccess {
    private static final Logger LOGGER = LoggerFactory.getLogger(ActivityLoggingService.class);
    private final MongoDatabase mongoDatabase;
    private final MongoClient mongoClient;

    public ActivityLoggingService(MongoDatabase mongoDatabase, MongoClient mongoClient) {
        this.mongoClient = mongoClient;
        this.mongoDatabase = mongoDatabase;
    }

    /**
     * Logs the activity of an admin updating a user profile.
     *
     * @param collectionName The name of the collection.
     * @param updatedBy       The admin who made the update.
     * @param updatedEntity   The entity whose details were updated.
     * @param updatedField    The field that was updated.
     * @param oldValue        The old value of the field.
     * @param newValue        The new value of the field.
     */
    public void logActivity(String collectionName, String updatedBy,String updatedEntity, String updatedField, Object oldValue, Object newValue) {
        // Do not log if both values are the same
        if (Objects.equals(oldValue, newValue)) {
            return;
        }

        // Get the current timestamp in milliseconds
        long timestamp = DateUtils.getCurrentTimeInMillis();

        // Create a document to store the activity log details
        Document activityLog = new Document()
                .append(KJUSYSCommonKeysPBinder.ACTIVITY_TIMESTAMP.getPropertyName(), timestamp)
                .append(KJUSYSCommonKeysPBinder.ACTIVITY_PERFORMED_BY.getPropertyName(), updatedBy)
                .append(KJUSYSCommonKeysPBinder.ACTIVITY_UPDATED_ENTITY.getPropertyName(), updatedEntity)
                .append(KJUSYSCommonKeysPBinder.ACTIVITY_UPDATED_FIELD.getPropertyName(), updatedField)
                .append(KJUSYSCommonKeysPBinder.ACTIVITY_OLD_VALUE.getPropertyName(), oldValue)
                .append(KJUSYSCommonKeysPBinder.ACTIVITY_NEW_VALUE.getPropertyName(), newValue);

        // Use a client session for transaction management
        try (ClientSession clientSession = getMongoDbSession(this.mongoClient)) {
            // Start a transaction
            startTransaction(clientSession);

            // Attempt to save the activity log document
            if (saveDocument(collectionName, activityLog, clientSession, mongoDatabase)) {
                // Commit the transaction if the document was saved successfully
                commitTransaction(clientSession);
            } else {
                // Abort the transaction if the document could not be saved
                abortTransaction(clientSession);
                LOGGER.error("Failed to save activity log to database");
            }
        } catch (Exception e) {
            // Log any exceptions that occur during the logging process
            LOGGER.error("Error occurred while creating activity log in database: {}", e.getMessage(), e);
        }
    }

    /**
     * Logs multiple activities of an admin updating user profiles.
     *
     * @param collectionName The name of the collection.
     * @param activityLogs List of ActivityLogEntry objects representing the activities to log.
     */
    public void logActivity(String collectionName, List<ActivityLogEntry> activityLogs) {
        // return if activityLogs is null
        if(activityLogs.isEmpty()){
            return;
        }
        // Convert ActivityLogEntry objects to Document objects
        List<Document> activityLogDocuments = activityLogs.stream()
                .map(ActivityLogEntry::toDocument)
                .toList();

        // Use a client session for transaction management
        try (ClientSession clientSession = getMongoDbSession(this.mongoClient)) {
            // Start a transaction
            startTransaction(clientSession);

            // Attempt to save the activity log documents
            if (saveDocuments(collectionName, activityLogDocuments, clientSession, mongoDatabase)) {
                // Commit the transaction if the documents were saved successfully
                commitTransaction(clientSession);
            } else {
                // Abort the transaction if the documents could not be saved
                abortTransaction(clientSession);
                LOGGER.error("Failed to save activity logs to database");
            }
        } catch (Exception e) {
            // Log any exceptions that occur during the logging process
            LOGGER.error("Error occurred while creating activity logs in database: {}", e.getMessage(), e);
        }
    }
}
