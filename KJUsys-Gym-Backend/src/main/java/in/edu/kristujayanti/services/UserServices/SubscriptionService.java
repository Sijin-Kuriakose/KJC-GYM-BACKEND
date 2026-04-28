package in.edu.kristujayanti.services.UserServices;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.ClientSession;
import com.mongodb.MongoException;
import com.mongodb.client.result.InsertOneResult;
import in.edu.kristujayanti.dbaccess.MongoDataAccess;
import in.edu.kristujayanti.util.ObjectIdUtil;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import in.edu.kristujayanti.util.DateUtils;

import java.util.*;

import static in.edu.kristujayanti.collectionNames.FeeModuleCNBinder.FEE_GROUP_COLLECTION;
import static in.edu.kristujayanti.collectionNames.PaymentCNBinder.ORDER_DETAILS_COLLECTION;
import static in.edu.kristujayanti.constants.DatabaseCollectionNames.*;
import static in.edu.kristujayanti.propertyBinder.FeeModule.FeeModuleKeysPBinder.*;
import static in.edu.kristujayanti.propertyBinder.Gym.GymKeysPBinder.*;
import static io.vertx.core.Promise.promise;

public class SubscriptionService extends MongoDataAccess {
    private static final Logger LOGGER = LogManager.getLogger(SubscriptionService.class);
    private final MongoDatabase mongoDatabase;
    private final MongoClient mongoClient;
    private final MongoCollection<Document> userCollection;
    private final MongoCollection<Document> serviceCollection;
    private final MongoCollection<Document> addOnCollection;
    private final MongoCollection<Document> orderDetailsCollection;

    public SubscriptionService(MongoDatabase mongoDatabase, MongoClient mongoClient) {
        this.mongoDatabase = mongoDatabase;
        this.mongoClient = mongoClient;
        this.userCollection = this.mongoDatabase.getCollection(GYM_USERS_COLLECTION);
        this.serviceCollection = this.mongoDatabase.getCollection(GYM_SERVICES_COLLECTION);
        this.addOnCollection = this.mongoDatabase.getCollection(GYM_ADDONS_COLLECTION);
        this.orderDetailsCollection = this.mongoDatabase.getCollection(GYM_ORDER_DETAILS_COLLECTION);

    }

