package in.edu.kristujayanti.services.UserServices;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.ClientSession;
import com.mongodb.MongoException;
import in.edu.kristujayanti.dbaccess.MongoDataAccess;
import io.vertx.core.Future;
//import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
//import org.bson.types.ObjectId;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;

import static in.edu.kristujayanti.constants.DatabaseCollectionNames.*;
import static in.edu.kristujayanti.propertyBinder.Gym.GymKeysPBinder.*;

public class UserService extends MongoDataAccess {
    private static  final Logger LOGGER=LogManager.getLogger(UserService.class);
    private final MongoDatabase mongoDatabase;
    private final MongoClient mongoClient;
    private final MongoCollection<Document> userCollection;
    private  final MongoCollection<Document>ordercollection;
//    private final MongoCollection<Document> serviceCollection;
//    private final MongoCollection<Document> addOnCollection;

    public UserService(MongoDatabase mongoDatabase, MongoClient mongoClient)
    {
        this.mongoDatabase = mongoDatabase;
        this.mongoClient = mongoClient;
        this.userCollection = this.mongoDatabase.getCollection(GYM_USERS_COLLECTION);
        this.ordercollection=this.mongoDatabase.getCollection(GYM_ORDER_DETAILS_COLLECTION);
//        this.serviceCollection = this.mongoDatabase.getCollection(SERVICES_COLLECTION);
//        this.addOnCollection = this.mongoDatabase.getCollection(ADDON_COLLECTION);
    }
    public Future<JsonObject> addmembership(String name,String userid,String usertype,String contactInfo,String emailid,Document preProcessedDocument)
    {
        ClientSession clientSession=getMongoDbSession(this.mongoClient);
        try
        {
            System.out.println(emailid);
            startTransaction(clientSession);
            LOGGER.info("Starting transaction to create member with emailID:{}",emailid);
            Document existingmember=userCollection.find(clientSession,Filters.eq(EMAIL_ID.getPropertyName(),emailid.toUpperCase())).first();
            if(existingmember!=null)
            {
                LOGGER.warn("Member with emailID {} already exists", emailid);
                abortTransaction(clientSession);
                LOGGER.info("Transaction aborted due to existing member with emailID: {}", emailid);
                return Future.failedFuture("Member with userID " + emailid + " already exists!");
            }
            System.out.println(existingmember);
//    LOGGER.debug("Received subscription object{}",subscriptions);
//    Object serviceDetailsObj=subscriptions.getValue(SUBSCRIPTION_SERVICE_LIST.getPropertyName());
//    JsonObject serviceDetails;
//    if(serviceDetailsObj instanceof JsonArray)
//    {
//        JsonArray serviceDetailsArray=(JsonArray) serviceDetailsObj;
//        if (serviceDetailsArray.isEmpty()) {
//            LOGGER.warn("{} is empty in subscription: {}", SUBSCRIPTION_SERVICE_LIST.getPropertyName(), subscriptions);
//            abortTransaction(clientSession);
//            LOGGER.info("Transaction aborted due to empty subscription service list for userID: {}", userid);
//            return Future.failedFuture(SUBSCRIPTION_SERVICE_LIST.getPropertyName() + " cannot be empty");
//        }
//        if (serviceDetailsArray.size() > 1) {
//            LOGGER.warn("Only one service is allowed in {}", SUBSCRIPTION_SERVICE_LIST.getPropertyName());
//            abortTransaction(clientSession);
//            LOGGER.info("Transaction aborted due to multiple services in subscription for userID: {}", userid);
//            return Future.failedFuture("Only one service is allowed in " + SUBSCRIPTION_SERVICE_LIST.getPropertyName());
//        }
//        serviceDetails=serviceDetailsArray.getJsonObject(0);
//    } else if (serviceDetailsObj instanceof JsonObject)
//    {
//    serviceDetails=(JsonObject) serviceDetailsObj;
//    }
//    else
//    {
//        LOGGER.warn("{} is not a valid document or list in subscription: {}", SUBSCRIPTION_SERVICE_LIST.getPropertyName(), subscriptions);
//        abortTransaction(clientSession);
//        LOGGER.info("Transaction aborted due to invalid subscription service list format for userID: {}", userid);
//        return Future.failedFuture(SUBSCRIPTION_SERVICE_LIST.getPropertyName() + " must be a valid document or a list with one document");
//    }
//        if (serviceDetails == null) {
//            LOGGER.warn("{} is null in subscription: {}", SUBSCRIPTION_SERVICE_LIST.getPropertyName(), subscriptions);
//            abortTransaction(clientSession);
//            LOGGER.info("Transaction aborted due to null subscription service list for userID: {}", userid);
//            return Future.failedFuture(SUBSCRIPTION_SERVICE_LIST.getPropertyName() + " cannot be null");
//        }
//    LOGGER.debug("Extracted service details:{}",serviceDetails);
//
//        JsonArray addOns = subscriptions.getJsonArray(SUBSCRIPTION_ADDON_LIST.getPropertyName(), new JsonArray());
//        String paymentDateStr = subscriptions.getString(PAYMENT_DATE.getPropertyName());
//        String method = subscriptions.getString(METHOD.getPropertyName());
//        String status = subscriptions.getString(STATUS.getPropertyName());
//        String preferredStartDateStr = subscriptions.getString("preferredStartDate_Gym_Date");
//
//        if (paymentDateStr == null || method == null || status == null) {
//            LOGGER.warn("Missing required subscription fields - paymentDate: {}, method: {}, status: {}", paymentDateStr, method, status);
//            abortTransaction(clientSession);
//            LOGGER.info("Transaction aborted due to missing subscription fields for emailID: {}", emailid);
//            return Future.failedFuture("Payment date, method, and status are required in subscription");
//        }
        Document userDoc = preProcessedDocument;

            userDoc = new Document()
                    .append(USER_ID.getPropertyName(), userid.toUpperCase())
                    .append(NAME.getPropertyName(), name.toUpperCase())
                    .append(USER_TYPE.getPropertyName(), usertype.toUpperCase())
                    .append(CONTACT_NUMBER.getPropertyName(), contactInfo)
                    .append(EMAIL_ID.getPropertyName(), emailid.toUpperCase());


            // Save the user document within the transaction
            LOGGER.info("Request to save user details in: {}", userDoc);
            saveDocument(GYM_USERS_COLLECTION, userDoc, clientSession, mongoDatabase);

//            LOGGER.info("Request to save user details in: {}", userDoc);
//            saveDocument(GYM_ORDER_DETAILS_COLLECTION, userDoc, clientSession, mongoDatabase);


            // Commit the transaction
            commitTransaction(clientSession);
            LOGGER.info("Member with userID {} created successfully", userid);
            JsonObject memberDetails = new JsonObject()
                    .put(USER_ID.getPropertyName(), userid)
                    .put(NAME.getPropertyName(), name)
                    .put(MEMBER_TYPE.getPropertyName(), usertype)
                    .put(CONTACT_NUMBER.getPropertyName(), contactInfo)
                    .put(EMAIL_ID.getPropertyName(), emailid);

            return Future.succeededFuture(memberDetails);
        } catch (MongoException e) {
            LOGGER.error("MongoDB error while creating member with emailID {}: {}", emailid, e.getMessage(), e);
            abortTransaction(clientSession);
            LOGGER.info("Transaction aborted due to MongoDB error for emailID: {}", emailid);
            return Future.failedFuture("Failed to create member due to MongoDB error: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to create member with emailID {}: {}", emailid, e.getMessage(), e);
            abortTransaction(clientSession);
            LOGGER.info("Transaction aborted due to general error for emailID: {}", emailid);
            return Future.failedFuture("Failed to create member: " + e.getMessage());
        } finally {
            clientSession.close();
            LOGGER.debug("ClientSession closed for emailID: {}", emailid);
        }
    }
    public boolean getUserByEmailId(String email) {
        Document query = new Document(EMAIL_ID.getPropertyName(), email);
        Document user = userCollection.find(query).first();
        return user != null;
    }

