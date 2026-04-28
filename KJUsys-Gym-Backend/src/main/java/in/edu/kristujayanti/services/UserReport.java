package in.edu.kristujayanti.services;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import in.edu.kristujayanti.dbaccess.MongoDataAccess;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;

import static in.edu.kristujayanti.propertyBinder.Gym.GymKeysPBinder.*;
import static in.edu.kristujayanti.constants.DatabaseCollectionNames.GYM_USERS_COLLECTION;
import static in.edu.kristujayanti.constants.DatabaseCollectionNames.GYM_ATTENDANCE_COLLECTION;

public class UserReport extends MongoDataAccess {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserReport.class);
    private final MongoDatabase database;
    private final MongoClient client;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            .withZone(ZoneId.of("Asia/Kolkata"));

    public UserReport(MongoDatabase database, MongoClient client) {
        this.database = database;
        this.client = client;
    }

    public JsonObject getUserReport(String userTypeFilter, int currentPage, int pageSize) {
        JsonArray reportArray = new JsonArray();

        try (ClientSession session = getMongoDbSession(client)) {
            session.startTransaction();

            Bson filter = (userTypeFilter != null)
                    ? Filters.regex(USER_TYPE.getPropertyName(), "^" + userTypeFilter + "$", "i")
                    : new Document();

            MongoCollection<Document> usersCollection = database.getCollection(GYM_USERS_COLLECTION);
            List<Document> users = usersCollection.find(session, filter).into(new ArrayList<>());

            session.commitTransaction();

            for (Document userDoc : users) {
                try {
                    String userId = userDoc.getString(USER_ID.getPropertyName());
                    String name = userDoc.getString(NAME.getPropertyName());
                    String userType = userDoc.getString(USER_TYPE.getPropertyName());

                    List<Document> subscriptions = userDoc.getList(SUBSCRIPTIONS_LIST.getPropertyName(), Document.class, new ArrayList<>());

                    if (subscriptions != null && !subscriptions.isEmpty()) {
                        for (Document subDoc : subscriptions) {
                            List<Document> addonDocs = subDoc.getList(SUBSCRIPTION_ADDON_LIST.getPropertyName(), Document.class, new ArrayList<>());
                            JsonArray addonArray = new JsonArray();

                            for (Document addonDoc : addonDocs) {
                                Long addonStart = addonDoc.getLong(ADDON_START_DATE.getPropertyName());
                                Long addonEnd = addonDoc.getLong(ADDON_END_DATE.getPropertyName());

                                JsonObject addonObj = new JsonObject()
                                        .put(ADDON_NAME.getPropertyName(), addonDoc.getString(ADDON_NAME.getPropertyName()))
                                        .put(ADDON_START_DATE.getPropertyName(), addonStart != null ? DATE_FORMATTER.format(Instant.ofEpochMilli(addonStart)) : null)
                                        .put(ADDON_END_DATE.getPropertyName(), addonEnd != null ? DATE_FORMATTER.format(Instant.ofEpochMilli(addonEnd)) : null);
                                addonArray.add(addonObj);
                            }

                            Document serviceDoc = subDoc.get(SUBSCRIPTION_SERVICE_LIST.getPropertyName(), Document.class);
                            String serviceName = serviceDoc != null ? serviceDoc.getString(SERVICE_NAME.getPropertyName()) : null;

                            int noOfDaysVisited = calculateDaysVisited(userId, subDoc);

                            Long subStart = subDoc.getLong(SUB_START_DATE.getPropertyName());
                            Long subEnd = subDoc.getLong(SUB_END_DATE.getPropertyName());

                            JsonObject flatObj = new JsonObject()
                                    .put(NAME.getPropertyName(), name)
                                    .put(USER_TYPE.getPropertyName(), userType)
                                    .put(USER_ID.getPropertyName(), userId)
                                    .put("subscriptionServiceName", serviceName)
                                    .put("subscriptionAddonNames", addonArray)
                                    .put(SUB_START_DATE.getPropertyName(), subStart != null ? DATE_FORMATTER.format(Instant.ofEpochMilli(subStart)) : null)
                                    .put(SUB_END_DATE.getPropertyName(), subEnd != null ? DATE_FORMATTER.format(Instant.ofEpochMilli(subEnd)) : null)
                                    .put("noOfDaysVisited_Gym_Int", noOfDaysVisited);

                            reportArray.add(flatObj);
                        }
                    }
                } catch (Exception e) {
                    LOGGER.warn("Malformed subscription skipped for user: {}", e.getMessage());
                }
            }

            int totalRecords = reportArray.size();
            int totalPages = (int) Math.ceil(totalRecords / (double) pageSize);

            int start = (currentPage - 1) * pageSize;
            int end = Math.min(start + pageSize, totalRecords);

            JsonArray pagedArray = new JsonArray();
            for (int i = start; i < end; i++) {
                pagedArray.add(reportArray.getJsonObject(i));
            }

            JsonObject pagination = new JsonObject()
                    .put("currentPage", currentPage)
                    .put("pageSize", pageSize)
                    .put("totalRecords", totalRecords)
                    .put("totalPages", totalPages);

            JsonObject result = new JsonObject()
                    .put("pagination", pagination)
                    .put("data", pagedArray);

            return new JsonObject()
                    .put("status", "SUCCESS")
                    .put("data", result)
                    .put("message", new JsonArray().add("User report fetched successfully"));

        } catch (Exception e) {
            LOGGER.error("Error generating user report: {}", e.getMessage(), e);
            return new JsonObject()
                    .put("status", "FAILURE")
                    .put("message", new JsonArray().add("Internal server error."));
        }
    }

    private int calculateDaysVisited(String userId, Document subDoc) {
        try {
            Long subStartDate = subDoc.getLong(SUB_START_DATE.getPropertyName());
            Long subEndDate = subDoc.getLong(SUB_END_DATE.getPropertyName());

            if (subStartDate == null || subEndDate == null) {
                return 0;
            }

            LOGGER.info("Calculating days visited for user {} between {} and {}", userId, subStartDate, subEndDate);

            if (subEndDate > System.currentTimeMillis()) {
                subEndDate = System.currentTimeMillis();
            }

            MongoCollection<Document> attendanceCollection = database.getCollection(GYM_ATTENDANCE_COLLECTION);

            Bson attendanceFilter = Filters.and(
                    Filters.eq("userId_Gym_Text", userId),
                    Filters.gte("checkIn_Gym_DateTime", subStartDate),
                    Filters.lte("checkIn_Gym_DateTime", subEndDate)
            );

            List<Document> attendanceDocs = attendanceCollection.find(attendanceFilter).into(new ArrayList<>());

            return attendanceDocs.size();

        } catch (Exception e) {
            LOGGER.error("Error calculating days visited: {}", e.getMessage(), e);
            return -1;
        }
    }
}