    public Future<Document> addSubscription(String emailId, JsonObject subscription_service_addonRequest) {
        ClientSession clientSession = getMongoDbSession(this.mongoClient);
        try {
            startTransaction(clientSession);
            LOGGER.info("Starting transaction to check and add subscription for emailID: {}", emailId);

            if (!getUserByEmailId(emailId.toUpperCase())) {
                LOGGER.warn("User with emailID {} does not exist", emailId);
                abortTransaction(clientSession);
                LOGGER.info("Transaction aborted due to non-existent user with emailID: {}", emailId);
                return Future.failedFuture(String.valueOf(new Document("error", "User with emailID " + emailId + " does not exist")));
            }
            LOGGER.info("User with emailID {} exists, checking status", emailId);

            // Retrieve user document and check status
            Document user = userCollection.find(clientSession, Filters.eq(EMAIL_ID.getPropertyName(), emailId.toUpperCase())).first();
            if (user != null && user.getBoolean(STATUS.getPropertyName(), true) == false) {
                clientSession.abortTransaction();
                LOGGER.warn("Transaction aborted: User with email {} has status false", emailId);
                return Future.failedFuture(String.valueOf(new Document("error", "Cannot add subscription: User with email " + emailId + " is inactive (status: false)")));
            }

            LOGGER.info("User with emailID {} exists, validating subscription details", emailId);

            Document userDoc = userCollection.find(clientSession, Filters.eq(EMAIL_ID.getPropertyName(), emailId)).first();
            if (userDoc != null) {
                List<Document> existingSubscriptions = userDoc.getList(SUBSCRIPTIONS_LIST.getPropertyName(), Document.class);

//                System.out.println("subs"+ existingSubscriptions);
                Long currentDateMillis = System.currentTimeMillis();

                boolean hasActiveService = false;
                int activeSubIndex = -1;
                Long activeSubEndDate = null;
                if (existingSubscriptions != null && !existingSubscriptions.isEmpty()) {
                    for (int i = 0; i < existingSubscriptions.size(); i++) {
                        Document sub = existingSubscriptions.get(i);
                        Long endDate = sub.getLong(SUB_END_DATE.getPropertyName());
                        if (endDate != null && endDate > currentDateMillis && sub.containsKey(SUBSCRIPTION_SERVICE_LIST.getPropertyName())) {
                            hasActiveService = true;
                            activeSubIndex = i; // Use the first active subscription
                            activeSubEndDate = endDate;
                            break; // Stop at the first active subscription to prioritize it
                        }
                    }
                }

                // Check for future subscriptions if adding a new service and an active service exists
                if (hasActiveService && subscription_service_addonRequest.getString(SERVICE_OID.getPropertyName()) != null) {
                    Long sevenDaysInMillis = 7L * 24L * 60L * 60L * 1000L;
                    Long maxAllowedEndDateMillis = currentDateMillis + sevenDaysInMillis;
                    for (Document sub : existingSubscriptions) {
                        Long subStartDate = sub.getLong(SUB_START_DATE.getPropertyName());
                        Long subEndDate = sub.getLong(SUB_END_DATE.getPropertyName());
                        if (subStartDate != null && subEndDate != null && subStartDate >= activeSubEndDate && sub.containsKey(SUBSCRIPTION_SERVICE_LIST.getPropertyName())) {
                            if (subEndDate > maxAllowedEndDateMillis) {
                                LOGGER.warn("Cannot add another future subscription for emailId {}. Existing future subscription ends on {} which is beyond the allowed 7-day window from current date {}",
                                        emailId, DateUtils.convertMillisToDateString(subEndDate), DateUtils.convertMillisToDateString(currentDateMillis));
                                abortTransaction(clientSession);
                                LOGGER.info("Transaction aborted due to existing future subscription with end date beyond 7-day window for emailID: {}", emailId);
                                return Future.failedFuture(String.valueOf(new Document("error", "Cannot add another future subscription as an existing future subscription ends beyond the allowed 7-day window on " +
                                        DateUtils.convertMillisToDateString(subEndDate))));
                            }
                        }
                    }
                }

                if (!hasActiveService) {
                    LOGGER.info("No active service found or subscriptions expired for emailID {}, routing to addSubscriptionForNewUser",
                            emailId);
                    return addSubscriptionForNewUser(emailId, subscription_service_addonRequest, clientSession, userDoc);
                }

                LOGGER.info("Active service found for emailID {}, routing to addNewServiceForExistingUser",
                        emailId);
                return addNewServiceForExistingUser(emailId, subscription_service_addonRequest, clientSession, userDoc, activeSubIndex);

            } else {
                abortTransaction(clientSession);
                LOGGER.info("User not found for emailID: {}", emailId);
                return Future.failedFuture(String.valueOf(new Document("error", "Failed to process subscription due to unknown emailid " + emailId)));
            }

        } catch (MongoException e) {
            LOGGER.error("MongoDB error while processing subscription for emailID {}: {}", emailId, e.getMessage(), e);
            abortTransaction(clientSession);
            LOGGER.info("Transaction aborted due to MongoDB error for emailID: {}", emailId);
            return Future.failedFuture(String.valueOf(new Document("error", "Failed to process subscription due to MongoDB error: " + e.getMessage())));
        } catch (Exception e) {
            LOGGER.error("Failed to process subscription for emailID {}: {}", emailId, e.getMessage(), e);
            abortTransaction(clientSession);
            LOGGER.info("Transaction aborted due to general error for emailID: {}", emailId);
            return Future.failedFuture(String.valueOf(new Document("error", "Failed to process subscription: " + e.getMessage())));
        } finally {
            if (clientSession != null) {
                clientSession.close();
                LOGGER.debug("ClientSession closed for emailID: {}", emailId);
            }
        }
    }

//    private Future<JsonObject> addSubscriptionForNewUser(String emailId, JsonObject subscription_service_addonRequest, ClientSession clientSession, Document userDoc) {
//        try {
//            LOGGER.info("Processing new subscription for emailID: {}", emailId);
//
//            String serviceIdStr = subscription_service_addonRequest.getString(SERVICE_OID.getPropertyName());
//            JsonArray addOns = subscription_service_addonRequest.getJsonArray(SUBSCRIPTION_ADDON_LIST.getPropertyName(), new JsonArray());
//            String preferredStartDateStr = subscription_service_addonRequest.getString("preferredStartDate");
//            Long currentDateMillis = System.currentTimeMillis();
//            Long preferredStartDateMillis = preferredStartDateStr != null ? DateUtils.getStartOfDayMillis(preferredStartDateStr) : currentDateMillis;
//            Long maxAllowedStartDateMillis = currentDateMillis + (6L * 24L * 60L * 60L * 1000L);
//
//            if (preferredStartDateMillis == null && preferredStartDateStr != null) {
//                LOGGER.warn("Invalid preferred start date format: {}", preferredStartDateStr);
//                abortTransaction(clientSession);
//                return Future.failedFuture("Preferred start date must be in dd-MM-yyyy, yyyy-MM-dd, yyyy/MM/dd, or dd/MM/yyyy format");
//            }
//
//            // Check if preferred date is in the past
//            if (preferredStartDateMillis != null && preferredStartDateMillis < currentDateMillis - (1L * 24L * 60L * 60L * 1000L)) {
//                LOGGER.warn("Preferred start date {} is in the past for emailID: {}", preferredStartDateStr, emailId);
//                abortTransaction(clientSession);
//                return Future.failedFuture("Preferred start date cannot be in the past");
//            }
//
//            // Adjust start date to current date if preferred date is not provided or matches current date
//            Long subStartDateMillis = preferredStartDateMillis != null ? preferredStartDateMillis : currentDateMillis;
//            if (subStartDateMillis > maxAllowedStartDateMillis) {
//                LOGGER.warn("Preferred start date {} out of allowed range [{} to {}] for emailID: {}",
//                        preferredStartDateStr, DateUtils.convertMillisToDateString(currentDateMillis),
//                        DateUtils.convertMillisToDateString(maxAllowedStartDateMillis), emailId);
//                abortTransaction(clientSession);
//                return Future.failedFuture("Preferred start date must be within 7 days from today");
//            }
//
//            if (serviceIdStr == null) {
//                LOGGER.warn("No service provided in the request for emailID: {}", emailId);
//                abortTransaction(clientSession);
//                LOGGER.info("Transaction aborted due to missing service for emailID: {}", emailId);
//                return Future.failedFuture("A service is required for new users");
//            }
//
//            List<Document> existingSubscriptions = userDoc.getList(SUBSCRIPTIONS_LIST.getPropertyName(), Document.class, new ArrayList<>());
//            System.out.println("existing"+ existingSubscriptions);
//            Document subscriptionDoc = new Document();
//            double currentTransactionCost = 0.0;
//            Long subEndDateMillis = null;
//
//            if (!ObjectId.isValid(serviceIdStr)) {
//                LOGGER.warn("Invalid service ID in subscription request: {}", serviceIdStr);
//                abortTransaction(clientSession);
//                LOGGER.info("Transaction aborted due to invalid service ID for emailID: {}", emailId);
//                return Future.failedFuture("Valid service ID is required in the request");
//            }
//            ObjectId serviceId = new ObjectId(serviceIdStr);
//
//            Document serviceDoc = serviceCollection.find(clientSession, Filters.eq("_id", serviceId)).first();
//            if (serviceDoc == null) {
//                LOGGER.warn("Service with ID {} does not exist in SERVICES_COLLECTION", serviceIdStr);
//                abortTransaction(clientSession);
//                LOGGER.info("Transaction aborted due to non-existent service with ID: {} for emailID: {}", serviceIdStr, emailId);
//                return Future.failedFuture("Service with ID " + serviceIdStr + " does not exist");
//            }
//            Double serviceCost = serviceDoc.getDouble(AMOUNT_SERVICE.getPropertyName());
//            if (serviceCost == null) {
//                LOGGER.warn("Service cost is null for service ID: {}", serviceIdStr);
//                abortTransaction(clientSession);
//                return Future.failedFuture("Service cost is missing or invalid for service ID " + serviceIdStr);
//            }
//            currentTransactionCost += serviceCost;
//
//            Integer durationService = serviceDoc.getInteger(DURATION_SERVICE.getPropertyName());
//            if (durationService == null) {
//                LOGGER.warn("Duration is null for service ID: {}", serviceIdStr);
//                abortTransaction(clientSession);
//                return Future.failedFuture("Service duration is missing for service ID " + serviceIdStr);
//            }
//            subEndDateMillis = subStartDateMillis + ((durationService - 1) * 24L * 60L * 60L * 1000L);
//
//            subscriptionDoc.append(SUBSCRIPTION_SERVICE_LIST.getPropertyName(), new Document()
//                            .append(SERVICE_OID.getPropertyName(), serviceId)
//                            .append(SERVICE_NAME.getPropertyName(), serviceDoc.getString(SERVICE_NAME.getPropertyName()))
//                            .append(DURATION_SERVICE.getPropertyName(), durationService)
//                            .append(MEMBER_TYPE.getPropertyName(), serviceDoc.getString(MEMBER_TYPE.getPropertyName()))
//                            .append(AMOUNT_SERVICE.getPropertyName(), serviceCost))
//                    .append(SUB_START_DATE.getPropertyName(), subStartDateMillis)
//                    .append(SUB_END_DATE.getPropertyName(), subEndDateMillis);
//
//            List<Document> addonList = new ArrayList<>();
//            for (int i = 0; i < addOns.size(); i++) {
//                JsonObject addOn = addOns.getJsonObject(i);
//                String addonIdStr = addOn.getString(ADDON_OID.getPropertyName());
//                if (addonIdStr == null || !ObjectId.isValid(addonIdStr)) {
//                    LOGGER.warn("Invalid or missing addon ID in subscription request: {}", addOn);
//                    abortTransaction(clientSession);
//                    LOGGER.info("Transaction aborted due to invalid addon ID for emailID: {}", emailId);
//                    return Future.failedFuture("Valid addon ID is required in the request");
//                }
//                ObjectId addonId = new ObjectId(addonIdStr);
//
//                // Check for existing active addon with same ID
//                for (Document existingSub : existingSubscriptions) {
//                    List<Document> existingAddons = existingSub.getList(SUBSCRIPTION_ADDON_LIST.getPropertyName(), Document.class, new ArrayList<>());
//                    for (Document existingAddon : existingAddons) {
//                        ObjectId existingAddonId = (ObjectId) existingAddon.get(ADDON_OID.getPropertyName());
//                        Long addonStartDate = existingAddon.getLong(ADDON_START_DATE.getPropertyName());
//                        Long addonEndDate = existingAddon.getLong(ADDON_END_DATE.getPropertyName());
//                        if (existingAddonId.equals(addonId) && addonStartDate <= currentDateMillis && addonEndDate > currentDateMillis) {
//                            LOGGER.warn("Addon with ID {} is already active for emailID: {} until {}", addonIdStr, emailId, DateUtils.convertMillisToDateString(addonEndDate));
//                            abortTransaction(clientSession);
//                            return Future.failedFuture("Addon with ID " + addonIdStr + " is already active until " + DateUtils.convertMillisToDateString(addonEndDate));
//                        }
//                    }
//                }
//
//                Document addonDoc = addOnCollection.find(clientSession, Filters.eq("_id", addonId)).first();
//                if (addonDoc == null) {
//                    LOGGER.warn("Addon with ID {} does not exist in ADDON_COLLECTION", addonIdStr);
//                    abortTransaction(clientSession);
//                    LOGGER.info("Transaction aborted due to non-existent addon with ID: {} for emailID: {}", addonIdStr, emailId);
//                    return Future.failedFuture("Addon with ID " + addonIdStr + " does not exist");
//                }
//                Double addonCost = addonDoc.getDouble(AMOUNT_ADDON.getPropertyName());
//                if (addonCost == null) {
//                    LOGGER.warn("Add-on cost is null for add-on ID: {}", addonIdStr);
//                    abortTransaction(clientSession);
//                    return Future.failedFuture("Add-on cost is missing or invalid for add-on ID " + addonIdStr);
//                }
//                currentTransactionCost += addonCost;
//
//                Integer durationAddon = addonDoc.getInteger(DURATION_ADDON.getPropertyName());
//                if (durationAddon == null) {
//                    LOGGER.warn("Duration is null for add-on ID: {}", addonIdStr);
//                    abortTransaction(clientSession);
//                    return Future.failedFuture("Add-on duration is missing for add-on ID " + addonIdStr);
//                }
//
//                Long addonStartDateMillis = subStartDateMillis;
//                Long addonEndDateMillis = addonStartDateMillis + (durationAddon * 24L * 60L * 60L * 1000L);
//                // Cap add-on end date to service end date if it exceeds
//                if (addonEndDateMillis > subEndDateMillis) {
//                    addonEndDateMillis = subEndDateMillis;
//                }
//
//                Document addonEntry = new Document()
//                        .append(ADDON_OID.getPropertyName(), addonId)
//                        .append(ADDON_NAME.getPropertyName(), addonDoc.getString(ADDON_NAME.getPropertyName()))
//                        .append(DESCRIPTION_ADDON.getPropertyName(), addonDoc.getString(DESCRIPTION_ADDON.getPropertyName()))
//                        .append(DURATION_ADDON.getPropertyName(), durationAddon)
//                        .append(MEMBER_TYPE.getPropertyName(), addonDoc.getString(MEMBER_TYPE.getPropertyName()))
//                        .append(AMOUNT_ADDON.getPropertyName(), addonCost)
//                        .append(ADDON_START_DATE.getPropertyName(), addonStartDateMillis)
//                        .append(ADDON_END_DATE.getPropertyName(), addonEndDateMillis);
//                addonList.add(addonEntry);
//            }
//
//            if (!addonList.isEmpty()) {
//                subscriptionDoc.append(SUBSCRIPTION_ADDON_LIST.getPropertyName(), addonList);
//            }
//
//            double totalCost = currentTransactionCost;
//            Document update = new Document();
//
//            List<Document> updatedSubscriptionList = new ArrayList<>(existingSubscriptions);
//            updatedSubscriptionList.add(subscriptionDoc);
//            update.append("$set", new Document()
//                    .append(SUBSCRIPTIONS_LIST.getPropertyName(), updatedSubscriptionList)
//                    .append(TOTAL_COST.getPropertyName(), totalCost));
//
//            boolean updated = updateDocument(GYM_USERS_COLLECTION, Filters.eq(EMAIL_ID.getPropertyName(), emailId), update, clientSession, mongoDatabase);
//            if (!updated) {
//                LOGGER.warn("Failed to update subscription details for user with emailID: {}", emailId);
//                abortTransaction(clientSession);
//                LOGGER.info("Transaction aborted due to failure in updating subscription for emailID: {}", emailId);
//                return Future.failedFuture("Failed to update subscription details");
//            }
//
//            Document updatedUser = userCollection.find(clientSession, Filters.eq(EMAIL_ID.getPropertyName(), emailId)).first();
//            Boolean currentStatus = updatedUser.getBoolean(STATUS.getPropertyName());
//            if (currentStatus == null) {
//                update = new Document("$set", new Document(STATUS.getPropertyName(), true));
//                updateDocument(GYM_USERS_COLLECTION, Filters.eq(EMAIL_ID.getPropertyName(), emailId), update, clientSession, mongoDatabase);
//                currentStatus = true;
//                LOGGER.debug("No status found for user with emailID {}, setting to true", emailId);
//            }
//            // Assuming clientSession is already started with clientSession.startTransaction()
//            Document user = userCollection.find(clientSession, Filters.eq("email", emailId)).first();
//
//            if (user != null && user.getBoolean("status") == false) {
//                clientSession.abortTransaction();
//                LOGGER.warn("Transaction aborted: User with email " + emailId + " has status false");
//
//            }
//
//
//// Proceed with the transaction if status is not false or user not found
//// e.g., clientSession.commitTransaction();
//            JsonObject memberDetails = new JsonObject()
//                    .put(USER_ID.getPropertyName(), updatedUser.getString(USER_ID.getPropertyName()))
//                    .put(NAME.getPropertyName(), updatedUser.getString(NAME.getPropertyName()))
//                    .put(MEMBER_TYPE.getPropertyName(), updatedUser.getString(USER_TYPE.getPropertyName()))
//                    .put(CONTACT_NUMBER.getPropertyName(), updatedUser.getLong(CONTACT_NUMBER.getPropertyName()))
//                    .put(EMAIL_ID.getPropertyName(), updatedUser.getString(EMAIL_ID.getPropertyName()))
//                    .put(STATUS.getPropertyName(), currentStatus)
//                    .put(SERVICE_OID.getPropertyName(), serviceIdStr)
//                    .put(SERVICE_NAME.getPropertyName(), serviceDoc.getString(SERVICE_NAME.getPropertyName()))
//                    .put(SUB_START_DATE.getPropertyName(), DateUtils.convertMillisToDateString(subStartDateMillis))
//                    .put(SUB_END_DATE.getPropertyName(), DateUtils.convertMillisToDateString(subEndDateMillis))
//                    .put(AMOUNT_PAYABLE.getPropertyName(), currentTransactionCost);
////                    .put(TOTAL_COST.getPropertyName(), totalCost);
//
//// Add add-on details (ADDON_OID and ADDON_NAME) for the current subscription
//
//            JsonArray addonDetailsArray = new JsonArray();
//            for (Document addon : addonList) {
//                JsonObject addonDetails = new JsonObject()
//                        .put(ADDON_OID.getPropertyName(), addon.get(ADDON_OID.getPropertyName()).toString())
//                        .put(ADDON_NAME.getPropertyName(), addon.getString(ADDON_NAME.getPropertyName()));
////                                .put(ADDON_START_DATE.getPropertyName(),);
//                addonDetailsArray.add(addonDetails);
//            }
//            if (!addonDetailsArray.isEmpty()) {
//                memberDetails.put(SUBSCRIPTION_ADDON_LIST.getPropertyName(), addonDetailsArray);
//            }
//            commitTransaction(clientSession);
//            LOGGER.info("Subscription updated successfully for user with emailID: {}", emailId);
//            return Future.succeededFuture(memberDetails);
//
//        } catch (MongoException e) {
//            LOGGER.error("MongoDB error while processing new subscription for emailID {}: {}", emailId, e.getMessage(), e);
//            abortTransaction(clientSession);
//            LOGGER.info("Transaction aborted due to MongoDB error for emailID: {}", emailId);
//            return Future.failedFuture("Failed to process subscription due to MongoDB error: " + e.getMessage());
//        } catch (Exception e) {
//            LOGGER.error("Failed to process new subscription for emailID {}: {}", emailId, e.getMessage(), e);
//            abortTransaction(clientSession);
//            LOGGER.info("Transaction aborted due to general error for emailID: {}", emailId);
//            return Future.failedFuture("Failed to process subscription: " + e.getMessage());
//        }
//    }
//
//    private Future<JsonObject> addNewServiceForExistingUser(String emailId, JsonObject subscription_service_addonRequest, ClientSession clientSession, Document userDoc, int activeSubIndex) {
//        try {
//            LOGGER.info("Processing subscription for existing user with emailID: {}", emailId);
//
//            List<Document> existingSubscriptions = userDoc.getList(SUBSCRIPTIONS_LIST.getPropertyName(), Document.class, new ArrayList<>());
//
//            String serviceIdStr = subscription_service_addonRequest.getString(SERVICE_OID.getPropertyName());
//            JsonArray addOns = subscription_service_addonRequest.getJsonArray(SUBSCRIPTION_ADDON_LIST.getPropertyName(), new JsonArray());
//            String preferredStartDateStr = subscription_service_addonRequest.getString("preferredStartDate");
//            Long currentDateMillis = System.currentTimeMillis();
//
//            if (serviceIdStr == null && addOns.isEmpty()) {
//                LOGGER.warn("No valid service or add-ons provided in the request for emailId: {}", emailId);
//                abortTransaction(clientSession);
//                LOGGER.info("Transaction aborted due to empty subscription data for emailId: {}", emailId);
//                return Future.failedFuture("Request must contain a service or add-ons");
//            }
//
//            double currentTransactionCost = 0.0;
//            List<Document> updatedSubscriptionList = new ArrayList<>(existingSubscriptions);
//
//            if (serviceIdStr == null && !addOns.isEmpty() && activeSubIndex != -1) {
//                Document activeSub = updatedSubscriptionList.get(activeSubIndex);
//                Document serviceDoc = activeSub.get(SUBSCRIPTION_SERVICE_LIST.getPropertyName(), Document.class);
//                double activeServiceCost = serviceDoc != null ? serviceDoc.getDouble(AMOUNT_SERVICE.getPropertyName()) : 0.0;
//                if (activeServiceCost == 0.0) {
//                    LOGGER.warn("Active service cost is missing or zero for emailId: {}", emailId);
//                    abortTransaction(clientSession);
//                    return Future.failedFuture("Active service cost is invalid");
//                }
//
//                Long subStartDateMillis = activeSub.getLong(SUB_START_DATE.getPropertyName());
//                Long subEndDateMillis = activeSub.getLong(SUB_END_DATE.getPropertyName());
//
//                // Check if this is a future subscription with no active subscription
//                if (subStartDateMillis > currentDateMillis) {
//                    LOGGER.warn("Cannot add add-ons to a future subscription without an active subscription for emailId: {}", emailId);
//                    abortTransaction(clientSession);
//                    return Future.failedFuture("Cannot add add-ons to a future subscription without an active subscription");
//                }
//
//                // Initialize addonList with only current transaction add-ons
//                List<Document> addonList = new ArrayList<>(); // Start with an empty list for new add-ons
//                for (int i = 0; i < addOns.size(); i++) {
//                    JsonObject addOn = addOns.getJsonObject(i);
//                    String addonIdStr = addOn.getString(ADDON_OID.getPropertyName());
//                    if (addonIdStr == null || !ObjectId.isValid(addonIdStr)) {
//                        LOGGER.warn("Invalid or missing addon ID in subscription request: {}", addOn);
//                        abortTransaction(clientSession);
//                        LOGGER.info("Transaction aborted due to invalid addon ID for emailId: {}", emailId);
//                        return Future.failedFuture("Valid addon ID is required in the request");
//                    }
//                    ObjectId addonId = new ObjectId(addonIdStr);
//
//                    // Check for existing active addon with same ID across all subscriptions
//                    for (Document existingSub : existingSubscriptions) {
//                        List<Document> existingAddons = existingSub.getList(SUBSCRIPTION_ADDON_LIST.getPropertyName(), Document.class, new ArrayList<>());
//                        for (Document existingAddon : existingAddons) {
//                            ObjectId existingAddonId = (ObjectId) existingAddon.get(ADDON_OID.getPropertyName());
//                            Long addonStartDate = existingAddon.getLong(ADDON_START_DATE.getPropertyName());
//                            Long addonEndDate = existingAddon.getLong(ADDON_END_DATE.getPropertyName());
//                            if (existingAddonId.equals(addonId) && addonStartDate <= currentDateMillis && addonEndDate > currentDateMillis) {
//                                LOGGER.warn("Addon with ID {} is already active for emailId: {} until {}", addonIdStr, emailId, DateUtils.convertMillisToDateString(addonEndDate));
//                                abortTransaction(clientSession);
//                                return Future.failedFuture("Addon with ID " + addonIdStr + " is already active until " + DateUtils.convertMillisToDateString(addonEndDate));
//                            }
//                        }
//                    }
//
//                    Document addonDoc = addOnCollection.find(clientSession, Filters.eq("_id", addonId)).first();
//                    if (addonDoc == null) {
//                        LOGGER.warn("Addon with ID {} does not exist in ADDON_COLLECTION", addonIdStr);
//                        abortTransaction(clientSession);
//                        LOGGER.info("Transaction aborted due to non-existent addon with ID: {} for emailId: {}", addonIdStr, emailId);
//                        return Future.failedFuture("Addon with ID " + addonIdStr + " does not exist");
//                    }
//                    Double addonCost = addonDoc.getDouble(AMOUNT_ADDON.getPropertyName());
//                    if (addonCost == null) {
//                        LOGGER.warn("Add-on cost is null for add-on ID: {}", addonIdStr);
//                        abortTransaction(clientSession);
//                        return Future.failedFuture("Add-on cost is missing or invalid for add-on ID " + addonIdStr);
//                    }
//                    currentTransactionCost += addonCost;
//
//                    Integer durationAddon = addonDoc.getInteger(DURATION_ADDON.getPropertyName());
//                    if (durationAddon == null) {
//                        LOGGER.warn("Duration is null for add-on ID: {}", addonIdStr);
//                        abortTransaction(clientSession);
//                        return Future.failedFuture("Add-on duration is missing for add-on ID " + addonIdStr);
//                    }
//
//                    Long addonStartDateMillis = currentDateMillis;
//                    Long addonEndDateMillis = addonStartDateMillis + (durationAddon * 24L * 60L * 60L * 1000L);
//                    if (addonEndDateMillis > subEndDateMillis) {
//                        addonEndDateMillis = subEndDateMillis;
//                    }
//
//                    Document addonEntry = new Document()
//                            .append(ADDON_OID.getPropertyName(), addonId)
//                            .append(ADDON_NAME.getPropertyName(), addonDoc.getString(ADDON_NAME.getPropertyName()))
//                            .append(DESCRIPTION_ADDON.getPropertyName(), addonDoc.getString(DESCRIPTION_ADDON.getPropertyName()))
//                            .append(DURATION_ADDON.getPropertyName(), durationAddon)
//                            .append(MEMBER_TYPE.getPropertyName(), addonDoc.getString(MEMBER_TYPE.getPropertyName()))
//                            .append(AMOUNT_ADDON.getPropertyName(), addonCost)
//                            .append(ADDON_START_DATE.getPropertyName(), addonStartDateMillis)
//                            .append(ADDON_END_DATE.getPropertyName(), addonEndDateMillis);
//                    addonList.add(addonEntry);
//                }
//
//                // Update the active subscription with the new add-on list, appending to existing add-ons
//                List<Document> existingAddons = activeSub.getList(SUBSCRIPTION_ADDON_LIST.getPropertyName(), Document.class, new ArrayList<>());
//                existingAddons.addAll(addonList);
//                activeSub.put(SUBSCRIPTION_ADDON_LIST.getPropertyName(), existingAddons);
//
//                // Calculate total cost for the active subscription
//                double totalCost = activeServiceCost;
//                for (Document addon : existingAddons) {
//                    Double addonCost = addon.getDouble(AMOUNT_ADDON.getPropertyName());
//                    if (addonCost != null) {
//                        totalCost += addonCost;
//                    }
//                }
//
//                Document update = new Document("$set", new Document()
//                        .append(SUBSCRIPTIONS_LIST.getPropertyName(), updatedSubscriptionList)
//                        .append(TOTAL_COST.getPropertyName(), totalCost));
//
//                boolean updated = updateDocument(GYM_USERS_COLLECTION, Filters.eq(EMAIL_ID.getPropertyName(), emailId), update, clientSession, mongoDatabase);
//                if (!updated) {
//                    LOGGER.warn("Failed to update add-on details for user with emailId: {}", emailId);
//                    abortTransaction(clientSession);
//                    LOGGER.info("Transaction aborted due to failure in updating add-ons for emailId: {}", emailId);
//                    return Future.failedFuture("Failed to update add-on details");
//                }
//
//                Document updatedUser = userCollection.find(clientSession, Filters.eq(EMAIL_ID.getPropertyName(), emailId)).first();
//                Boolean currentStatus = updatedUser.getBoolean(STATUS.getPropertyName());
//                if (currentStatus == null) {
//                    currentStatus = true;
//                    update = new Document("$set", new Document(STATUS.getPropertyName(), true));
//                    updateDocument(GYM_USERS_COLLECTION, Filters.eq(EMAIL_ID.getPropertyName(), emailId), update, clientSession, mongoDatabase);
//                    LOGGER.debug("No status found for user with emailId {}, setting to true", emailId);
//                }
//
//                JsonObject memberDetails = new JsonObject()
//                        .put(USER_ID.getPropertyName(), updatedUser.getString(USER_ID.getPropertyName()))
//                        .put(NAME.getPropertyName(), updatedUser.getString(NAME.getPropertyName()))
//                        .put(MEMBER_TYPE.getPropertyName(), updatedUser.getString(USER_TYPE.getPropertyName()))
//                        .put(CONTACT_NUMBER.getPropertyName(), updatedUser.getLong(CONTACT_NUMBER.getPropertyName()))
//                        .put(EMAIL_ID.getPropertyName(), updatedUser.getString(EMAIL_ID.getPropertyName()))
//                        .put(STATUS.getPropertyName(), currentStatus)
//                        .put(SUB_START_DATE.getPropertyName(), DateUtils.convertMillisToDateString(subStartDateMillis))
//                        .put(SUB_END_DATE.getPropertyName(), DateUtils.convertMillisToDateString(subEndDateMillis))
//                        .put(AMOUNT_PAYABLE.getPropertyName(), currentTransactionCost);
////                        .put(TOTAL_COST.getPropertyName(), totalCost);
//
//                // Add add-on details (ADDON_OID and ADDON_NAME) for the current transaction only
//                JsonArray addonDetailsArray = new JsonArray();
//                for (Document addon : addonList) {
//                    JsonObject addonDetails = new JsonObject()
//                            .put(ADDON_OID.getPropertyName(), addon.get(ADDON_OID.getPropertyName()).toString())
//                            .put(ADDON_NAME.getPropertyName(), addon.getString(ADDON_NAME.getPropertyName()));
//                    addonDetailsArray.add(addonDetails);
//                }
//                if (!addonDetailsArray.isEmpty()) {
//                    memberDetails.put(SUBSCRIPTION_ADDON_LIST.getPropertyName(), addonDetailsArray);
//                }
//                commitTransaction(clientSession);
//                LOGGER.info("Add-ons added to active subscription for user with emailId: {}", emailId);
//                return Future.succeededFuture(memberDetails);
//            }
//
//            Long sevenDaysInMillis = 7L * 24L * 60L * 60L * 1000L;
//            Long minAllowedPurchaseDateMillis = existingSubscriptions.get(activeSubIndex).getLong(SUB_END_DATE.getPropertyName()) - sevenDaysInMillis;
//            if (currentDateMillis < minAllowedPurchaseDateMillis) {
//                LOGGER.warn("Cannot purchase new service for emailId {}. Current date {} is before allowed period starting {}",
//                        emailId, DateUtils.convertMillisToDateString(currentDateMillis),
//                        DateUtils.convertMillisToDateString(minAllowedPurchaseDateMillis));
//                abortTransaction(clientSession);
//                return Future.failedFuture("New service can only be purchased within 7 days before the current service end date " +
//                        DateUtils.convertMillisToDateString(existingSubscriptions.get(activeSubIndex).getLong(SUB_END_DATE.getPropertyName())));
//            }
//
//            Document subscriptionDoc = new Document();
//            Long subStartDateMillis = null;
//            Long subEndDateMillis = null;
//
//            if (!ObjectId.isValid(serviceIdStr)) {
//                LOGGER.warn("Invalid service ID in subscription request: {}", serviceIdStr);
//                abortTransaction(clientSession);
//                LOGGER.info("Transaction aborted due to invalid service ID for emailId: {}", emailId);
//                return Future.failedFuture("Valid service ID is required in the request");
//            }
//            ObjectId serviceId = new ObjectId(serviceIdStr);
//
//            Document serviceDoc = serviceCollection.find(clientSession, Filters.eq("_id", serviceId)).first();
//            if (serviceDoc == null) {
//                LOGGER.warn("Service with ID {} does not exist in SERVICES_COLLECTION", serviceIdStr);
//                abortTransaction(clientSession);
//                LOGGER.info("Transaction aborted due to non-existent service with ID: {} for emailId: {}", serviceIdStr, emailId);
//                return Future.failedFuture("Service with ID " + serviceIdStr + " does not exist");
//            }
//            Double serviceCost = serviceDoc.getDouble(AMOUNT_SERVICE.getPropertyName());
//            if (serviceCost == null) {
//                LOGGER.warn("Service cost is null for service ID: {}", serviceIdStr);
//                abortTransaction(clientSession);
//                return Future.failedFuture("Service cost is missing or invalid for service ID " + serviceIdStr);
//            }
//            currentTransactionCost += serviceCost;
//
//            Integer durationService = serviceDoc.getInteger(DURATION_SERVICE.getPropertyName());
//            if (durationService == null) {
//                LOGGER.warn("Duration is null for service ID: {}", serviceIdStr);
//                abortTransaction(clientSession);
//                return Future.failedFuture("Service duration is missing for service ID " + serviceIdStr);
//            }
//
//            Long minAllowedStartDateMillis = existingSubscriptions.get(activeSubIndex).getLong(SUB_END_DATE.getPropertyName()) + (24L * 60L * 60L * 1000L);
//            Long maxAllowedStartDateMillis = currentDateMillis + (6L * 24L * 60L * 60L * 1000L);
//            subStartDateMillis = preferredStartDateStr != null ? DateUtils.getStartOfDayMillis(preferredStartDateStr) : minAllowedStartDateMillis;
//
//            if (subStartDateMillis == null && preferredStartDateStr != null) {
//                LOGGER.warn("Invalid preferred start date format: {}", preferredStartDateStr);
//                abortTransaction(clientSession);
//                return Future.failedFuture("Preferred start date must be in dd-MM-yyyy, yyyy-MM-dd, yyyy/MM/dd, or dd/MM/yyyy format");
//            }
//
//            // Check if preferred date is in the past
//            if (subStartDateMillis != null && subStartDateMillis < currentDateMillis) {
//                LOGGER.warn("Preferred start date {} is in the past for emailID: {}", preferredStartDateStr, emailId);
//                abortTransaction(clientSession);
//                return Future.failedFuture("Preferred start date cannot be in the past");
//            }
//
//            if (subStartDateMillis < minAllowedStartDateMillis || subStartDateMillis > maxAllowedStartDateMillis) {
//                LOGGER.warn("Preferred start date {} out of allowed range [{} to {}] for emailId: {}",
//                        preferredStartDateStr != null ? preferredStartDateStr : "null",
//                        DateUtils.convertMillisToDateString(minAllowedStartDateMillis),
//                        DateUtils.convertMillisToDateString(maxAllowedStartDateMillis), emailId);
//                abortTransaction(clientSession);
//                return Future.failedFuture("Preferred start date must be on or after the day following the current service end date (" +
//                        DateUtils.convertMillisToDateString(minAllowedStartDateMillis) +
//                        ") and within 7 days from today");
//            }
//
//            subEndDateMillis = subStartDateMillis + ((durationService - 1) * 24L * 60L * 60L * 1000L);
//
//            subscriptionDoc.append(SUBSCRIPTION_SERVICE_LIST.getPropertyName(), new Document()
//                            .append(SERVICE_OID.getPropertyName(), serviceId)
//                            .append(SERVICE_NAME.getPropertyName(), serviceDoc.getString(SERVICE_NAME.getPropertyName()))
//                            .append(DURATION_SERVICE.getPropertyName(), durationService)
//                            .append(MEMBER_TYPE.getPropertyName(), serviceDoc.getString(MEMBER_TYPE.getPropertyName()))
//                            .append(AMOUNT_SERVICE.getPropertyName(), serviceCost))
//                    .append(SUB_START_DATE.getPropertyName(), subStartDateMillis)
//                    .append(SUB_END_DATE.getPropertyName(), subEndDateMillis);
//
//            List<Document> addonList = new ArrayList<>();
//            Document activeSub = existingSubscriptions.get(activeSubIndex);
//            List<Document> activeAddons = activeSub.getList(SUBSCRIPTION_ADDON_LIST.getPropertyName(), Document.class, new ArrayList<>());
//
//            for (int i = 0; i < addOns.size(); i++) {
//                JsonObject addOn = addOns.getJsonObject(i);
//                String addonIdStr = addOn.getString(ADDON_OID.getPropertyName());
//                if (addonIdStr == null || !ObjectId.isValid(addonIdStr)) {
//                    LOGGER.warn("Invalid or missing addon ID in subscription request: {}", addOn);
//                    abortTransaction(clientSession);
//                    LOGGER.info("Transaction aborted due to invalid addon ID for emailId: {}", emailId);
//                    return Future.failedFuture("Valid addon ID is required in the request");
//                }
//                ObjectId addonId = new ObjectId(addonIdStr);
//
//                // Check if within 7 days before active subscription end date
//                Long activeSubEndDate = existingSubscriptions.get(activeSubIndex).getLong(SUB_END_DATE.getPropertyName());
//                if (currentDateMillis >= (activeSubEndDate - sevenDaysInMillis) && currentDateMillis <= activeSubEndDate) {
//                    // Allow existing active add-ons to be reused
//                    boolean isExistingAddon = false;
//                    for (Document existingAddon : activeAddons) {
//                        ObjectId existingAddonId = (ObjectId) existingAddon.get(ADDON_OID.getPropertyName());
//                        Long addonEndDate = existingAddon.getLong(ADDON_END_DATE.getPropertyName());
//                        if (existingAddonId.equals(addonId) && addonEndDate > currentDateMillis) {
//                            isExistingAddon = true;
//                            // Reuse the existing add-on details but adjust dates
//                            Long addonStartDateMillis = subStartDateMillis;
//                            Long addonEndDateMillis = addonStartDateMillis + (existingAddon.getInteger(DURATION_ADDON.getPropertyName()) * 24L * 60L * 60L * 1000L);
//                            if (addonEndDateMillis > subEndDateMillis) {
//                                addonEndDateMillis = subEndDateMillis;
//                            }
//                            Document addonEntry = new Document()
//                                    .append(ADDON_OID.getPropertyName(), addonId)
//                                    .append(ADDON_NAME.getPropertyName(), existingAddon.getString(ADDON_NAME.getPropertyName()))
//                                    .append(DESCRIPTION_ADDON.getPropertyName(), existingAddon.getString(DESCRIPTION_ADDON.getPropertyName()))
//                                    .append(DURATION_ADDON.getPropertyName(), existingAddon.getInteger(DURATION_ADDON.getPropertyName()))
//                                    .append(MEMBER_TYPE.getPropertyName(), existingAddon.getString(MEMBER_TYPE.getPropertyName()))
//                                    .append(AMOUNT_ADDON.getPropertyName(), existingAddon.getDouble(AMOUNT_ADDON.getPropertyName()))
//                                    .append(ADDON_START_DATE.getPropertyName(), addonStartDateMillis)
//                                    .append(ADDON_END_DATE.getPropertyName(), addonEndDateMillis);
//                            addonList.add(addonEntry);
//                            currentTransactionCost += existingAddon.getDouble(AMOUNT_ADDON.getPropertyName());
//                            LOGGER.info("Reusing existing active addon with ID {} for emailId: {}", addonIdStr, emailId);
//                            break;
//                        }
//                    }
//                    if (isExistingAddon) {
//                        continue; // Skip to next add-on if this one was reused
//                    }
//                }
//
//                // Check for existing active addon with same ID across all subscriptions
//                for (Document existingSub : existingSubscriptions) {
//                    List<Document> existingAddonsCheck = existingSub.getList(SUBSCRIPTION_ADDON_LIST.getPropertyName(), Document.class, new ArrayList<>());
//                    for (Document existingAddon : existingAddonsCheck) {
//                        ObjectId existingAddonId = (ObjectId) existingAddon.get(ADDON_OID.getPropertyName());
//                        Long addonStartDate = existingAddon.getLong(ADDON_START_DATE.getPropertyName());
//                        Long addonEndDate = existingAddon.getLong(ADDON_END_DATE.getPropertyName());
//                        if (existingAddonId.equals(addonId) && addonStartDate <= currentDateMillis && addonEndDate > currentDateMillis) {
//                            LOGGER.warn("Addon with ID {} is already active for emailId: {} until {}", addonIdStr, emailId, DateUtils.convertMillisToDateString(addonEndDate));
//                            abortTransaction(clientSession);
//                            return Future.failedFuture("Addon with ID " + addonIdStr + " is already active until " + DateUtils.convertMillisToDateString(addonEndDate));
//                        }
//                    }
//                }
//
//                Document addonDoc = addOnCollection.find(clientSession, Filters.eq("_id", addonId)).first();
//                if (addonDoc == null) {
//                    LOGGER.warn("Addon with ID {} does not exist in ADDON_COLLECTION", addonIdStr);
//                    abortTransaction(clientSession);
//                    LOGGER.info("Transaction aborted due to non-existent addon with ID: {} for emailId: {}", addonIdStr, emailId);
//                    return Future.failedFuture("Addon with ID " + addonIdStr + " does not exist");
//                }
//                Double addonCost = addonDoc.getDouble(AMOUNT_ADDON.getPropertyName());
//                if (addonCost == null) {
//                    LOGGER.warn("Add-on cost is null for add-on ID: {}", addonIdStr);
//                    abortTransaction(clientSession);
//                    return Future.failedFuture("Add-on cost is missing or invalid for add-on ID " + addonIdStr);
//                }
//                currentTransactionCost += addonCost;
//
//                Integer durationAddon = addonDoc.getInteger(DURATION_ADDON.getPropertyName());
//                if (durationAddon == null) {
//                    LOGGER.warn("Duration is null for add-on ID: {}", addonIdStr);
//                    abortTransaction(clientSession);
//                    return Future.failedFuture("Add-on duration is missing for add-on ID " + addonIdStr);
//                }
//
//                Long addonStartDateMillis = subStartDateMillis;
//                Long addonEndDateMillis = addonStartDateMillis + (durationAddon * 24L * 60L * 60L * 1000L);
//                // Cap add-on end date to service end date if it exceeds
//                if (addonEndDateMillis > subEndDateMillis) {
//                    addonEndDateMillis = subEndDateMillis;
//                }
//
//                Document addonEntry = new Document()
//                        .append(ADDON_OID.getPropertyName(), addonId)
//                        .append(ADDON_NAME.getPropertyName(), addonDoc.getString(ADDON_NAME.getPropertyName()))
//                        .append(DESCRIPTION_ADDON.getPropertyName(), addonDoc.getString(DESCRIPTION_ADDON.getPropertyName()))
//                        .append(DURATION_ADDON.getPropertyName(), durationAddon)
//                        .append(MEMBER_TYPE.getPropertyName(), addonDoc.getString(MEMBER_TYPE.getPropertyName()))
//                        .append(AMOUNT_ADDON.getPropertyName(), addonCost)
//                        .append(ADDON_START_DATE.getPropertyName(), addonStartDateMillis)
//                        .append(ADDON_END_DATE.getPropertyName(), addonEndDateMillis);
//                addonList.add(addonEntry);
//            }
//
//            if (!addonList.isEmpty()) {
//                subscriptionDoc.append(SUBSCRIPTION_ADDON_LIST.getPropertyName(), addonList);
//            }
//
//            double totalCost = currentTransactionCost;
//            Document update = new Document();
//
//            updatedSubscriptionList.add(subscriptionDoc);
//            update.append("$set", new Document()
//                    .append(SUBSCRIPTIONS_LIST.getPropertyName(), updatedSubscriptionList)
//                    .append(TOTAL_COST.getPropertyName(), totalCost));
//
//            boolean updated = updateDocument(GYM_USERS_COLLECTION, Filters.eq(EMAIL_ID.getPropertyName(), emailId), update, clientSession, mongoDatabase);
//            if (!updated) {
//                LOGGER.warn("Failed to update subscription details for user with emailId: {}", emailId);
//                abortTransaction(clientSession);
//                LOGGER.info("Transaction aborted due to failure in updating subscription for emailId: {}", emailId);
//                return Future.failedFuture("Failed to update subscription details");
//            }
//
//            Document updatedUser = userCollection.find(clientSession, Filters.eq(EMAIL_ID.getPropertyName(), emailId)).first();
//            Boolean currentStatus = updatedUser.getBoolean(STATUS.getPropertyName());
//            if (currentStatus == null) {
//                currentStatus = true;
//                update = new Document("$set", new Document(STATUS.getPropertyName(), true));
//                updateDocument(GYM_USERS_COLLECTION, Filters.eq(EMAIL_ID.getPropertyName(), emailId), update, clientSession, mongoDatabase);
//                LOGGER.debug("No status found for user with emailId {}, setting to true", emailId);
//            }
//
//            JsonObject memberDetails = new JsonObject()
//                    .put(USER_ID.getPropertyName(), updatedUser.getString(USER_ID.getPropertyName()))
//                    .put(NAME.getPropertyName(), updatedUser.getString(NAME.getPropertyName()))
//                    .put(MEMBER_TYPE.getPropertyName(), updatedUser.getString(USER_TYPE.getPropertyName()))
//                    .put(CONTACT_NUMBER.getPropertyName(), updatedUser.getLong(CONTACT_NUMBER.getPropertyName()))
//                    .put(EMAIL_ID.getPropertyName(), updatedUser.getString(EMAIL_ID.getPropertyName()))
//                    .put(SERVICE_OID.getPropertyName(), serviceIdStr)
//                    .put(SERVICE_NAME.getPropertyName(), serviceDoc.getString(SERVICE_NAME.getPropertyName()))
////                    .put(SUB_START_DATE.getPropertyName(),DateUtils.convertMillisToDateString())
//                    .put(AMOUNT_PAYABLE.getPropertyName(), currentTransactionCost)