    public Future<Boolean> updateUserDetails(String emailId, String newName, Long newContactNumber) {
        ClientSession clientSession = getMongoDbSession(this.mongoClient);
        try {
            startTransaction(clientSession);
            LOGGER.info("Starting transaction to update member details for emailID: {}", emailId);

            // Check if user exists
            Document existingUser = userCollection.find(clientSession, Filters.eq(EMAIL_ID.getPropertyName(), emailId.toUpperCase())).first();
            if (existingUser == null) {
                LOGGER.warn("Member with emailID {} does not exist", emailId);
                abortTransaction(clientSession);
                LOGGER.info("Transaction aborted due to non-existent member with emailID: {}", emailId);
                return Future.failedFuture("Member with emailID " + emailId + " does not exist!");
            }

            // Prepare update document with only name and contact number
            Document update = new Document("$set", new Document()
                    .append(NAME.getPropertyName(), newName.toUpperCase())
                    .append(CONTACT_NUMBER.getPropertyName(), newContactNumber));

            // Perform the update
            boolean updated = updateDocument(GYM_USERS_COLLECTION, Filters.eq(EMAIL_ID.getPropertyName(), emailId.toUpperCase()), update, clientSession, mongoDatabase);
            if (!updated) {
                LOGGER.warn("Failed to update member details for emailID: {}", emailId);
                abortTransaction(clientSession);
                LOGGER.info("Transaction aborted due to failure in updating member details for emailID: {}", emailId);
                return Future.failedFuture("Failed to update member details");
            }

            // Fetch the updated user document
//            Document updatedUser = userCollection.find(clientSession, Filters.eq(EMAIL_ID.getPropertyName(), emailId.toUpperCase())).first();
//            JsonObject updatedDetails = new JsonObject()
//                    .put(USER_ID.getPropertyName(), updatedUser.getString(USER_ID.getPropertyName()))
//                    .put(NAME.getPropertyName(), updatedUser.getString(NAME.getPropertyName()))
//                    .put(MEMBER_TYPE.getPropertyName(), updatedUser.getString(USER_TYPE.getPropertyName()))
//                    .put(CONTACT_NUMBER.getPropertyName(), updatedUser.getLong(CONTACT_NUMBER.getPropertyName()))
//                    .put(EMAIL_ID.getPropertyName(), updatedUser.getString(EMAIL_ID.getPropertyName()));
//
//            // Create a new document in GYM_ORDER_DETAILS_COLLECTION with user details
//            // Create a new document in GYM_ORDER_DETAILS_COLLECTION with user details
//            Document orderDocument = new Document()
//                    .append("_id", new org.bson.types.ObjectId())
//                    .append("userId_Gym_Text", updatedUser.getString(USER_ID.getPropertyName()))
//                    .append("name_Gym_Text", updatedUser.getString(NAME.getPropertyName()))
//                    .append("userType_Gym_Text", updatedUser.getString(USER_TYPE.getPropertyName()))
//                    .append("emailId_Gym_Text", updatedUser.getString(EMAIL_ID.getPropertyName()))
//                    .append("contactNumber_Gym_Long", updatedUser.getLong(CONTACT_NUMBER.getPropertyName()));
////                    .append("update_timestamp", System.currentTimeMillis());
//
//// Insert the new document into GYM_ORDER_DETAILS_COLLECTION
//            MongoCollection<Document> orderDetailsCollection = mongoDatabase.getCollection(GYM_ORDER_DETAILS_COLLECTION);
//            orderDetailsCollection.insertOne(clientSession, orderDocument);
            LOGGER.info("New document created in GYM_ORDER_DETAILS_COLLECTION for emailID: {}", emailId);
            commitTransaction(clientSession);
            LOGGER.info("Member details updated successfully for emailID: {}", emailId);
            return Future.succeededFuture(updated);

        } catch (MongoException e) {
            LOGGER.error("MongoDB error while updating member with emailID {}: {}", emailId, e.getMessage(), e);
            abortTransaction(clientSession);
            LOGGER.info("Transaction aborted due to MongoDB error for emailID: {}", emailId);
            return Future.failedFuture("Failed to update member due to MongoDB error: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to update member with emailID {}: {}", emailId, e.getMessage(), e);
            abortTransaction(clientSession);
            LOGGER.info("Transaction aborted due to general error for emailID: {}", emailId);
            return Future.failedFuture("Failed to update member: " + e.getMessage());
        } finally {
            clientSession.close();
            LOGGER.debug("ClientSession closed for emailID: {}", emailId);
        }
    }

