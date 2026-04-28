package in.edu.kristujayanti.services;

import com.mongodb.client.ClientSession;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import in.edu.kristujayanti.dbaccess.MongoDataAccess;
import in.edu.kristujayanti.exception.DataAccessException;
import in.edu.kristujayanti.util.DateUtils;
import in.edu.kristujayanti.util.ResponseUtil;
import in.edu.kristujayanti.util.UserInfoUtil;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static in.edu.kristujayanti.constants.DatabaseCollectionNames.GYM_SERVICES_COLLECTION;
import static in.edu.kristujayanti.propertyBinder.Gym.GymKeysPBinder.*;

public class ServiceManager extends MongoDataAccess {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceManager.class);
    private final MongoDatabase mongoDatabase;
    private final MongoClient mongoClient;

    private static final String SERVICE_NOT_FOUND_LOG = "Service with ID {} not found.";
    private static final String SERVICE_UPDATE_SUCCESS_LOG = "Service with ID {} updated successfully.";
    private static final String SERVICE_UPDATE_FAILED_LOG = "Failed to update service with ID {}.";
    private static final String SERVICE_DELETE_SUCCESS_LOG = "Service with ID {} deleted successfully.";
    private static final String SERVICE_DELETE_FAILED_LOG = "Failed to delete service with ID {}.";
    private static final String SERVICE_INACTIVE_LOG = "Update rejected: Service with ID {} is inactive.";

    public ServiceManager(MongoDatabase mongoDatabase, MongoClient mongoClient) {
        this.mongoDatabase = mongoDatabase;
        this.mongoClient = mongoClient;
    }

    // --- Create Service ---
    public JsonObject insertServiceDetails(Document insertServiceRequiredDetails, RoutingContext context) {
        try {
            Bson filter = Filters.and(
                    Filters.eq(SERVICE_NAME.getPropertyName(), insertServiceRequiredDetails.getString(SERVICE_NAME.getPropertyName())),
                    Filters.eq(MEMBER_TYPE.getPropertyName(), insertServiceRequiredDetails.getString(MEMBER_TYPE.getPropertyName()))
            );

            Document existingService = findSingleDocument(mongoDatabase, GYM_SERVICES_COLLECTION, filter);

            JsonObject result = new JsonObject();
            if (existingService != null) {
                LOGGER.info("Duplicate service detected. Service not added.");
                result.put("status", "DUPLICATE");
                return result;
            }

            Document serviceToBeAdded = prepareServiceDetailsDocument(insertServiceRequiredDetails, context);
            ClientSession clientSession = getMongoDbSession(mongoClient);
            boolean insertResult = saveDocument(GYM_SERVICES_COLLECTION, serviceToBeAdded, clientSession, mongoDatabase);

            if (insertResult) {
                LOGGER.info("New Service Added Successfully.");
                result.put("status", "SUCCESS");
            } else {
                LOGGER.info("Failed to add new Service.");
                result.put("status", "FAILURE");
            }
            return result;
        } catch (Exception e) {
            LOGGER.error("Error in insertServiceDetails method: {}", e.getMessage());
            throw new DataAccessException("Error in insertServiceDetails method: " + e.getMessage());
        }
    }

    private Document prepareServiceDetailsDocument(Document insertServiceRequiredDetails, RoutingContext context) {
        ObjectId serviceId = new ObjectId();
        String loggedInUserId = UserInfoUtil.getLoggedInUserId(context.request().headers());
        long createdAt = DateUtils.getCurrentTimeInMillis();

        Document newService = new Document();
        newService.append("_id", serviceId)
                .append(MEMBER_TYPE.getPropertyName(), insertServiceRequiredDetails.getString(MEMBER_TYPE.getPropertyName()))
                .append(SERVICE_NAME.getPropertyName(), insertServiceRequiredDetails.getString(SERVICE_NAME.getPropertyName()))
                .append(DURATION_SERVICE.getPropertyName(), insertServiceRequiredDetails.getInteger(DURATION_SERVICE.getPropertyName()))
                .append(AMOUNT_SERVICE.getPropertyName(), insertServiceRequiredDetails.getDouble(AMOUNT_SERVICE.getPropertyName()))
                .append(SERVICE_STATUS.getPropertyName(), true) // Default to true (active)
                .append("createdBy_Gym_Text", loggedInUserId != null ? loggedInUserId : "unknown") // Internal field
                .append("createdAt_Gym_Long", createdAt); // Internal field
        return newService;
    }

    // --- View Services ---
    public JsonObject getAllServices(int page, int size, String memberType, Boolean isActive) {
        try {
            Bson filter = new Document();
            if (memberType != null) {
                filter = Filters.and(filter, Filters.eq(MEMBER_TYPE.getPropertyName(), memberType));
            }
            // Apply isActive filter if provided
            if (isActive != null) {
                filter = Filters.and(filter, Filters.eq(SERVICE_STATUS.getPropertyName(), isActive));
            }

            FindIterable<Document> services = findDocuments(mongoDatabase, GYM_SERVICES_COLLECTION, filter, new Document())
                    .skip((page - 1) * size)
                    .limit(size);

            JsonArray servicesArray = new JsonArray();
            for (Document service : services) {
                // Exclude internal fields from the response
                Document filteredService = new Document(service);
                filteredService.remove("createdBy_Gym_Text");
                filteredService.remove("createdAt_Gym_Long");
                filteredService.remove("lastModifiedBy_Gym_Text");
                filteredService.remove("lastModifiedAt_Gym_Long");
                ResponseUtil.processResponseDocument(filteredService); // Process the document to convert ObjectId fields
                servicesArray.add(new JsonObject(filteredService.toJson()));
            }

            // Calculate total records
            long totalRecords = mongoDatabase.getCollection(GYM_SERVICES_COLLECTION).countDocuments(filter);
            int totalPages = (int) Math.ceil((double) totalRecords / size);

            JsonObject result = new JsonObject();
            if (servicesArray.isEmpty()) {
                LOGGER.info("No services found in the database.");
                result.put("status", "NO_DATA");
            } else {
                LOGGER.info("Successfully retrieved {} services.", servicesArray.size());
                result.put("status", "SUCCESS")
                        .put("services", servicesArray)
                        .put("pagination", new JsonObject()
                                .put("currentPage", page)
                                .put("pageSize", size)
                                .put("totalRecords", totalRecords)
                                .put("totalPages", totalPages));
            }
            return result;
        } catch (Exception e) {
            LOGGER.error("Error retrieving services: {}", e.getMessage());
            return new JsonObject().put("status", "FAILURE");
        }
    }

    // --- Export Services ---
    public List<Document> getAllServicesAsDocuments(int page, int size, String memberType) {
        try {
            FindIterable<Document> services;
            if (memberType != null) {
                Bson filter = Filters.eq(MEMBER_TYPE.getPropertyName(), memberType);
                services = findDocuments(mongoDatabase, GYM_SERVICES_COLLECTION, filter, new Document())
                        .skip((page - 1) * size)
                        .limit(size);
            } else {
                services = findDocuments(mongoDatabase, GYM_SERVICES_COLLECTION, new Document(), new Document())
                        .skip((page - 1) * size)
                        .limit(size);
            }

            List<Document> serviceList = new ArrayList<>();
            for (Document service : services) {
                ResponseUtil.processResponseDocument(service); // Process the document to convert ObjectId fields
                // Add status field based on SERVICE_STATUS
                Boolean isActive = service.getBoolean(SERVICE_STATUS.getPropertyName(), true); // Default to true if not present
                service.append("status", isActive);
                serviceList.add(service); // Include all fields, including internal ones, for export
            }

            return serviceList;
        } catch (Exception e) {
            LOGGER.error("Error retrieving services as documents: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    public JsonObject getServiceById(String serviceId) {
        try {
            if (!ObjectId.isValid(serviceId)) {
                return new JsonObject().put("status", "NOT_FOUND");
            }

            Bson filter = Filters.eq("_id", new ObjectId(serviceId));
            Document service = findSingleDocument(mongoDatabase, GYM_SERVICES_COLLECTION, filter);

            JsonObject result = new JsonObject();
            if (service == null) {
                LOGGER.info(SERVICE_NOT_FOUND_LOG, serviceId);
                result.put("status", "NOT_FOUND");
            } else {
                // Exclude internal fields from the response
                Document filteredService = new Document(service);
                filteredService.remove("createdBy_Gym_Text");
                filteredService.remove("createdAt_Gym_Long");
                filteredService.remove("lastModifiedBy_Gym_Text");
                filteredService.remove("lastModifiedAt_Gym_Long");
                ResponseUtil.processResponseDocument(filteredService); // Process the document to convert ObjectId fields
                JsonArray servicesArray = new JsonArray().add(new JsonObject(filteredService.toJson()));
                LOGGER.info("Successfully retrieved service with ID {}.", serviceId);
                result.put("status", "SUCCESS").put("services", servicesArray);
            }
            return result;
        } catch (Exception e) {
            LOGGER.error("Error retrieving service with ID {}: {}", serviceId, e.getMessage());
            return new JsonObject().put("status", "FAILURE");
        }
    }

    // --- Edit Service ---
    public JsonObject updateService(String serviceId, Document updateServiceDetails, RoutingContext context) {
        try {
            if (!ObjectId.isValid(serviceId)) {
                return new JsonObject().put("status", "NOT_FOUND");
            }

            Bson filter = Filters.eq("_id", new ObjectId(serviceId));
            Document existingService = findSingleDocument(mongoDatabase, GYM_SERVICES_COLLECTION, filter);

            if (existingService == null) {
                LOGGER.info(SERVICE_NOT_FOUND_LOG, serviceId);
                return new JsonObject().put("status", "NOT_FOUND");
            }

            Bson duplicateFilter = Filters.and(
                    Filters.eq(SERVICE_NAME.getPropertyName(), updateServiceDetails.getString(SERVICE_NAME.getPropertyName())),
                    Filters.eq(MEMBER_TYPE.getPropertyName(), updateServiceDetails.getString(MEMBER_TYPE.getPropertyName())),
                    Filters.ne("_id", new ObjectId(serviceId))
            );
            Document duplicateService = findSingleDocument(mongoDatabase, GYM_SERVICES_COLLECTION, duplicateFilter);

            if (duplicateService != null) {
                LOGGER.info("A service with the same name and member type already exists.");
                return new JsonObject().put("status", "DUPLICATE");
            }

            Document updateDoc = new Document();
            updateDoc.append(MEMBER_TYPE.getPropertyName(), updateServiceDetails.getString(MEMBER_TYPE.getPropertyName()))
                    .append(SERVICE_NAME.getPropertyName(), updateServiceDetails.getString(SERVICE_NAME.getPropertyName()))
                    .append(DURATION_SERVICE.getPropertyName(), updateServiceDetails.getInteger(DURATION_SERVICE.getPropertyName()))
                    .append(AMOUNT_SERVICE.getPropertyName(), updateServiceDetails.getDouble(AMOUNT_SERVICE.getPropertyName()))
                    .append("lastModifiedBy_Gym_Text", UserInfoUtil.getLoggedInUserId(context.request().headers()) != null ? UserInfoUtil.getLoggedInUserId(context.request().headers()) : "unknown") // Internal field
                    .append("lastModifiedAt_Gym_Long", DateUtils.getCurrentTimeInMillis()); // Internal field

            Document setDoc = new Document("$set", updateDoc);

            ClientSession clientSession = getMongoDbSession(mongoClient);
            boolean updateResult = updateDocument(GYM_SERVICES_COLLECTION, filter, setDoc, clientSession, mongoDatabase);

            JsonObject result = new JsonObject();
            result.put("status", updateResult ? "SUCCESS" : "FAILURE");
            LOGGER.info(updateResult ? SERVICE_UPDATE_SUCCESS_LOG : SERVICE_UPDATE_FAILED_LOG, serviceId);
            if (updateResult) {
                result.put("message", "Service updated successfully.");
            }
            return result;
        } catch (Exception e) {
            LOGGER.error("Error updating service with ID {}: {}", serviceId, e.getMessage());
            return new JsonObject().put("status", "FAILURE");
        }
    }

    // --- Delete Service ---
    public JsonObject deleteService(String serviceId) {
        try {
            if (!ObjectId.isValid(serviceId)) {
                return new JsonObject().put("status", "NOT_FOUND");
            }

            Bson filter = Filters.eq("_id", new ObjectId(serviceId));
            Document existingService = findSingleDocument(mongoDatabase, GYM_SERVICES_COLLECTION, filter);

            if (existingService == null) {
                LOGGER.info(SERVICE_NOT_FOUND_LOG, serviceId);
                return new JsonObject().put("status", "NOT_FOUND");
            }

            ClientSession clientSession = getMongoDbSession(mongoClient);
            boolean deleteResult = deleteDocument(GYM_SERVICES_COLLECTION, filter, clientSession, mongoDatabase);

            JsonObject result = new JsonObject();
            result.put("status", deleteResult ? "SUCCESS" : "FAILURE");
            LOGGER.info(deleteResult ? SERVICE_DELETE_SUCCESS_LOG : SERVICE_DELETE_FAILED_LOG, serviceId);
            if (deleteResult) {
                result.put("message", "Service deleted successfully.");
            }
            return result;
        } catch (Exception e) {
            LOGGER.error("Error deleting service with ID {}: {}", serviceId, e.getMessage());
            return new JsonObject().put("status", "FAILURE");
        }
    }

    // --- Update Service Status ---
    public JsonObject updateServiceStatus(String serviceId, boolean isActive) {
        try {
            if (!ObjectId.isValid(serviceId)) {
                return new JsonObject().put("status", "NOT_FOUND");
            }

            Bson filter = Filters.eq("_id", new ObjectId(serviceId));
            Document existingService = findSingleDocument(mongoDatabase, GYM_SERVICES_COLLECTION, filter);

            if (existingService == null) {
                LOGGER.info(SERVICE_NOT_FOUND_LOG, serviceId);
                return new JsonObject().put("status", "NOT_FOUND");
            }

            // Update the status to the specified value
            Document updateDoc = new Document("$set", new Document(SERVICE_STATUS.getPropertyName(), isActive));
            ClientSession clientSession = getMongoDbSession(mongoClient);
            boolean updateResult = updateDocument(GYM_SERVICES_COLLECTION, filter, updateDoc, clientSession, mongoDatabase);

            JsonObject result = new JsonObject();
            if (updateResult) {
                LOGGER.info("Service with ID {} marked as {} successfully.", serviceId, isActive ? "active" : "inactive");
                result.put("status", "SUCCESS").put("message", "Service marked as " + (isActive ? "active" : "inactive") + " successfully.");
            } else {
                LOGGER.error("Failed to mark service with ID {} as {}.", serviceId, isActive ? "active" : "inactive");
                result.put("status", "FAILURE").put("message", "Failed to mark service as " + (isActive ? "active" : "inactive") + ".");
            }
            return result;
        } catch (Exception e) {
            LOGGER.error("Error updating service status with ID {}: {}", serviceId, e.getMessage());
            return new JsonObject().put("status", "FAILURE").put("message", "Internal server error.");
        }
    }
}