    /// /                    .put(TOTAL_COST.getPropertyName(), totalCost)
//                    .put(STATUS.getPropertyName(), currentStatus);
//
//            // Add add-on details (ADDON_OID and ADDON_NAME) to memberDetails
//            JsonArray addonDetailsArray = new JsonArray();
//            for (Document addon : addonList) {
//                JsonObject addonDetails = new JsonObject()
//                        .put(ADDON_OID.getPropertyName(), addon.get(ADDON_OID.getPropertyName()).toString())
//                        .put(ADDON_NAME.getPropertyName(), addon.getString(ADDON_NAME.getPropertyName()));
//                addonDetailsArray.add(addonDetails);
//            }
//            if (!addonDetailsArray.isEmpty()) {
//                memberDetails.put(SUBSCRIPTION_ADDON_LIST.getPropertyName(), addonDetailsArray);
//            }
//
//            commitTransaction(clientSession);
//            LOGGER.info("New service subscription added for user with emailId: {}", emailId);
//            return Future.succeededFuture(memberDetails);
//
//        } catch (MongoException e) {
//            LOGGER.error("MongoDB error while processing subscription for emailId {}: {}", emailId, e.getMessage(), e);
//            abortTransaction(clientSession);
//            LOGGER.info("Transaction aborted due to MongoDB error for emailId: {}", emailId);
//            return Future.failedFuture("Failed to process subscription due to MongoDB error: " + e.getMessage());
//        } catch (Exception e) {
//            LOGGER.error("Failed to process subscription for emailId {}: {}", emailId, e.getMessage(), e);
//            abortTransaction(clientSession);
//            LOGGER.info("Transaction aborted due to general error for emailId: {}", emailId);
//            return Future.failedFuture("Failed to process subscription: " + e.getMessage());
//        }
//    }
//
//    public Future<Void> updateUserStatus(String emailId, String statusText) {
//        ClientSession clientSession = getMongoDbSession(this.mongoClient);
//        try {
//            startTransaction(clientSession);
//            if (statusText == null || statusText.trim().isEmpty()) {
//                abortTransaction(clientSession);
//                return Future.failedFuture("Status text cannot be null or empty");
//            }
//
//            if ("Inactive".equalsIgnoreCase(statusText.trim())) {
//                Document update = new Document("$set", new Document(STATUS.getPropertyName(), false));
//                boolean updated = updateDocument(GYM_USERS_COLLECTION, Filters.eq(EMAIL_ID.getPropertyName(), emailId.toUpperCase()), update, clientSession, mongoDatabase);
//                if (!updated) {
//                    abortTransaction(clientSession);
//                    return Future.failedFuture("Failed to update status for user with emailID: " + emailId);
//                }
//                LOGGER.info("User status updated to 'Inactive' for emailID: {}", emailId);
//            } else if ("Active".equalsIgnoreCase(statusText.trim())) {
//                Document update = new Document("$set", new Document(STATUS.getPropertyName(), true));
//                boolean updated = updateDocument(GYM_USERS_COLLECTION, Filters.eq(EMAIL_ID.getPropertyName(), emailId.toUpperCase()), update, clientSession, mongoDatabase);
//                if (!updated) {
//                    abortTransaction(clientSession);
//                    return Future.failedFuture("Failed to update status for user with emailID: " + emailId);
//                }
//                LOGGER.info("User status updated to 'Active' for emailID: {}", emailId);
//            } else {
//                LOGGER.info("Status text '{}' not 'Active' or 'Inactive', no status change for emailID: {}", statusText, emailId);
//            }
//
//            commitTransaction(clientSession);
//            return Future.succeededFuture();
//        } catch (MongoException e) {
//            abortTransaction(clientSession);
//            LOGGER.error("MongoDB error updating status for emailID {}: {}", emailId, e.getMessage(), e);
//            return Future.failedFuture("MongoDB error: " + e.getMessage());
//        } finally {
//            if (clientSession != null) {
//                clientSession.close();
//            }
//        }
//    }
    private Future<Document> addSubscriptionForNewUser(String emailId, JsonObject subscription_service_addonRequest, ClientSession clientSession, Document userDoc) {
        try {
            LOGGER.info("Processing new subscription for emailID: {}", emailId);

            // Modified to use new key names
            String serviceIdStr = subscription_service_addonRequest.getString("serviceOid_Gym_ObjectId");
            JsonArray addOns = subscription_service_addonRequest.getJsonArray(SUBSCRIPTION_ADDON_LIST.getPropertyName(), new JsonArray());
            String preferredStartDateStr = subscription_service_addonRequest.getString("preferredStartDate_Gym_Date");
            Long currentDateMillis = System.currentTimeMillis();
            Long preferredStartDateMillis = preferredStartDateStr != null ? DateUtils.getStartOfDayMillis(preferredStartDateStr) : currentDateMillis;
            Long maxAllowedStartDateMillis = currentDateMillis + (6L * 24L * 60L * 60L * 1000L);

            if (preferredStartDateMillis == null && preferredStartDateStr != null) {
                LOGGER.warn("Invalid preferred start date format: {}", preferredStartDateStr);
                abortTransaction(clientSession);
                return Future.failedFuture(String.valueOf(new Document("error", "Preferred start date must be in dd-MM-yyyy, yyyy-MM-dd, yyyy/MM/dd, or dd/MM/yyyy format")));
            }

            if (preferredStartDateMillis != null && preferredStartDateMillis < currentDateMillis - (1L * 24L * 60L * 60L * 1000L)) {
                LOGGER.warn("Preferred start date {} is in the past for emailID: {}", preferredStartDateStr, emailId);
                abortTransaction(clientSession);
                return Future.failedFuture(String.valueOf(new Document("error", "Preferred start date cannot be in the past")));
            }

            Long subStartDateMillis = preferredStartDateMillis != null ? preferredStartDateMillis : currentDateMillis;
            if (subStartDateMillis > maxAllowedStartDateMillis) {
                LOGGER.warn("Preferred start date {} out of allowed range [{} to {}] for emailID: {}",
                        preferredStartDateStr, DateUtils.convertMillisToDateString(currentDateMillis),
                        DateUtils.convertMillisToDateString(maxAllowedStartDateMillis), emailId);
                abortTransaction(clientSession);
                return Future.failedFuture(String.valueOf(new Document("error", "Preferred start date must be within 7 days from today")));
            }

            if (serviceIdStr == null && addOns.isEmpty()) {
                LOGGER.warn("No service or add-ons provided in the request for emailID: {}", emailId);
                abortTransaction(clientSession);
                LOGGER.info("Transaction aborted due to missing service and add-ons for emailID: {}", emailId);
                return Future.failedFuture(String.valueOf(new Document("error", "At least a service or add-ons are required for new users")));
            }

            List<Document> existingSubscriptions = userDoc.getList(SUBSCRIPTIONS_LIST.getPropertyName(), Document.class, new ArrayList<>());
            System.out.println("existing" + existingSubscriptions);
            boolean allSubscriptionsEnded = true;
            for (Document sub : existingSubscriptions) {
                Long subEndDate = sub.getLong(SUB_END_DATE.getPropertyName());
                if (subEndDate != null && subEndDate >= currentDateMillis) {
                    allSubscriptionsEnded = false;
                    break;
                }
            }
            LOGGER.debug("All subscriptions ended for emailID {}: {}", emailId, allSubscriptionsEnded);
            Document subscriptionDoc = new Document();
            double currentTransactionCost = 0.0;
            Long subEndDateMillis = null;
            Document serviceDoc = null;
            ObjectId serviceId = null;

            if (serviceIdStr != null) {
                if (!ObjectId.isValid(serviceIdStr)) {
                    LOGGER.warn("Invalid service ID in subscription request: {}", serviceIdStr);
                    abortTransaction(clientSession);
                    LOGGER.info("Transaction aborted due to invalid service ID for emailID: {}", emailId);
                    return Future.failedFuture(String.valueOf(new Document("error", "Valid service ID is required in the request")));
                }
                serviceId = new ObjectId(serviceIdStr);

                serviceDoc = serviceCollection.find(clientSession, Filters.eq("_id", serviceId)).first();
                if (serviceDoc == null) {
                    LOGGER.warn("Service with ID {} does not exist in SERVICES_COLLECTION", serviceIdStr);
                    abortTransaction(clientSession);
                    LOGGER.info("Transaction aborted due to non-existent service with ID: {} for emailID: {}", serviceIdStr, emailId);
                    return Future.failedFuture(String.valueOf(new Document("error", "Service with ID " + serviceIdStr + " does not exist")));
                }
                Double serviceCost = serviceDoc.getDouble(AMOUNT_SERVICE.getPropertyName());
                if (serviceCost == null) {
                    LOGGER.warn("Service cost is null for service ID: {}", serviceIdStr);
                    abortTransaction(clientSession);
                    return Future.failedFuture(String.valueOf(new Document("error", "Service cost is missing or invalid for service ID " + serviceIdStr)));
                }
                currentTransactionCost += serviceCost;

                Integer durationService = serviceDoc.getInteger(DURATION_SERVICE.getPropertyName());
                if (durationService == null) {
                    LOGGER.warn("Duration is null for service ID: {}", serviceIdStr);
                    abortTransaction(clientSession);
                    return Future.failedFuture(String.valueOf(new Document("error", "Service duration is missing for service ID " + serviceIdStr)));
                }
                subEndDateMillis = subStartDateMillis + ((durationService - 1) * 24L * 60L * 60L * 1000L);

                subscriptionDoc.append(SUBSCRIPTION_SERVICE_LIST.getPropertyName(), new Document()
                                .append(SERVICE_OID.getPropertyName(), serviceId)
                                .append(SERVICE_NAME.getPropertyName(), serviceDoc.getString(SERVICE_NAME.getPropertyName()))
                                .append(DURATION_SERVICE.getPropertyName(), durationService)
                                .append(MEMBER_TYPE.getPropertyName(), serviceDoc.getString(MEMBER_TYPE.getPropertyName()))
                                .append(AMOUNT_SERVICE.getPropertyName(), serviceCost))
                        .append(SUB_START_DATE.getPropertyName(), subStartDateMillis)
                        .append(SUB_END_DATE.getPropertyName(), subEndDateMillis);
            }
            System.out.println("hioiiiiii" + addOns);
            /// addon objectID Array
//            List<Document> addonList = new ArrayList<>();
//            LOGGER.debug("Input addOns array: {}", addOns.encodePrettily()); // Debug input array
//            if (addOns == null || addOns.isEmpty()) {
//                LOGGER.warn("No add-ons provided in the request for emailID: {}", emailId);
//            } else {
//                for (int i = 0; i < addOns.size(); i++) {
//                    LOGGER.debug("Processing add-on index: {}", i); // Debug loop entry
//                    JsonObject addOn = addOns.getJsonObject(i);
//                    if (addOn == null) {
//                        LOGGER.warn("Null add-on object at index {} for emailID: {}", i, emailId);
//                        continue; // Skip null entries
//                    }
//                    String addonIdStr = addOn.getString(ADDON_OID.getPropertyName());
//                    LOGGER.debug("Add-on ID: {}", addonIdStr); // Debug add-on ID
//                    if (addonIdStr == null || !ObjectId.isValid(addonIdStr)) {
//                        LOGGER.warn("Invalid or missing addon ID in subscription request: {}", addOn);
//                        abortTransaction(clientSession);
//                        LOGGER.info("Transaction aborted due to invalid addon ID for emailID: {}", emailId);
//                        return Future.failedFuture(String.valueOf(new Document("error", "Valid addon ID is required in the request")));
//                    }
//                    ObjectId addonId = new ObjectId(addonIdStr);
//                    System.out.println("addon" + addonId);
//
//                    // Check for existing active addon with same ID
//                    for (Document existingSub : existingSubscriptions) {
//                        List<Document> existingAddons = existingSub.getList(SUBSCRIPTION_ADDON_LIST.getPropertyName(), Document.class, new ArrayList<>());
//                        for (Document existingAddon : existingAddons) {
//                            ObjectId existingAddonId = (ObjectId) existingAddon.get(ADDON_OID.getPropertyName());
//                            Long addonStartDate = existingAddon.getLong(ADDON_START_DATE.getPropertyName());
//                            Long addonEndDate = existingAddon.getLong(ADDON_END_DATE.getPropertyName());
//                            if (existingAddonId.equals(addonId) && addonStartDate <= currentDateMillis && addonEndDate > currentDateMillis) {
//                                LOGGER.warn("Addon with ID {} is already active for emailID: {} until {}", addonIdStr, emailId, DateUtils.convertMillisToDateString(addonEndDate));
//                                abortTransaction(clientSession);
//                                return Future.failedFuture(String.valueOf(new Document("error", "Addon with ID " + addonIdStr + " is already active until " + DateUtils.convertMillisToDateString(addonEndDate))));
//                            }
//                        }
//                    }
//
//                    Document addonDoc = addOnCollection.find(clientSession, Filters.eq("_id", addonId)).first();
//                    if (addonDoc == null) {
//                        LOGGER.warn("Addon with ID {} does not exist in ADDON_COLLECTION", addonIdStr);
//                        abortTransaction(clientSession);
//                        LOGGER.info("Transaction aborted due to non-existent addon with ID: {} for emailID: {}", addonIdStr, emailId);
//                        return Future.failedFuture(String.valueOf(new Document("error", "Addon with ID " + addonIdStr + " does not exist")));
//                    }
//                    Double addonCost = addonDoc.getDouble(AMOUNT_ADDON.getPropertyName());
//                    if (addonCost == null) {
//                        LOGGER.warn("Add-on cost is null for add-on ID: {}", addonIdStr);
//                        abortTransaction(clientSession);
//                        return Future.failedFuture(String.valueOf(new Document("error", "Add-on cost is missing or invalid for add-on ID " + addonIdStr)));
//                    }
//                    currentTransactionCost += addonCost;
//
//                    Integer durationAddon = addonDoc.getInteger(DURATION_ADDON.getPropertyName());
//                    if (durationAddon == null) {
//                        LOGGER.warn("Duration is null for add-on ID: {}", addonIdStr);
//                        abortTransaction(clientSession);
//                        return Future.failedFuture(String.valueOf(new Document("error", "Add-on duration is missing for add-on ID " + addonIdStr)));
//                    }
//
//                    Long addonStartDateMillis = subStartDateMillis;
//                    Long addonEndDateMillis = addonStartDateMillis + (durationAddon * 24L * 60L * 60L * 1000L);
//                    // Cap add-on end date to service end date if it exceeds
//                    if (serviceIdStr != null && addonEndDateMillis > subEndDateMillis) {
//                        addonEndDateMillis = subEndDateMillis;
//                    } else if (serviceIdStr == null) {
//                        // For add-ons only, set subscription dates
//                        subEndDateMillis = addonStartDateMillis + (durationAddon * 24L * 60L * 60L * 1000L);
//                        subscriptionDoc.append(SUB_START_DATE.getPropertyName(), subStartDateMillis)
//                                .append(SUB_END_DATE.getPropertyName(), subEndDateMillis);
//                    }
//
//                    Document addonEntry = new Document()
//                            .append(ADDON_OID.getPropertyName(), addonId)
//                            .append(ADDON_NAME.getPropertyName(), addonDoc.getString(ADDON_NAME.getPropertyName()))
//                            .append(DESCRIPTION_ADDON.getPropertyName(), addonDoc.getString(DESCRIPTION_ADDON.getPropertyName()))
//                            .append(DURATION_ADDON.getPropertyName(), durationAddon)
//                            .append(MEMBER_TYPE.getPropertyName(), addonDoc.getString(MEMBER_TYPE.getPropertyName()))
//                            .append(AMOUNT_ADDON.getPropertyName(), addonCost)
//                            .append(ADDON_START_DATE.getPropertyName(), addonStartDateMillis)
//                            .append(ADDON_END_DATE.getPropertyName(), addonEndDateMillis);
//                    addonList.add(addonEntry);
//                    LOGGER.debug("Added add-on to addonList: {}", addonEntry.toJson()); // Debug added entry
//                }
//            }

            /// old method with document array
            List<Document> addonList = new ArrayList<>();
            for (int i = 0; i < addOns.size(); i++) {
                JsonObject addOn = addOns.getJsonObject(i);
                String addonIdStr = addOn.getString(ADDON_OID.getPropertyName());
                if (addonIdStr == null || !ObjectId.isValid(addonIdStr)) {
                    LOGGER.warn("Invalid or missing addon ID in subscription request: {}", addOn);
                    abortTransaction(clientSession);
                    LOGGER.info("Transaction aborted due to invalid addon ID for emailID: {}", emailId);
                    return Future.failedFuture("Valid addon ID is required in the request");
                }
                ObjectId addonId = new ObjectId(addonIdStr);

                // Check for existing active addon with same ID
                for (Document existingSub : existingSubscriptions) {
                    List<Document> existingAddons = existingSub.getList(SUBSCRIPTION_ADDON_LIST.getPropertyName(), Document.class, new ArrayList<>());
                    for (Document existingAddon : existingAddons) {
                        ObjectId existingAddonId = (ObjectId) existingAddon.get(ADDON_OID.getPropertyName());
                        Long addonStartDate = existingAddon.getLong(ADDON_START_DATE.getPropertyName());
                        Long addonEndDate = existingAddon.getLong(ADDON_END_DATE.getPropertyName());
                        if (existingAddonId.equals(addonId) && addonStartDate <= currentDateMillis && addonEndDate > currentDateMillis) {
                            LOGGER.warn("Addon with ID {} is already active for emailID: {} until {}", addonIdStr, emailId, DateUtils.convertMillisToDateString(addonEndDate));
                            abortTransaction(clientSession);
                            return Future.failedFuture("Addon with ID " + addonIdStr + " is already active until " + DateUtils.convertMillisToDateString(addonEndDate));
                        }
                    }
                }

                Document addonDoc = addOnCollection.find(clientSession, Filters.eq("_id", addonId)).first();
                if (addonDoc == null) {
                    LOGGER.warn("Addon with ID {} does not exist in ADDON_COLLECTION", addonIdStr);
                    abortTransaction(clientSession);
                    LOGGER.info("Transaction aborted due to non-existent addon with ID: {} for emailID: {}", addonIdStr, emailId);
                    return Future.failedFuture("Addon with ID " + addonIdStr + " does not exist");
                }
                Double addonCost = addonDoc.getDouble(AMOUNT_ADDON.getPropertyName());
                if (addonCost == null) {
                    LOGGER.warn("Add-on cost is null for add-on ID: {}", addonIdStr);
                    abortTransaction(clientSession);
                    return Future.failedFuture("Add-on cost is missing or invalid for add-on ID " + addonIdStr);
                }
                currentTransactionCost += addonCost;

                Integer durationAddon = addonDoc.getInteger(DURATION_ADDON.getPropertyName());
                if (durationAddon == null) {
                    LOGGER.warn("Duration is null for add-on ID: {}", addonIdStr);
                    abortTransaction(clientSession);
                    return Future.failedFuture("Add-on duration is missing for add-on ID " + addonIdStr);
                }

                Long addonStartDateMillis = subStartDateMillis;
                Long addonEndDateMillis = addonStartDateMillis + (durationAddon * 24L * 60L * 60L * 1000L);
                // Cap add-on end date to service end date if it exceeds
                if (addonEndDateMillis > subEndDateMillis) {
                    addonEndDateMillis = subEndDateMillis;
                }

                Document addonEntry = new Document()
                        .append(ADDON_OID.getPropertyName(), addonId)
                        .append(ADDON_NAME.getPropertyName(), addonDoc.getString(ADDON_NAME.getPropertyName()))
                        .append(DESCRIPTION_ADDON.getPropertyName(), addonDoc.getString(DESCRIPTION_ADDON.getPropertyName()))
                        .append(DURATION_ADDON.getPropertyName(), durationAddon)
                        .append(MEMBER_TYPE.getPropertyName(), addonDoc.getString(MEMBER_TYPE.getPropertyName()))
                        .append(AMOUNT_ADDON.getPropertyName(), addonCost)
                        .append(ADDON_START_DATE.getPropertyName(), addonStartDateMillis)
                        .append(ADDON_END_DATE.getPropertyName(), addonEndDateMillis);
                addonList.add(addonEntry);
            }

            if (!addonList.isEmpty()) {
                subscriptionDoc.append(SUBSCRIPTION_ADDON_LIST.getPropertyName(), addonList);
            }
            System.out.println("addon list" + addonList);
            if (!addonList.isEmpty()) {
                subscriptionDoc.append(SUBSCRIPTION_ADDON_LIST.getPropertyName(), addonList);
            }


            double totalCost = currentTransactionCost;
            Document update = new Document();

            List<Document> updatedSubscriptionList = new ArrayList<>(existingSubscriptions);
            updatedSubscriptionList.add(subscriptionDoc);
            update.append("$set", new Document()
                    .append(SUBSCRIPTIONS_LIST.getPropertyName(), updatedSubscriptionList)
                    .append(TOTAL_COST.getPropertyName(), totalCost));

            // Commented code remains commented
        /*
        MongoCollection<Document> orderDetailsCollection = mongoDatabase.getCollection(GYM_ORDER_DETAILS_COLLECTION);
        Document orderDoc = orderDetailsCollection.find(clientSession, Filters.eq(EMAIL_ID.getPropertyName(), emailId.toUpperCase()))
                .sort(new Document("_id", -1))
                .limit(1)
                .first();

        Document subscriptionEntry = new Document()
                .append(AMOUNT_PAYABLE.getPropertyName(), currentTransactionCost);

        if (serviceIdStr != null && serviceDoc != null) {
            subscriptionEntry.append("serviceId", serviceId.toString())
                    .append("serviceName", serviceDoc.getString(SERVICE_NAME.getPropertyName()))
                    .append("subStartDate", DateUtils.convertMillisToDateString(subStartDateMillis))
                    .append("subEndDate", DateUtils.convertMillisToDateString(subEndDateMillis))
                    .append("serviceAmount", serviceDoc.getDouble(AMOUNT_SERVICE.getPropertyName()));
        }

        List<Document> addonEntries = new ArrayList<>();
        for (Document addon : addonList) {
            addonEntries.add(new Document()
                    .append("addonId", addon.get(ADDON_OID.getPropertyName()).toString())
                    .append("addonName", addon.getString(ADDON_NAME.getPropertyName()))
                    .append("addonAmount", addon.getDouble(AMOUNT_ADDON.getPropertyName()))
                    .append("addonStartDate", DateUtils.convertMillisToDateString(addon.getLong(ADDON_START_DATE.getPropertyName())))
                    .append("addonEndDate", DateUtils.convertMillisToDateString(addon.getLong(ADDON_END_DATE.getPropertyName()))));
        }
        if (!addonEntries.isEmpty()) {
            subscriptionEntry.append("addons", addonEntries);
        }

        if (orderDoc == null) {
            LOGGER.warn("No document found in GYM_ORDER_DETAILS_COLLECTION for emailID: {}", emailId);
            abortTransaction(clientSession);
            LOGGER.info("Transaction aborted due to missing document for emailID: {}", emailId);
            return Future.failedFuture("No document found in GYM_ORDER_DETAILS_COLLECTION for emailID: " + emailId);
        }

        Document orderUpdate = new Document("$push", new Document(SUBSCRIPTIONS_LIST.getPropertyName(), subscriptionEntry));
        boolean orderDetailsUpdated = updateDocument(GYM_ORDER_DETAILS_COLLECTION,
                Filters.eq("_id", orderDoc.getObjectId("_id")),
                orderUpdate, clientSession, mongoDatabase);
        if (!orderDetailsUpdated) {
            LOGGER.warn("Failed to update subscription details in GYM_ORDER_DETAILS_COLLECTION for emailID: {}", emailId);
            abortTransaction(clientSession);
            LOGGER.info("Transaction aborted due to failure in updating order details for emailID: {}", emailId);
            return Future.failedFuture("Failed to update order details");
        }
        */

        /*
        boolean updated = updateDocument(GYM_USERS_COLLECTION, Filters.eq(EMAIL_ID.getPropertyName(), emailId), update, clientSession, mongoDatabase);
        if (!updated) {
            LOGGER.warn("Failed to update subscription details for user with emailID: {}", emailId);
            abortTransaction(clientSession);
            LOGGER.info("Transaction aborted due to failure in updating subscription for emailID: {}", emailId);
            return Future.failedFuture("Failed to update subscription details");
        }
        */

            Document updatedUser = userCollection.find(clientSession, Filters.eq(EMAIL_ID.getPropertyName(), emailId)).first();
            Boolean currentStatus = updatedUser.getBoolean(STATUS.getPropertyName());
            if (currentStatus == null) {
                update = new Document("$set", new Document(STATUS.getPropertyName(), true));
                updateDocument(GYM_USERS_COLLECTION, Filters.eq(EMAIL_ID.getPropertyName(), emailId), update, clientSession, mongoDatabase);
                currentStatus = true;
                LOGGER.debug("No status found for user with emailID {}, setting to true", emailId);
            }

            Document user = userCollection.find(clientSession, Filters.eq("email", emailId)).first();
            if (user != null && user.getBoolean("status") == false) {
                clientSession.abortTransaction();
                LOGGER.warn("Transaction aborted: User with email " + emailId + " has status false");
                return Future.failedFuture(String.valueOf(new Document("error", "Transaction aborted: User with email " + emailId + " has status false")));
            }

            // Modified to use new key names in memberDetails
            JsonObject memberDetails = new JsonObject()
                    .put(USER_ID.getPropertyName(), updatedUser.getString(USER_ID.getPropertyName()))
                    .put(NAME.getPropertyName(), updatedUser.getString(NAME.getPropertyName()))
                    .put(MEMBER_TYPE.getPropertyName(), updatedUser.getString(USER_TYPE.getPropertyName()))
                    .put(CONTACT_NUMBER.getPropertyName(), updatedUser.getLong(CONTACT_NUMBER.getPropertyName()))
                    .put(EMAIL_ID.getPropertyName(), emailId)
                    .put("serviceOid_Gym_ObjectId", serviceIdStr)
                    .put(SERVICE_NAME.getPropertyName(), serviceDoc != null ? serviceDoc.getString(SERVICE_NAME.getPropertyName()) : null)
                    .put(AMOUNT_SERVICE.getPropertyName(), serviceDoc != null ? serviceDoc.getDouble(AMOUNT_SERVICE.getPropertyName()) : null)
                    .put(SUB_START_DATE.getPropertyName(), subStartDateMillis)
                    .put(SUB_END_DATE.getPropertyName(), subEndDateMillis)
                    .put(AMOUNT_PAYABLE.getPropertyName(), currentTransactionCost);

            JsonArray addonDetailsArray = new JsonArray();
            for (Document addon : addonList) {
                JsonObject addonDetails = new JsonObject()
                        .put("addonOid_Gym_ObjectId", addon.get(ADDON_OID.getPropertyName()).toString())
                        .put(ADDON_NAME.getPropertyName(), addon.getString(ADDON_NAME.getPropertyName()))
                        .put(AMOUNT_ADDON.getPropertyName(), addon.getDouble(AMOUNT_ADDON.getPropertyName()))
                        .put(ADDON_START_DATE.getPropertyName(), addon.getLong(ADDON_START_DATE.getPropertyName()))
                        .put(ADDON_END_DATE.getPropertyName(), addon.getLong(ADDON_END_DATE.getPropertyName()));
                addonDetailsArray.add(addonDetails);
            }

            if (!addonDetailsArray.isEmpty()) {
                memberDetails.put(SUBSCRIPTION_ADDON_LIST.getPropertyName(), addonDetailsArray);
            }

        /*
        if (serviceIdStr == null && !addOns.isEmpty()) {
            if (!allSubscriptionsEnded) {
                mongoDatabase.getCollection(GYM_ORDER_DETAILS_COLLECTION)
                        .insertOne(clientSession, Document.parse(memberDetails.toString()));
            } else {
                LOGGER.warn("Cannot add add-ons without an active service for emailId: {}", emailId);
                abortTransaction(clientSession);
                LOGGER.info("Transaction aborted due to no active service for emailId: {}", emailId);
                return Future.failedFuture(String.valueOf(new Document("error", "Cannot add add-ons without an active service")));
            }
        } else {
            mongoDatabase.getCollection(GYM_ORDER_DETAILS_COLLECTION)
                    .insertOne(clientSession, Document.parse(memberDetails.toString()));
        }
        */

            commitTransaction(clientSession);
            LOGGER.info("Subscription updated successfully for user with emailID: {}", emailId);
            return Future.succeededFuture(Document.parse(memberDetails.toString()));

        } catch (MongoException e) {
            LOGGER.error("MongoDB error while processing new subscription for emailID {}: {}", emailId, e.getMessage(), e);
            abortTransaction(clientSession);
            LOGGER.info("Transaction aborted due to MongoDB error for emailID: {}", emailId);
            return Future.failedFuture(String.valueOf(new Document("error", "Failed to process subscription due to MongoDB error: " + e.getMessage())));
        } catch (Exception e) {
            LOGGER.error("Failed to process new subscription for emailID {}: {}", emailId, e.getMessage(), e);
            abortTransaction(clientSession);
            LOGGER.info("Transaction aborted due to general error for emailID: {}", emailId);
            return Future.failedFuture(String.valueOf(new Document("error", "Failed to process subscription: " + e.getMessage())));
        }
    }