    public Future<JsonObject> getUserDetailsByEmail(String emailId) {
        ClientSession clientSession = getMongoDbSession(this.mongoClient);
        try {
            startTransaction(clientSession);
            LOGGER.info("Starting transaction to fetch user details for emailID: {}", emailId);

            // Fetch user document
            Document user = userCollection.find(clientSession, Filters.eq(EMAIL_ID.getPropertyName(), emailId.toUpperCase())).first();
            if (user == null) {
                LOGGER.warn("Member with emailID {} does not exist", emailId);
                abortTransaction(clientSession);
                LOGGER.info("Transaction aborted due to non-existent member with emailID: {}", emailId);
                return Future.failedFuture("Member with emailID " + emailId + " does not exist!");
            }

            // Prepare user details
            JsonObject userDetails = new JsonObject()
                    .put(USER_ID.getPropertyName(), user.getString(USER_ID.getPropertyName()))
                    .put(NAME.getPropertyName(), user.getString(NAME.getPropertyName()))
                    .put(CONTACT_NUMBER.getPropertyName(), user.getLong(CONTACT_NUMBER.getPropertyName()))
                    .put(EMAIL_ID.getPropertyName(), user.getString(EMAIL_ID.getPropertyName()))
                    .put(USER_OID.getPropertyName(), user.getObjectId("_id").toString());

            commitTransaction(clientSession);
            LOGGER.info("User details fetched successfully for emailID: {}", emailId);
            return Future.succeededFuture(userDetails);

        } catch (MongoException e) {
            LOGGER.error("MongoDB error while fetching user details with emailID {}: {}", emailId, e.getMessage(), e);
            abortTransaction(clientSession);
            LOGGER.info("Transaction aborted due to MongoDB error for emailID: {}", emailId);
            return Future.failedFuture("Failed to fetch user details due to MongoDB error: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to fetch user details with emailID {}: {}", emailId, e.getMessage(), e);
            abortTransaction(clientSession);
            LOGGER.info("Transaction aborted due to general error for emailID: {}", emailId);
            return Future.failedFuture("Failed to fetch user details: " + e.getMessage());
        } finally {
            clientSession.close();
            LOGGER.debug("ClientSession closed for emailID: {}", emailId);
        }
    }
}