    private Future<Document> addNewServiceForExistingUser(String emailId, JsonObject subscription_service_addonRequest, ClientSession clientSession, Document userDoc, int activeSubIndex) {
        try {
            LOGGER.info("Processing subscription for existing user with emailID: {}", emailId);

            List<Document> existingSubscriptions = userDoc.getList(SUBSCRIPTIONS_LIST.getPropertyName(), Document.class, new ArrayList<>());
            boolean allSubscriptionsEnded = true;
            for (Document sub : existingSubscriptions) {
                Long subEndDate = sub.getLong(SUB_END_DATE.getPropertyName());
                if (subEndDate != null && subEndDate >= System.currentTimeMillis()) {
                    allSubscriptionsEnded = false;
                    break;
                }
            }
            LOGGER.debug("All subscriptions ended for emailID {}: {}", emailId, allSubscriptionsEnded);

            // Modified to use new key names
            String serviceIdStr = subscription_service_addonRequest.getString("serviceOid_Gym_ObjectId");
            JsonArray addOns = subscription_service_addonRequest.getJsonArray(SUBSCRIPTION_ADDON_LIST.getPropertyName(), new JsonArray());
            String preferredStartDateStr = subscription_service_addonRequest.getString("preferredStartDate_Gym_Date");
            Long currentDateMillis = System.currentTimeMillis();

            if (serviceIdStr == null && addOns.isEmpty()) {
                LOGGER.warn("No valid service or add-ons provided in the request for emailId: {}", emailId);
                abortTransaction(clientSession);
                LOGGER.info("Transaction aborted due to empty subscription data for emailId: {}", emailId);
                return Future.failedFuture(String.valueOf(new Document("error", "Request must contain a service or add-ons")));
            }

            double currentTransactionCost = 0.0;
            List<Document> updatedSubscriptionList = new ArrayList<>(existingSubscriptions);

            if (serviceIdStr == null && !addOns.isEmpty() && activeSubIndex != -1) {
                if (allSubscriptionsEnded) {
                    LOGGER.warn("Cannot add add-ons without an active service for emailId: {}", emailId);
                    abortTransaction(clientSession);
                    LOGGER.info("Transaction aborted due to no active service for emailId: {}", emailId);
                    return Future.failedFuture(String.valueOf(new Document("error", "Cannot add add-ons without an active service")));
                }

                Document activeSub = updatedSubscriptionList.get(activeSubIndex);
                Document serviceDoc = activeSub.get(SUBSCRIPTION_SERVICE_LIST.getPropertyName(), Document.class);
                double activeServiceCost = serviceDoc != null ? serviceDoc.getDouble(AMOUNT_SERVICE.getPropertyName()) : 0.0;
                if (activeServiceCost == 0.0) {
                    LOGGER.warn("Active service cost is missing or zero for emailId: {}", emailId);
                    abortTransaction(clientSession);
                    return Future.failedFuture(String.valueOf(new Document("error", "Active service cost is invalid")));
                }

                Long subStartDateMillis = activeSub.getLong(SUB_START_DATE.getPropertyName());
                Long subEndDateMillis = activeSub.getLong(SUB_END_DATE.getPropertyName());

                if (subStartDateMillis > currentDateMillis) {
                    LOGGER.warn("Cannot add add-ons to a future subscription without an active subscription for emailId: {}", emailId);
                    abortTransaction(clientSession);
                    return Future.failedFuture(String.valueOf(new Document("error", "Cannot add add-ons to a future subscription without an active subscription")));
                }
        /// for addonObjectID Array
//                List<Document> addonList = new ArrayList<>();
//                LOGGER.debug("Input addOns array: {}", addOns.encodePrettily()); // Debug input array
//                if (addOns == null || addOns.isEmpty()) {
//                    LOGGER.warn("No add-ons provided in the request for emailID: {}", emailId);
//                } else {
//                    for (int i = 0; i < addOns.size(); i++) {
//                        LOGGER.debug("Processing add-on index: {}", i); // Debug loop entry
//                        JsonObject addOn = addOns.getJsonObject(i);
//                        if (addOn == null) {
//                            LOGGER.warn("Null add-on object at index {} for emailID: {}", i, emailId);
//                            continue; // Skip null entries
//                        }
//                        String addonIdStr = addOn.getString(ADDON_OID.getPropertyName());
//                        LOGGER.debug("Add-on ID: {}", addonIdStr); // Debug add-on ID
//                        if (addonIdStr == null || !ObjectId.isValid(addonIdStr)) {
//                            LOGGER.warn("Invalid or missing addon ID in subscription request: {}", addOn);
//                            abortTransaction(clientSession);
//                            LOGGER.info("Transaction aborted due to invalid addon ID for emailID: {}", emailId);
//                            return Future.failedFuture(String.valueOf(new Document("error", "Valid addon ID is required in the request")));
//                        }
//                        ObjectId addonId = new ObjectId(addonIdStr);
//                        System.out.println("addon" + addonId);
//
//                        // Check for existing active addon with same ID
//                        for (Document existingSub : existingSubscriptions) {
//                            List<Document> existingAddons = existingSub.getList(SUBSCRIPTION_ADDON_LIST.getPropertyName(), Document.class, new ArrayList<>());
//                            for (Document existingAddon : existingAddons) {
//                                ObjectId existingAddonId = (ObjectId) existingAddon.get(ADDON_OID.getPropertyName());
//                                Long addonStartDate = existingAddon.getLong(ADDON_START_DATE.getPropertyName());
//                                Long addonEndDate = existingAddon.getLong(ADDON_END_DATE.getPropertyName());
//                                if (existingAddonId.equals(addonId) && addonStartDate <= currentDateMillis && addonEndDate > currentDateMillis) {
//                                    LOGGER.warn("Addon with ID {} is already active for emailID: {} until {}", addonIdStr, emailId, DateUtils.convertMillisToDateString(addonEndDate));
//                                    abortTransaction(clientSession);
//                                    return Future.failedFuture(String.valueOf(new Document("error", "Addon with ID " + addonIdStr + " is already active until " + DateUtils.convertMillisToDateString(addonEndDate))));
//                                }
//                            }
//                        }
//
//                        Document addonDoc = addOnCollection.find(clientSession, Filters.eq("_id", addonId)).first();
//                        if (addonDoc == null) {
//                            LOGGER.warn("Addon with ID {} does not exist in ADDON_COLLECTION", addonIdStr);
//                            abortTransaction(clientSession);
//                            LOGGER.info("Transaction aborted due to non-existent addon with ID: {} for emailID: {}", addonIdStr, emailId);
//                            return Future.failedFuture(String.valueOf(new Document("error", "Addon with ID " + addonIdStr + " does not exist")));
//                        }
//                        Double addonCost = addonDoc.getDouble(AMOUNT_ADDON.getPropertyName());
//                        if (addonCost == null) {
//                            LOGGER.warn("Add-on cost is null for add-on ID: {}", addonIdStr);
//                            abortTransaction(clientSession);
//                            return Future.failedFuture(String.valueOf(new Document("error", "Add-on cost is missing or invalid for add-on ID " + addonIdStr)));
//                        }
//                        currentTransactionCost += addonCost;
//
//                        Integer durationAddon = addonDoc.getInteger(DURATION_ADDON.getPropertyName());
//                        if (durationAddon == null) {
//                            LOGGER.warn("Duration is null for add-on ID: {}", addonIdStr);
//                            abortTransaction(clientSession);
//                            return Future.failedFuture(String.valueOf(new Document("error", "Add-on duration is missing for add-on ID " + addonIdStr)));
//                        }
//
//                        Long addonStartDateMillis = subStartDateMillis;
//                        Long addonEndDateMillis = addonStartDateMillis + (durationAddon * 24L * 60L * 60L * 1000L);
//                        // Cap add-on end date to service end date if it exceeds
//
//
//                        Document addonEntry = new Document()
//                                .append(ADDON_OID.getPropertyName(), addonId)
//                                .append(ADDON_NAME.getPropertyName(), addonDoc.getString(ADDON_NAME.getPropertyName()))
//                                .append(DESCRIPTION_ADDON.getPropertyName(), addonDoc.getString(DESCRIPTION_ADDON.getPropertyName()))
//                                .append(DURATION_ADDON.getPropertyName(), durationAddon)
//                                .append(MEMBER_TYPE.getPropertyName(), addonDoc.getString(MEMBER_TYPE.getPropertyName()))
//                                .append(AMOUNT_ADDON.getPropertyName(), addonCost)
//                                .append(ADDON_START_DATE.getPropertyName(), addonStartDateMillis)
//                                .append(ADDON_END_DATE.getPropertyName(), addonEndDateMillis);
//                        addonList.add(addonEntry);
//                        LOGGER.debug("Added add-on to addonList: {}", addonEntry.toJson()); // Debug added entry
//                    }
//                }
//                List<Document> existingAddons = activeSub.getList(SUBSCRIPTION_ADDON_LIST.getPropertyName(), Document.class, new ArrayList<>());
//                existingAddons.addAll(addonList);
//                activeSub.put(SUBSCRIPTION_ADDON_LIST.getPropertyName(), existingAddons);
//
//                double totalCost = activeServiceCost;
//                for (Document addon : existingAddons) {
//                    Double addonCost = addon.getDouble(AMOUNT_ADDON.getPropertyName());
//                    if (addonCost != null) {
//                        totalCost += addonCost;
//                    }
//                }
                List<Document> addonList = new ArrayList<>(); // Start with an empty list for new add-ons
                for (int i = 0; i < addOns.size(); i++) {
                    JsonObject addOn = addOns.getJsonObject(i);
                    String addonIdStr = addOn.getString(ADDON_OID.getPropertyName());
                    if (addonIdStr == null || !ObjectId.isValid(addonIdStr)) {
                        LOGGER.warn("Invalid or missing addon ID in subscription request: {}", addOn);
                        abortTransaction(clientSession);
                        LOGGER.info("Transaction aborted due to invalid addon ID for emailId: {}", emailId);
                        return Future.failedFuture("Valid addon ID is required in the request");
                    }
                    ObjectId addonId = new ObjectId(addonIdStr);

                    // Check for existing active addon with same ID across all subscriptions
                    for (Document existingSub : existingSubscriptions) {
                        List<Document> existingAddons = existingSub.getList(SUBSCRIPTION_ADDON_LIST.getPropertyName(), Document.class, new ArrayList<>());
                        for (Document existingAddon : existingAddons) {
                            ObjectId existingAddonId = (ObjectId) existingAddon.get(ADDON_OID.getPropertyName());
                            Long addonStartDate = existingAddon.getLong(ADDON_START_DATE.getPropertyName());
                            Long addonEndDate = existingAddon.getLong(ADDON_END_DATE.getPropertyName());
                            if (existingAddonId.equals(addonId) && addonStartDate <= currentDateMillis && addonEndDate > currentDateMillis) {
                                LOGGER.warn("Addon with ID {} is already active for emailId: {} until {}", addonIdStr, emailId, DateUtils.convertMillisToDateString(addonEndDate));
                                abortTransaction(clientSession);
                                return Future.failedFuture("Addon with ID " + addonIdStr + " is already active until " + DateUtils.convertMillisToDateString(addonEndDate));
                            }
                        }
                    }

                    Document addonDoc = addOnCollection.find(clientSession, Filters.eq("_id", addonId)).first();
                    if (addonDoc == null) {
                        LOGGER.warn("Addon with ID {} does not exist in ADDON_COLLECTION", addonIdStr);
                        abortTransaction(clientSession);
                        LOGGER.info("Transaction aborted due to non-existent addon with ID: {} for emailId: {}", addonIdStr, emailId);
                        return Future.failedFuture("Addon with ID " + addonIdStr + " does not exist");
                    }
                    Double addonCost = addonDoc.getDouble(AMOUNT_ADDON.getPropertyName());
                    if (addonCost == null) {
                        LOGGER.warn("Add-on cost is null for add-on ID: {}", addonIdStr);
                        abortTransaction(clientSession);
                        return Future.failedFuture("Add-on cost is missing or invalid for add-on ID " + addonIdStr);
                    }
                    currentTransactionCost += addonCost;

                    Integer durationAddon = addonDoc.getInteger(DURATION_ADDON.getPropertyName());
                    if (durationAddon == null) {
                        LOGGER.warn("Duration is null for add-on ID: {}", addonIdStr);
                        abortTransaction(clientSession);
                        return Future.failedFuture("Add-on duration is missing for add-on ID " + addonIdStr);
                    }

                    Long addonStartDateMillis = currentDateMillis;
                    Long addonEndDateMillis = addonStartDateMillis + (durationAddon * 24L * 60L * 60L * 1000L);
                    if (addonEndDateMillis > subEndDateMillis) {
                        addonEndDateMillis = subEndDateMillis;
                    }

                    Document addonEntry = new Document()
                            .append(ADDON_OID.getPropertyName(), addonId)
                            .append(ADDON_NAME.getPropertyName(), addonDoc.getString(ADDON_NAME.getPropertyName()))
                            .append(DESCRIPTION_ADDON.getPropertyName(), addonDoc.getString(DESCRIPTION_ADDON.getPropertyName()))
                            .append(DURATION_ADDON.getPropertyName(), durationAddon)
                            .append(MEMBER_TYPE.getPropertyName(), addonDoc.getString(MEMBER_TYPE.getPropertyName()))
                            .append(AMOUNT_ADDON.getPropertyName(), addonCost)
                            .append(ADDON_START_DATE.getPropertyName(), addonStartDateMillis)
                            .append(ADDON_END_DATE.getPropertyName(), addonEndDateMillis);
                    addonList.add(addonEntry);
                }

// Update the active subscription with the new add-on list, appending to existing add-ons
                List<Document> existingAddons = activeSub.getList(SUBSCRIPTION_ADDON_LIST.getPropertyName(), Document.class, new ArrayList<>());
                existingAddons.addAll(addonList);
                activeSub.put(SUBSCRIPTION_ADDON_LIST.getPropertyName(), existingAddons);

// Calculate total cost for the active subscription
                double totalCost = activeServiceCost;
                for (Document addon : existingAddons) {
                    Double addonCost = addon.getDouble(AMOUNT_ADDON.getPropertyName());
                    if (addonCost != null) {
                        totalCost += addonCost;
                    }
                }

            /*
            MongoCollection<Document> orderDetailsCollection = mongoDatabase.getCollection(GYM_ORDER_DETAILS_COLLECTION);
            Document orderDoc = orderDetailsCollection.find(clientSession, Filters.eq(EMAIL_ID.getPropertyName(), emailId.toUpperCase()))
                    .sort(new Document("_id", -1))
                    .limit(1)
                    .first();

            Document subscriptionEntry = new Document()
                    .append(AMOUNT_PAYABLE.getPropertyName(), currentTransactionCost);

            if (serviceDoc != null) {
                subscriptionEntry.append("serviceId", serviceDoc.get(SERVICE_OID.getPropertyName()).toString())
                        .append("serviceName", serviceDoc.getString(SERVICE_NAME.getPropertyName()))
                        .append("subStartDate", DateUtils.convertMillisToDateString(subStartDateMillis))
                        .append("subEndDate", DateUtils.convertMillisToDateString(subEndDateMillis))
                        .append("serviceAmount", serviceDoc.getDouble(AMOUNT_SERVICE.getPropertyName()));
            }

            List<Document> addonEntries = new ArrayList<>();
            for (Document addon : addonList) {
                addonEntries.add(new Document()
                        .append("addonId", addon.get(ADDON_OID.getPropertyName()).toString())
                        .append("addonName", addon.getString(ADDON_NAME.getPropertyName()))
                        .append("addonAmount", addon.getDouble(AMOUNT_ADDON.getPropertyName()))
                        .append("addonStartDate", DateUtils.convertMillisToDateString(addon.getLong(ADDON_START_DATE.getPropertyName())))
                        .append("addonEndDate", DateUtils.convertMillisToDateString(addon.getLong(ADDON_END_DATE.getPropertyName()))));
            }
            if (!addonEntries.isEmpty()) {
                subscriptionEntry.append("addons", addonEntries);
            }

            if (orderDoc == null) {
                LOGGER.warn("No document found in GYM_ORDER_DETAILS_COLLECTION for emailID: {}", emailId);
                abortTransaction(clientSession);
                LOGGER.info("Transaction aborted due to missing document for emailID: {}", emailId);
                return Future.failedFuture("No document found in GYM_ORDER_DETAILS_COLLECTION for emailID: " + emailId);
            }

            Document orderUpdate = new Document("$push", new Document(SUBSCRIPTIONS_LIST.getPropertyName(), subscriptionEntry));
            boolean orderDetailsUpdated = updateDocument(GYM_ORDER_DETAILS_COLLECTION,
                    Filters.eq("_id", orderDoc.getObjectId("_id")),
                    orderUpdate, clientSession, mongoDatabase);
            if (!orderDetailsUpdated) {
                LOGGER.warn("Failed to update subscription details in GYM_ORDER_DETAILS_COLLECTION for emailID: {}", emailId);
                abortTransaction(clientSession);
                LOGGER.info("Transaction aborted due to failure in updating order details for emailID: {}", emailId);
                return Future.failedFuture("Failed to update order details");
            }
            */

                Document update = new Document("$set", new Document()
                        .append(SUBSCRIPTIONS_LIST.getPropertyName(), updatedSubscriptionList)
                        .append(TOTAL_COST.getPropertyName(), totalCost));

            /*
            boolean updated = updateDocument(GYM_USERS_COLLECTION, Filters.eq(EMAIL_ID.getPropertyName(), emailId), update, clientSession, mongoDatabase);
            if (!updated) {
                LOGGER.warn("Failed to update add-on details for user with emailId: {}", emailId);
                abortTransaction(clientSession);
                LOGGER.info("Transaction aborted due to failure in updating add-ons for emailId: {}", emailId);
                return Future.failedFuture("Failed to update add-on details");
            }
            */

                Document updatedUser = userCollection.find(clientSession, Filters.eq(EMAIL_ID.getPropertyName(), emailId)).first();
                Boolean currentStatus = updatedUser.getBoolean(STATUS.getPropertyName());
                if (currentStatus == null) {
                    currentStatus = true;
                    update = new Document("$set", new Document(STATUS.getPropertyName(), true));
                    updateDocument(GYM_USERS_COLLECTION, Filters.eq(EMAIL_ID.getPropertyName(), emailId), update, clientSession, mongoDatabase);
                    LOGGER.debug("No status found for user with emailId {}, setting to true", emailId);
                }

                // Modified to use new key names in memberDetails
                JsonObject memberDetails = new JsonObject()
                        .put(USER_ID.getPropertyName(), updatedUser.getString(USER_ID.getPropertyName()))
                        .put(NAME.getPropertyName(), updatedUser.getString(NAME.getPropertyName()))
                        .put(MEMBER_TYPE.getPropertyName(), updatedUser.getString(USER_TYPE.getPropertyName()))
                        .put(CONTACT_NUMBER.getPropertyName(), updatedUser.getLong(CONTACT_NUMBER.getPropertyName()))
                        .put(EMAIL_ID.getPropertyName(), emailId)
                        .put(AMOUNT_PAYABLE.getPropertyName(), currentTransactionCost);

                JsonArray addonDetailsArray = new JsonArray();
                for (Document addon : addonList) {
                    JsonObject addonDetails = new JsonObject()
                            .put("addonOid_Gym_ObjectId", addon.get(ADDON_OID.getPropertyName()).toString())
                            .put(ADDON_NAME.getPropertyName(), addon.getString(ADDON_NAME.getPropertyName()))
                            .put(AMOUNT_ADDON.getPropertyName(), addon.getDouble(AMOUNT_ADDON.getPropertyName()))
                            .put(ADDON_START_DATE.getPropertyName(), addon.getLong(ADDON_START_DATE.getPropertyName()))
                            .put(ADDON_END_DATE.getPropertyName(), addon.getLong(ADDON_END_DATE.getPropertyName()));
                    addonDetailsArray.add(addonDetails);
                }

                if (!addonDetailsArray.isEmpty()) {
                    memberDetails.put(SUBSCRIPTION_ADDON_LIST.getPropertyName(), addonDetailsArray);
                }

            /*
            System.out.println(memberDetails);
            mongoDatabase.getCollection(GYM_ORDER_DETAILS_COLLECTION)
                    .insertOne(clientSession, Document.parse(memberDetails.toString()));
            */

                commitTransaction(clientSession);
                LOGGER.info("Subscription updated successfully for user with emailID: {}", emailId);
                return Future.succeededFuture(Document.parse(memberDetails.toString()));
            }

            Long sevenDaysInMillis = 7L * 24L * 60L * 60L * 1000L;
            Long minAllowedPurchaseDateMillis = existingSubscriptions.get(activeSubIndex).getLong(SUB_END_DATE.getPropertyName()) - sevenDaysInMillis;
            if (currentDateMillis < minAllowedPurchaseDateMillis) {
                LOGGER.warn("Cannot purchase new service for emailId {}. Current date {} is before allowed period starting {}",
                        emailId, DateUtils.convertMillisToDateString(currentDateMillis),
                        DateUtils.convertMillisToDateString(minAllowedPurchaseDateMillis));
                abortTransaction(clientSession);
                return Future.failedFuture("New service can only be purchased within 7 days before the current service end date " +
                        DateUtils.convertMillisToDateString(existingSubscriptions.get(activeSubIndex).getLong(SUB_END_DATE.getPropertyName())));
            }

            Document subscriptionDoc = new Document();
            Long subStartDateMillis = null;
            Long subEndDateMillis = null;

            if (!ObjectId.isValid(serviceIdStr)) {
                LOGGER.warn("Invalid service ID in subscription request: {}", serviceIdStr);
                abortTransaction(clientSession);
                LOGGER.info("Transaction aborted due to invalid service ID for emailId: {}", emailId);
                return Future.failedFuture(String.valueOf(new Document("error", "Valid service ID is required in the request")));
            }
            ObjectId serviceId = new ObjectId(serviceIdStr);

            Document serviceDoc = serviceCollection.find(clientSession, Filters.eq("_id", serviceId)).first();
            if (serviceDoc == null) {
                LOGGER.warn("Service with ID {} does not exist in SERVICES_COLLECTION", serviceIdStr);
                abortTransaction(clientSession);
                LOGGER.info("Transaction aborted due to non-existent service with ID: {} for emailId: {}", serviceIdStr, emailId);
                return Future.failedFuture(String.valueOf(new Document("error", "Service with ID " + serviceIdStr + " does not exist")));
            }
            Double serviceCost = serviceDoc.getDouble(AMOUNT_SERVICE.getPropertyName());
            if (serviceCost == null) {
                LOGGER.warn("Service cost is null for service ID: {}", serviceIdStr);
                abortTransaction(clientSession);
                return Future.failedFuture(String.valueOf(new Document("error", "Service cost is missing or invalid for service ID " + serviceIdStr)));
            }
            currentTransactionCost += serviceCost;

            Integer durationService = serviceDoc.getInteger(DURATION_SERVICE.getPropertyName());
            if (durationService == null) {
                LOGGER.warn("Duration is null for service ID: {}", serviceIdStr);
                abortTransaction(clientSession);
                return Future.failedFuture(String.valueOf(new Document("error", "Service duration is missing for service ID " + serviceIdStr)));
            }

            Long minAllowedStartDateMillis = existingSubscriptions.get(activeSubIndex).getLong(SUB_END_DATE.getPropertyName()) + (24L * 60L * 60L * 1000L);
            Long maxAllowedStartDateMillis = currentDateMillis + (6L * 60L * 60L * 1000L);
            subStartDateMillis = preferredStartDateStr != null ? DateUtils.getStartOfDayMillis(preferredStartDateStr) : minAllowedStartDateMillis;

            if (subStartDateMillis == null && preferredStartDateStr != null) {
                LOGGER.warn("Invalid preferred start date format: {}", preferredStartDateStr);
                abortTransaction(clientSession);
                return Future.failedFuture(String.valueOf(new Document("error", "Preferred start date must be in dd-MM-yyyy, yyyy-MM-dd, yyyy/MM/dd, or dd/MM/yyyy format")));
            }

            if (subStartDateMillis != null && subStartDateMillis < currentDateMillis) {
                LOGGER.warn("Preferred start date {} is in the past for emailID: {}", preferredStartDateStr, emailId);
                abortTransaction(clientSession);
                return Future.failedFuture(String.valueOf(new Document("error", "Preferred start date cannot be in the past")));
            }

            if (subStartDateMillis < minAllowedStartDateMillis || subStartDateMillis > maxAllowedStartDateMillis) {
                LOGGER.warn("Preferred start date {} out of allowed range [{} to {}] for emailId: {}",
                        preferredStartDateStr != null ? preferredStartDateStr : "null",
                        DateUtils.convertMillisToDateString(minAllowedStartDateMillis),
                        DateUtils.convertMillisToDateString(maxAllowedStartDateMillis), emailId);
                abortTransaction(clientSession);
                return Future.failedFuture(String.valueOf(new Document("error", "Preferred start date must be on or after the day following the current service end date (" +
                        DateUtils.convertMillisToDateString(minAllowedStartDateMillis) +
                        ") and within 7 days from today")));
            }

            subEndDateMillis = subStartDateMillis + ((durationService - 1) * 24L * 60L * 60L * 1000L);

            subscriptionDoc.append(SUBSCRIPTION_SERVICE_LIST.getPropertyName(), new Document()
                            .append(SERVICE_OID.getPropertyName(), serviceId)
                            .append(SERVICE_NAME.getPropertyName(), serviceDoc.getString(SERVICE_NAME.getPropertyName()))
                            .append(DURATION_SERVICE.getPropertyName(), durationService)
                            .append(MEMBER_TYPE.getPropertyName(), serviceDoc.getString(MEMBER_TYPE.getPropertyName()))
                            .append(AMOUNT_SERVICE.getPropertyName(), serviceCost))
                    .append(SUB_START_DATE.getPropertyName(), subStartDateMillis)
                    .append(SUB_END_DATE.getPropertyName(), subEndDateMillis);
///  addon for objectID Array
//            List<Document> addonList = new ArrayList<>();
//            Document activeSub = existingSubscriptions.get(activeSubIndex);
//            List<Document> activeAddons = activeSub.getList(SUBSCRIPTION_ADDON_LIST.getPropertyName(), Document.class, new ArrayList<>());
//
//            // Modified to handle array of ObjectId strings
//            for (int i = 0; i < addOns.size(); i++) {
//                String addonIdStr = addOns.getString(i);
//                if (addonIdStr == null || !ObjectId.isValid(addonIdStr)) {
//                    LOGGER.warn("Invalid or missing addon ID in subscription request: {}", addonIdStr);
//                    abortTransaction(clientSession);
//                    LOGGER.info("Transaction aborted due to invalid addon ID for emailId: {}", emailId);
//                    return Future.failedFuture(String.valueOf(new Document("error", "Valid addon ID is required in the request")));
//                }
//                ObjectId addonId = new ObjectId(addonIdStr);
//
//                Long activeSubEndDate = existingSubscriptions.get(activeSubIndex).getLong(SUB_END_DATE.getPropertyName());
//                if (currentDateMillis >= (activeSubEndDate - sevenDaysInMillis) && currentDateMillis <= activeSubEndDate) {
//                    boolean isExistingAddon = false;
//                    for (Document existingAddon : activeAddons) {
//                        ObjectId existingAddonId = (ObjectId) existingAddon.get(ADDON_OID.getPropertyName());
//                        Long addonEndDate = existingAddon.getLong(ADDON_END_DATE.getPropertyName());
//                        if (existingAddonId.equals(addonId) && addonEndDate > currentDateMillis) {
//                            isExistingAddon = true;
//                            Long addonStartDateMillis = subStartDateMillis;
//                            Long addonEndDateMillis = addonStartDateMillis + (existingAddon.getInteger(DURATION_ADDON.getPropertyName()) * 24L * 60L * 60L * 1000L);
//                            if (addonEndDateMillis > subEndDateMillis) {
//                                addonEndDateMillis = subEndDateMillis;
//                            }
//                            Document addonEntry = new Document()
//                                    .append(ADDON_OID.getPropertyName(), addonId)
//                                    .append(ADDON_NAME.getPropertyName(), existingAddon.getString(ADDON_NAME.getPropertyName()))
//                                    .append(DESCRIPTION_ADDON.getPropertyName(), existingAddon.getString(DESCRIPTION_ADDON.getPropertyName()))
//                                    .append(DURATION_ADDON.getPropertyName(), existingAddon.getInteger(DURATION_ADDON.getPropertyName()))
//                                    .append(MEMBER_TYPE.getPropertyName(), existingAddon.getString(MEMBER_TYPE.getPropertyName()))
//                                    .append(AMOUNT_ADDON.getPropertyName(), existingAddon.getDouble(AMOUNT_ADDON.getPropertyName()))
//                                    .append(ADDON_START_DATE.getPropertyName(), addonStartDateMillis)
//                                    .append(ADDON_END_DATE.getPropertyName(), addonEndDateMillis);
//                            addonList.add(addonEntry);
//                            currentTransactionCost += existingAddon.getDouble(AMOUNT_ADDON.getPropertyName());
//                            LOGGER.info("Reusing existing active addon with ID {} for emailId: {}", addonIdStr, emailId);
//                            break;
//                        }
//                    }
//                    if (isExistingAddon) {
//                        continue;
//                    }
//                }
//
//                for (Document existingSub : existingSubscriptions) {
//                    List<Document> existingAddonsCheck = existingSub.getList(SUBSCRIPTION_ADDON_LIST.getPropertyName(), Document.class, new ArrayList<>());
//                    for (Document existingAddon : existingAddonsCheck) {
//                        ObjectId existingAddonId = (ObjectId) existingAddon.get(ADDON_OID.getPropertyName());
//                        Long addonStartDate = existingAddon.getLong(ADDON_START_DATE.getPropertyName());
//                        Long addonEndDate = existingAddon.getLong(ADDON_END_DATE.getPropertyName());
//                        if (existingAddonId.equals(addonId) && addonStartDate <= currentDateMillis && addonEndDate > currentDateMillis) {
//                            LOGGER.warn("Addon with ID {} is already active for emailId: {} until {}", addonIdStr, emailId, DateUtils.convertMillisToDateString(addonEndDate));
//                            abortTransaction(clientSession);
//                            return Future.failedFuture(String.valueOf(new Document("error", "Addon with ID " + addonIdStr + " is already active until " + DateUtils.convertMillisToDateString(addonEndDate))));
//                        }
//                    }
//                }
//                System.out.println(addOns);
//                Document addonDoc = addOnCollection.find(clientSession, Filters.eq("_id", addonId)).first();
//                if (addonDoc == null) {
//                    LOGGER.warn("Addon with ID {} does not exist in ADDON_COLLECTION", addonIdStr);
//                    abortTransaction(clientSession);
//                    LOGGER.info("Transaction aborted due to non-existent addon with ID: {} for emailId: {}", addonIdStr, emailId);
//                    return Future.failedFuture(String.valueOf(new Document("error", "Addon with ID " + addonIdStr + " does not exist")));
//                }
//                Double addonCost = addonDoc.getDouble(AMOUNT_ADDON.getPropertyName());
//                if (addonCost == null) {
//                    LOGGER.warn("Add-on cost is null for add-on ID: {}", addonIdStr);
//                    abortTransaction(clientSession);
//                    return Future.failedFuture(String.valueOf(new Document("error", "Add-on cost is missing or invalid for add-on ID " + addonIdStr)));
//                }
//                currentTransactionCost += addonCost;
//
//                Integer durationAddon = addonDoc.getInteger(DURATION_ADDON.getPropertyName());
//                if (durationAddon == null) {
//                    LOGGER.warn("Duration is null for add-on ID: {}", addonIdStr);
//                    abortTransaction(clientSession);
//                    return Future.failedFuture(String.valueOf(new Document("error", "Add-on duration is missing for add-on ID " + addonIdStr)));
//                }
//
//                Long addonStartDateMillis = subStartDateMillis;
//                Long addonEndDateMillis = addonStartDateMillis + (durationAddon * 24L * 60L * 60L * 1000L);
//                if (addonEndDateMillis > subEndDateMillis) {
//                    addonEndDateMillis = subEndDateMillis;
//                }
//
//                Document addonEntry = new Document()
//                        .append(ADDON_OID.getPropertyName(), addonId)
//                        .append(ADDON_NAME.getPropertyName(), addonDoc.getString(ADDON_NAME.getPropertyName()))
//                        .append(DESCRIPTION_ADDON.getPropertyName(), addonDoc.getString(DESCRIPTION_ADDON.getPropertyName()))
//                        .append(DURATION_ADDON.getPropertyName(), durationAddon)
//                        .append(MEMBER_TYPE.getPropertyName(), addonDoc.getString(MEMBER_TYPE.getPropertyName()))
//                        .append(AMOUNT_ADDON.getPropertyName(), addonCost)
//                        .append(ADDON_START_DATE.getPropertyName(), addonStartDateMillis)
//                        .append(ADDON_END_DATE.getPropertyName(), addonEndDateMillis);
//                addonList.add(addonEntry);
//            }
//
//            if (!addonList.isEmpty()) {
//                subscriptionDoc.append(SUBSCRIPTION_ADDON_LIST.getPropertyName(), addonList);
//            }
            List<Document> addonList = new ArrayList<>();
            Document activeSub = existingSubscriptions.get(activeSubIndex);
            List<Document> activeAddons = activeSub.getList(SUBSCRIPTION_ADDON_LIST.getPropertyName(), Document.class, new ArrayList<>());

            for (int i = 0; i < addOns.size(); i++)
            {
                JsonObject addOn = addOns.getJsonObject(i);
                String addonIdStr = addOn.getString(ADDON_OID.getPropertyName());
                if (addonIdStr == null || !ObjectId.isValid(addonIdStr)) {
                    LOGGER.warn("Invalid or missing addon ID in subscription request: {}", addOn);
                    abortTransaction(clientSession);
                    LOGGER.info("Transaction aborted due to invalid addon ID for emailId: {}", emailId);
                    return Future.failedFuture("Valid addon ID is required in the request");
                }
                ObjectId addonId = new ObjectId(addonIdStr);

                // Check if within 7 days before active subscription end date
                Long activeSubEndDate = existingSubscriptions.get(activeSubIndex).getLong(SUB_END_DATE.getPropertyName());
                if (currentDateMillis >= (activeSubEndDate - sevenDaysInMillis) && currentDateMillis <= activeSubEndDate) {
                    // Allow existing active add-ons to be reused
                    boolean isExistingAddon = false;
                    for (Document existingAddon : activeAddons) {
                        ObjectId existingAddonId = (ObjectId) existingAddon.get(ADDON_OID.getPropertyName());
                        Long addonEndDate = existingAddon.getLong(ADDON_END_DATE.getPropertyName());
                        if (existingAddonId.equals(addonId) && addonEndDate > currentDateMillis) {
                            isExistingAddon = true;
                            // Reuse the existing add-on details but adjust dates
                            Long addonStartDateMillis = subStartDateMillis;
                            Long addonEndDateMillis = addonStartDateMillis + (existingAddon.getInteger(DURATION_ADDON.getPropertyName()) * 24L * 60L * 60L * 1000L);
                            if (addonEndDateMillis > subEndDateMillis) {
                                addonEndDateMillis = subEndDateMillis;
                            }
                            Document addonEntry = new Document()
                                    .append(ADDON_OID.getPropertyName(), addonId)
                                    .append(ADDON_NAME.getPropertyName(), existingAddon.getString(ADDON_NAME.getPropertyName()))
                                    .append(DESCRIPTION_ADDON.getPropertyName(), existingAddon.getString(DESCRIPTION_ADDON.getPropertyName()))
                                    .append(DURATION_ADDON.getPropertyName(), existingAddon.getInteger(DURATION_ADDON.getPropertyName()))
                                    .append(MEMBER_TYPE.getPropertyName(), existingAddon.getString(MEMBER_TYPE.getPropertyName()))
                                    .append(AMOUNT_ADDON.getPropertyName(), existingAddon.getDouble(AMOUNT_ADDON.getPropertyName()))
                                    .append(ADDON_START_DATE.getPropertyName(), addonStartDateMillis)
                                    .append(ADDON_END_DATE.getPropertyName(), addonEndDateMillis);
                            addonList.add(addonEntry);
                            currentTransactionCost += existingAddon.getDouble(AMOUNT_ADDON.getPropertyName());
                            LOGGER.info("Reusing existing active addon with ID {} for emailId: {}", addonIdStr, emailId);
                            break;
                        }
                    }
                    if (isExistingAddon) {
                        continue; // Skip to next add-on if this one was reused
                    }
                }

                // Check for existing active addon with same ID across all subscriptions
                for (Document existingSub : existingSubscriptions) {
                    List<Document> existingAddonsCheck = existingSub.getList(SUBSCRIPTION_ADDON_LIST.getPropertyName(), Document.class, new ArrayList<>());
                    for (Document existingAddon : existingAddonsCheck) {
                        ObjectId existingAddonId = (ObjectId) existingAddon.get(ADDON_OID.getPropertyName());
                        Long addonStartDate = existingAddon.getLong(ADDON_START_DATE.getPropertyName());
                        Long addonEndDate = existingAddon.getLong(ADDON_END_DATE.getPropertyName());
                        if (existingAddonId.equals(addonId) && addonStartDate <= currentDateMillis && addonEndDate > currentDateMillis) {
                            LOGGER.warn("Addon with ID {} is already active for emailId: {} until {}", addonIdStr, emailId, DateUtils.convertMillisToDateString(addonEndDate));
                            abortTransaction(clientSession);
                            return Future.failedFuture("Addon with ID " + addonIdStr + " is already active until " + DateUtils.convertMillisToDateString(addonEndDate));
                        }
                    }
                }

                Document addonDoc = addOnCollection.find(clientSession, Filters.eq("_id", addonId)).first();
                if (addonDoc == null) {
                    LOGGER.warn("Addon with ID {} does not exist in ADDON_COLLECTION", addonIdStr);
                    abortTransaction(clientSession);
                    LOGGER.info("Transaction aborted due to non-existent addon with ID: {} for emailId: {}", addonIdStr, emailId);
                    return Future.failedFuture("Addon with ID " + addonIdStr + " does not exist");
                }
                Double addonCost = addonDoc.getDouble(AMOUNT_ADDON.getPropertyName());
                if (addonCost == null) {
                    LOGGER.warn("Add-on cost is null for add-on ID: {}", addonIdStr);
                    abortTransaction(clientSession);
                    return Future.failedFuture("Add-on cost is missing or invalid for add-on ID " + addonIdStr);
                }
                currentTransactionCost += addonCost;

                Integer durationAddon = addonDoc.getInteger(DURATION_ADDON.getPropertyName());
                if (durationAddon == null) {
                    LOGGER.warn("Duration is null for add-on ID: {}", addonIdStr);
                    abortTransaction(clientSession);
                    return Future.failedFuture("Add-on duration is missing for add-on ID " + addonIdStr);
                }

                Long addonStartDateMillis = subStartDateMillis;
                Long addonEndDateMillis = addonStartDateMillis + (durationAddon * 24L * 60L * 60L * 1000L);
                // Cap add-on end date to service end date if it exceeds
                if (addonEndDateMillis > subEndDateMillis) {
                    addonEndDateMillis = subEndDateMillis;
                }

                Document addonEntry = new Document()
                        .append(ADDON_OID.getPropertyName(), addonId)
                        .append(ADDON_NAME.getPropertyName(), addonDoc.getString(ADDON_NAME.getPropertyName()))
                        .append(DESCRIPTION_ADDON.getPropertyName(), addonDoc.getString(DESCRIPTION_ADDON.getPropertyName()))
                        .append(DURATION_ADDON.getPropertyName(), durationAddon)
                        .append(MEMBER_TYPE.getPropertyName(), addonDoc.getString(MEMBER_TYPE.getPropertyName()))
                        .append(AMOUNT_ADDON.getPropertyName(), addonCost)
                        .append(ADDON_START_DATE.getPropertyName(), addonStartDateMillis)
                        .append(ADDON_END_DATE.getPropertyName(), addonEndDateMillis);
                addonList.add(addonEntry);
            }

            if (!addonList.isEmpty()) {
                subscriptionDoc.append(SUBSCRIPTION_ADDON_LIST.getPropertyName(), addonList);
            }
            double totalCost = currentTransactionCost;
            Document update = new Document();

            updatedSubscriptionList.add(subscriptionDoc);
            update.append("$set", new Document()
                    .append(SUBSCRIPTIONS_LIST.getPropertyName(), updatedSubscriptionList)
                    .append(TOTAL_COST.getPropertyName(), totalCost));

        /*
        MongoCollection<Document> orderDetailsCollection = mongoDatabase.getCollection(GYM_ORDER_DETAILS_COLLECTION);
        Document orderDoc = orderDetailsCollection.find(clientSession, Filters.eq(EMAIL_ID.getPropertyName(), emailId.toUpperCase()))
                .sort(new Document("_id", -1))
                .limit(1)
                .first();

        Document subscriptionEntry = new Document()
                .append(AMOUNT_PAYABLE.getPropertyName(), currentTransactionCost);

        if (serviceIdStr != null && serviceDoc != null) {
            subscriptionEntry.append("serviceId", serviceId.toString())
                    .append("serviceName", serviceDoc.getString(SERVICE_NAME.getPropertyName()))
                    .append("subStartDate", DateUtils.convertMillisToDateString(subStartDateMillis))
                    .append("subEndDate", DateUtils.convertMillisToDateString(subEndDateMillis))
                    .append("serviceAmount", serviceDoc.getDouble(AMOUNT_SERVICE.getPropertyName()));
        }

        List<Document> addonEntries = new ArrayList<>();
        for (Document addon : addonList) {
            addonEntries.add(new Document()
                    .append("addonId", addon.get(ADDON_OID.getPropertyName()).toString())
                    .append("addonName", addon.getString(ADDON_NAME.getPropertyName()))
                    .append("addonAmount", addon.getDouble(AMOUNT_ADDON.getPropertyName()))
                    .append("addonStartDate", DateUtils.convertMillisToDateString(addon.getLong(ADDON_START_DATE.getPropertyName())))
                    .append("addonEndDate", DateUtils.convertMillisToDateString(addon.getLong(ADDON_END_DATE.getPropertyName()))));
        }
        if (!addonEntries.isEmpty()) {
            subscriptionEntry.append("addons", addonEntries);
        }

        if (orderDoc == null) {
            LOGGER.warn("No document found in GYM_ORDER_DETAILS_COLLECTION for emailID: {}", emailId);
            abortTransaction(clientSession);
            LOGGER.info("Transaction aborted due to missing document for emailID: {}", emailId);
            return Future.failedFuture("No document found in GYM_ORDER_DETAILS_COLLECTION for emailID: " + emailId);
        }

        Document orderUpdate = new Document("$push", new Document(SUBSCRIPTIONS_LIST.getPropertyName(), subscriptionEntry));
        boolean orderDetailsUpdated = updateDocument(GYM_ORDER_DETAILS_COLLECTION,
                Filters.eq("_id", orderDoc.getObjectId("_id")),
                orderUpdate, clientSession, mongoDatabase);
        if (!orderDetailsUpdated) {
            LOGGER.warn("Failed to update subscription details in GYM_ORDER_DETAILS_COLLECTION for emailID: {}", emailId);
            abortTransaction(clientSession);
            LOGGER.info("Transaction aborted due to failure in updating order details for emailID: {}", emailId);
            return Future.failedFuture("Failed to update order details");
        }
        */

        /*
        boolean updated = updateDocument(GYM_USERS_COLLECTION, Filters.eq(EMAIL_ID.getPropertyName(), emailId), update, clientSession, mongoDatabase);
        if (!updated) {
            LOGGER.warn("Failed to update subscription details for user with emailId: {}", emailId);
            abortTransaction(clientSession);
            LOGGER.info("Transaction aborted due to failure in updating subscription for emailId: {}", emailId);
            return Future.failedFuture("Failed to update subscription details");
        }
        */

            Document updatedUser = userCollection.find(clientSession, Filters.eq(EMAIL_ID.getPropertyName(), emailId)).first();
            Boolean currentStatus = updatedUser.getBoolean(STATUS.getPropertyName());
            if (currentStatus == null) {
                currentStatus = true;
                update = new Document("$set", new Document(STATUS.getPropertyName(), true));
                updateDocument(GYM_USERS_COLLECTION, Filters.eq(EMAIL_ID.getPropertyName(), emailId), update, clientSession, mongoDatabase);
                LOGGER.debug("No status found for user with emailId {}, setting to true", emailId);
            }

            // Modified to use new key names in memberDetails
            JsonObject memberDetails = new JsonObject()
                    .put(USER_ID.getPropertyName(), updatedUser.getString(USER_ID.getPropertyName()))
                    .put(NAME.getPropertyName(), updatedUser.getString(NAME.getPropertyName()))
                    .put(MEMBER_TYPE.getPropertyName(), updatedUser.getString(USER_TYPE.getPropertyName()))
                    .put(CONTACT_NUMBER.getPropertyName(), updatedUser.getLong(CONTACT_NUMBER.getPropertyName()))
                    .put(EMAIL_ID.getPropertyName(), emailId)
                    .put("serviceOid_Gym_ObjectId", serviceIdStr)
                    .put(SERVICE_NAME.getPropertyName(), serviceDoc != null ? serviceDoc.getString(SERVICE_NAME.getPropertyName()) : null)
                    .put(AMOUNT_SERVICE.getPropertyName(), serviceDoc != null ? serviceDoc.getDouble(AMOUNT_SERVICE.getPropertyName()) : null)
                    .put(SUB_START_DATE.getPropertyName(), subStartDateMillis)
                    .put(SUB_END_DATE.getPropertyName(), subEndDateMillis)
                    .put(AMOUNT_PAYABLE.getPropertyName(), currentTransactionCost);

            JsonArray addonDetailsArray = new JsonArray();
            for (Document addon : addonList) {
                JsonObject addonDetails = new JsonObject()
                        .put("addonOid_Gym_ObjectId", addon.get(ADDON_OID.getPropertyName()).toString())
                        .put(ADDON_NAME.getPropertyName(), addon.getString(ADDON_NAME.getPropertyName()))
                        .put(AMOUNT_ADDON.getPropertyName(), addon.getDouble(AMOUNT_ADDON.getPropertyName()))
                        .put(ADDON_START_DATE.getPropertyName(), addon.getLong(ADDON_START_DATE.getPropertyName()))
                        .put(ADDON_END_DATE.getPropertyName(), addon.getLong(ADDON_END_DATE.getPropertyName()));
                addonDetailsArray.add(addonDetails);
            }

            if (!addonDetailsArray.isEmpty()) {
                memberDetails.put(SUBSCRIPTION_ADDON_LIST.getPropertyName(), addonDetailsArray);
            }

        /*
        mongoDatabase.getCollection(GYM_ORDER_DETAILS_COLLECTION)
                .insertOne(clientSession, Document.parse(memberDetails.toString()));
        */

            commitTransaction(clientSession);
            LOGGER.info("Subscription updated successfully for user with emailID: {}", emailId);
            return Future.succeededFuture(Document.parse(memberDetails.toString()));
        } catch (MongoException e) {
            LOGGER.error("MongoDB error while processing subscription for emailId {}: {}", emailId, e.getMessage(), e);
            abortTransaction(clientSession);
            LOGGER.info("Transaction aborted due to MongoDB error for emailId: {}", emailId);
            return Future.failedFuture(String.valueOf(new Document("error", "Failed to process subscription due to MongoDB error: " + e.getMessage())));
        } catch (Exception e) {
            LOGGER.error("Failed to process subscription for emailId {}: {}", emailId, e.getMessage(), e);
            abortTransaction(clientSession);
            LOGGER.info("Transaction aborted due to general error for emailId: {}", emailId);
            return Future.failedFuture(String.valueOf(new Document("error", "Failed to process subscription: " + e.getMessage())));
        }
    }

    public Future<Void> updateUserStatus(String emailId, String statusText) {
        ClientSession clientSession = getMongoDbSession(this.mongoClient);
        try {
            startTransaction(clientSession);
            if (statusText == null || statusText.trim().isEmpty()) {
                abortTransaction(clientSession);
                return Future.failedFuture("Status text cannot be null or empty");
            }

            if ("Inactive".equalsIgnoreCase(statusText.trim())) {
                Document update = new Document("$set", new Document(STATUS.getPropertyName(), false));
                boolean updated = updateDocument(GYM_USERS_COLLECTION, Filters.eq(EMAIL_ID.getPropertyName(), emailId.toUpperCase()), update, clientSession, mongoDatabase);
                if (!updated) {
                    abortTransaction(clientSession);
                    return Future.failedFuture("Failed to update status for user with emailID: " + emailId);
                }
                LOGGER.info("User status updated to 'Inactive' for emailID: {}", emailId);
            } else if ("Active".equalsIgnoreCase(statusText.trim())) {
                Document update = new Document("$set", new Document(STATUS.getPropertyName(), true));
                boolean updated = updateDocument(GYM_USERS_COLLECTION, Filters.eq(EMAIL_ID.getPropertyName(), emailId.toUpperCase()), update, clientSession, mongoDatabase);
                if (!updated) {
                    abortTransaction(clientSession);
                    return Future.failedFuture("Failed to update status for user with emailID: " + emailId);
                }
                LOGGER.info("User status updated to 'Active' for emailID: {}", emailId);
            } else {
                LOGGER.info("Status text '{}' not 'Active' or 'Inactive', no status change for emailID: {}", statusText, emailId);
            }

            commitTransaction(clientSession);
            return Future.succeededFuture();
        } catch (MongoException e) {
            abortTransaction(clientSession);
            LOGGER.error("MongoDB error updating status for emailID {}: {}", emailId, e.getMessage(), e);
            return Future.failedFuture("MongoDB error: " + e.getMessage());
        } finally {
            if (clientSession != null) {
                clientSession.close();
            }
        }
    }


    public boolean getUserByEmailId(String email) {
        Document query = new Document(EMAIL_ID.getPropertyName(), email);
        Document user = userCollection.find(query).first();
        return user != null;
    }

    public Future<JsonObject> getCurrentSubscriptionDetails(String emailId) {
        ClientSession clientSession = getMongoDbSession(this.mongoClient);
        try {
            startTransaction(clientSession);
            LOGGER.info("Starting transaction to retrieve subscription details for emailID: {}", emailId);

            if (!getUserByEmailId(emailId.toUpperCase())) {
                LOGGER.warn("User with emailID {} does not exist", emailId);
                abortTransaction(clientSession);
                return Future.failedFuture("User with emailID " + emailId + " does not exist");
            }

            Document userDoc = userCollection.find(clientSession, Filters.eq(EMAIL_ID.getPropertyName(), emailId)).first();
            List<Document> subscriptions = userDoc.getList(SUBSCRIPTIONS_LIST.getPropertyName(), Document.class);
            Long currentDateMillis = System.currentTimeMillis();
            JsonArray subscriptionDetailsArray = new JsonArray();

            // Process all subscriptions (active and future)
            if (subscriptions != null && !subscriptions.isEmpty()) {
                for (Document sub : subscriptions) {
                    Long startDate = sub.getLong(SUB_START_DATE.getPropertyName());
                    Long endDate = sub.getLong(SUB_END_DATE.getPropertyName());
                    if (startDate != null && endDate != null && sub.containsKey(SUBSCRIPTION_SERVICE_LIST.getPropertyName())) {
                        // Include active (startDate <= now && endDate > now) or future (startDate > now) subscriptions
                        if ((startDate <= currentDateMillis && endDate > currentDateMillis) || startDate > currentDateMillis) {
                            JsonObject subDetails = buildSubscriptionDetails(sub, currentDateMillis);
                            subscriptionDetailsArray.add(subDetails);
                        }
                    }
                }
            }

            // Fail if no valid subscriptions are found
            if (subscriptionDetailsArray.isEmpty()) {
                LOGGER.warn("No active or future subscriptions found for emailID: {}", emailId);
                abortTransaction(clientSession);
                return Future.failedFuture("No active or future subscription found for user with emailID: " + emailId);
            }

            // Build response with subscription details array
            JsonObject response = new JsonObject()
                    .put(EMAIL_ID.getPropertyName(), emailId)
                    .put("subscriptionList_Gym_DocumentArray", subscriptionDetailsArray);

            commitTransaction(clientSession);
            LOGGER.info("Subscription details retrieved successfully for emailID: {}", emailId);
            return Future.succeededFuture(response);

        } catch (MongoException e) {
            LOGGER.error("MongoDB error while retrieving subscription details for emailID {}: {}", emailId, e.getMessage(), e);
            abortTransaction(clientSession);
            return Future.failedFuture("Failed to retrieve subscription details due to MongoDB error: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to retrieve subscription details for emailID {}: {}", emailId, e.getMessage(), e);
            abortTransaction(clientSession);
            return Future.failedFuture("Failed to retrieve subscription details: " + e.getMessage());
        } finally {
            if (clientSession != null) {
                clientSession.close();
                LOGGER.debug("ClientSession closed for emailID: {}", emailId);
            }
        }
    }

    // Helper method to build subscription details
    private JsonObject buildSubscriptionDetails(Document subscription, Long currentDateMillis) {
        // Extract service details
        Document serviceDoc = subscription.get(SUBSCRIPTION_SERVICE_LIST.getPropertyName(), Document.class);
        JsonObject serviceDetails = new JsonObject()
                .put(SERVICE_OID.getPropertyName(), serviceDoc.get(SERVICE_OID.getPropertyName()).toString())
                .put(SERVICE_NAME.getPropertyName(), serviceDoc.getString(SERVICE_NAME.getPropertyName()))
                .put(DURATION_SERVICE.getPropertyName(), serviceDoc.getInteger(DURATION_SERVICE.getPropertyName()))
                .put(MEMBER_TYPE.getPropertyName(), serviceDoc.getString(MEMBER_TYPE.getPropertyName()))
                .put(AMOUNT_SERVICE.getPropertyName(), serviceDoc.getDouble(AMOUNT_SERVICE.getPropertyName()));

        // Extract add-ons details
        List<Document> addonList = subscription.getList(SUBSCRIPTION_ADDON_LIST.getPropertyName(), Document.class, new ArrayList<>());
        JsonArray addonsDetails = new JsonArray();
        for (Document addon : addonList) {
            Long addonStartDate = addon.getLong(ADDON_START_DATE.getPropertyName());
            Long addonEndDate = addon.getLong(ADDON_END_DATE.getPropertyName());
            // For active subscriptions, include only active add-ons; for future, include all
            if (subscription.getLong(SUB_START_DATE.getPropertyName()) <= currentDateMillis &&
                    addonStartDate != null && addonEndDate != null &&
                    addonStartDate <= currentDateMillis && addonEndDate > currentDateMillis) {
                JsonObject addonDetails = buildAddonDetails(addon);
                addonsDetails.add(addonDetails);
            } else if (subscription.getLong(SUB_START_DATE.getPropertyName()) > currentDateMillis) {
                JsonObject addonDetails = buildAddonDetails(addon);
                addonsDetails.add(addonDetails);
            }
        }

        return new JsonObject()
                .put(SUB_START_DATE.getPropertyName(), DateUtils.convertMillisToDateString(subscription.getLong(SUB_START_DATE.getPropertyName())))
                .put(SUB_END_DATE.getPropertyName(), DateUtils.convertMillisToDateString(subscription.getLong(SUB_END_DATE.getPropertyName())))
                .put(SUBSCRIPTION_SERVICE_LIST.getPropertyName(), serviceDetails)
                .put(SUBSCRIPTION_ADDON_LIST.getPropertyName(), addonsDetails);
    }

    // Helper method to build add-on details
    private JsonObject buildAddonDetails(Document addon) {
        return new JsonObject()
                .put(ADDON_OID.getPropertyName(), addon.get(ADDON_OID.getPropertyName()).toString())
                .put(ADDON_NAME.getPropertyName(), addon.getString(ADDON_NAME.getPropertyName()))
                .put(DESCRIPTION_ADDON.getPropertyName(), addon.getString(DESCRIPTION_ADDON.getPropertyName()))
                .put(DURATION_ADDON.getPropertyName(), addon.getInteger(DURATION_ADDON.getPropertyName()))
                .put(MEMBER_TYPE.getPropertyName(), addon.getString(MEMBER_TYPE.getPropertyName()))
                .put(AMOUNT_ADDON.getPropertyName(), addon.getDouble(AMOUNT_ADDON.getPropertyName()))
                .put(ADDON_START_DATE.getPropertyName(), DateUtils.convertMillisToDateString(addon.getLong(ADDON_START_DATE.getPropertyName())))
                .put(ADDON_END_DATE.getPropertyName(), DateUtils.convertMillisToDateString(addon.getLong(ADDON_END_DATE.getPropertyName())));
    }

    // save the fee payment request to the database
    public Future<ObjectId> saveFeePaymentRequestFuture(Document feePaymentRequest) {
        Promise<ObjectId> promise = Promise.promise();
        MongoCollection<Document> orderDetailsCollection = mongoDatabase.getCollection(GYM_ORDER_DETAILS_COLLECTION);
        try {
            // Transform feePaymentRequest to include subscriptionsList_Gym_DocumentArray
            Document transformedDoc = new Document()
                    .append(USER_ID.getPropertyName(), feePaymentRequest.getString(USER_ID.getPropertyName()))
                    .append(NAME.getPropertyName(), feePaymentRequest.getString(NAME.getPropertyName()))
                    .append(MEMBER_TYPE.getPropertyName(), feePaymentRequest.getString(MEMBER_TYPE.getPropertyName()))
                    .append(CONTACT_NUMBER.getPropertyName(), feePaymentRequest.getLong(CONTACT_NUMBER.getPropertyName()))
                    .append(EMAIL_ID.getPropertyName(), feePaymentRequest.getString(EMAIL_ID.getPropertyName()))
                    .append(AMOUNT_PAYABLE.getPropertyName(), feePaymentRequest.getDouble(AMOUNT_PAYABLE.getPropertyName()));

            // Create subscription document
            Document subscriptionDoc = new Document();


            // Add service details only if service is not null
            String serviceId = feePaymentRequest.getString(SERVICE_OID.getPropertyName());
            if (serviceId != null) {
                subscriptionDoc.append(SERVICE_OID.getPropertyName(), serviceId)
                        .append(SERVICE_NAME.getPropertyName(), feePaymentRequest.getString(SERVICE_NAME.getPropertyName()))
                        .append(AMOUNT_SERVICE.getPropertyName(), feePaymentRequest.getDouble(AMOUNT_SERVICE.getPropertyName()))
                        .append(SUB_START_DATE.getPropertyName(), feePaymentRequest.getLong(SUB_START_DATE.getPropertyName()))
                        .append(SUB_END_DATE.getPropertyName(), feePaymentRequest.getLong(SUB_END_DATE.getPropertyName()));
            }

            // Add add-ons if present
            List<Document> addonList = feePaymentRequest.getList(SUBSCRIPTION_ADDON_LIST.getPropertyName(), Document.class, new ArrayList<>());
            if (!addonList.isEmpty()) {
                subscriptionDoc.append(SUBSCRIPTION_ADDON_LIST.getPropertyName(), addonList);
            }

            // Add subscription to subscriptionsList_Gym_DocumentArray
            transformedDoc.append(SUBSCRIPTIONS_LIST.getPropertyName(), Arrays.asList(subscriptionDoc));

            // Insert transformed document
            InsertOneResult result = orderDetailsCollection.insertOne(transformedDoc);
            ObjectId insertedId = result.getInsertedId().asObjectId().getValue();

            LOGGER.info("Successfully inserted fee payment request with ID: {}", insertedId);
            promise.complete(insertedId);
        } catch (Exception e) {
            LOGGER.error("Error during MongoDB fee payment request operation: {}", e.getMessage());
            promise.fail(e);
        }
        return promise.future();
    }

    //fetch json payload request details
    public Future<Document> fetchFeePaymentResponseFuture(ObjectId feePaymentRequestId) {
        Promise<Document> promise = Promise.promise();
        MongoCollection<Document> orderDetailsCollection = mongoDatabase.getCollection(GYM_ORDER_DETAILS_COLLECTION);
        try {
            Document result = orderDetailsCollection.find(new Document().append("_id", feePaymentRequestId)).first();
            if (result != null) {
                LOGGER.info("Successfully retrieved fee payment request with ID: {}", feePaymentRequestId);
                promise.complete(result);
            } else {
                LOGGER.warn("No fee payment request found with ID: {}", feePaymentRequestId);
                promise.fail(new Exception("No fee payment request found"));
            }
        } catch (Exception e) {
            LOGGER.error("Error during MongoDB fee payment request operation for ID {}: {}", feePaymentRequestId, e.getMessage());
            promise.fail(e);
        }
        return promise.future();
    }


    /// fetching the order details as the fee payment request

    public Document fetchFeePaymentRequestDetails(ObjectId feePaymentRequestId) {
        if (feePaymentRequestId != null) {
            return findSingleDocument(mongoDatabase, ORDER_DETAILS_COLLECTION.getCollectionName(), new Document("_id", feePaymentRequestId));
        }
        return new Document();
    }

//    public List<Document> processThePaymentDetail(Document paymentDetailJson) {
//
//        List<Document> listOfFeeGroups = new ArrayList<>();
//        long totalAmountPaid = 0;
//
//        // Get payment mode first to determine which date to use
//        String paymentMode = paymentDetailJson.getString("feePaymentMode_FeeModule_Text");
//
//        String dateStringForReceipt;
//        long dateForReceipt;
//
//        if ("MANUAL".equals(paymentMode)) {
//            try {
//                // Get payment details and payment method details
//                Document paymentDetails = paymentDetailJson.get("paymentDetails", Document.class);
//                if (paymentDetails != null) {
//
//                    // Get instrument date from paymentMethodDetails
//                    if (paymentDetails.containsKey("created_at")) {
//                        Long feeCollectionDate = paymentDetails.getLong("created_at");
//
//                        if (feeCollectionDate != null && feeCollectionDate > 0) {
//                            dateStringForReceipt = DateUtils.convertMillisToDateString(feeCollectionDate);
//                            dateForReceipt = feeCollectionDate;
//                        } else {
//                            throw new IllegalArgumentException("Invalid feeCollection date");
//                        }
//                    } else {
//                        // For non-manual payments, use current date
//                        Long currentDate = DateUtils.currentDateTimeInMillis();
//                        dateStringForReceipt = DateUtils.convertMillisToDateString(currentDate);
//                        dateForReceipt = currentDate;
//                    }
//
//                } else {
//                    throw new IllegalArgumentException("Payment details not found");
//                }
//            } catch (Exception e) {
//                LOGGER.error("Error processing instrument date", e);
//                throw new IllegalArgumentException("Failed to process instrument date", e);
//            }
//        } else {
//
//            Document paymentDetails = paymentDetailJson.get("paymentDetails", Document.class);
//            Long createdAt;
//
//            if (paymentDetails != null && paymentDetails.containsKey("created_at")) {
//                createdAt = paymentDetails.get("created_at") != null ? (Long) paymentDetails.get("created_at") : 0L;
//
//                dateStringForReceipt = DateUtils.convertMillisToDateString(createdAt);
//                dateForReceipt = createdAt;
//            } else {
//                // For non-manual payments, use current date
//                Long currentDate = DateUtils.currentDateTimeInMillis();
//                dateStringForReceipt = DateUtils.convertMillisToDateString(currentDate);
//                dateForReceipt = currentDate;
//            }
//
////        }
//    }
//    }
}