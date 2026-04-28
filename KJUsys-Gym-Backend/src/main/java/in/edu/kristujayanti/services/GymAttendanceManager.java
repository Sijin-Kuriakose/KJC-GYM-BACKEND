package in.edu.kristujayanti.services;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import in.edu.kristujayanti.dbaccess.MongoDataAccess;
import in.edu.kristujayanti.util.DateUtils;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static in.edu.kristujayanti.constants.DatabaseCollectionNames.*;
import static in.edu.kristujayanti.propertyBinder.Gym.GymKeysPBinder.*;

public class GymAttendanceManager extends MongoDataAccess {
    private static final Logger LOGGER = LoggerFactory.getLogger(GymAttendanceManager.class);
    private final MongoDatabase mongoDatabase;
    private final MongoClient mongoClient;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    public GymAttendanceManager(MongoDatabase mongoDatabase, MongoClient mongoClient) {
        this.mongoDatabase = mongoDatabase;
        this.mongoClient = mongoClient;
    }
    /**
     * Handles the attendance process for a user, performing either a check-in or check-out based on the current attendance state.
     * - If no attendance record exists for today, performs a check-in.
     * - If a check-in exists without a check-out, performs a check-out.
     * - If a check-out has already been recorded, returns an error.
     *
     * @param requestBody A JsonObject containing the request details, including the user ID.
     * @return A JsonObject containing the status, message, and relevant details (e.g., check-in/check-out times, subscription, and addons) of the operation.
     *         Possible statuses include:
     *         - CHECK_IN_SUCCESS: Check-in was successful.
     *         - CHECK_OUT_SUCCESS: Check-out was successful.
     *         - FAILURE: Operation failed due to an error.
     *         - NOT_FOUND: User not found.
     *         - NO_SUBSCRIPTION: No active subscription found.
     *         - ALREADY_CHECKED_OUT: User has already checked out for today.
     */

    public JsonObject handleAttendance(JsonObject requestBody) {
        LOGGER.info("Starting handleAttendance");
        try {
            // Step 1: Extract userId from request body
            String userId = requestBody.getString(USER_ID.getPropertyName());
            if (userId == null) {
                LOGGER.info("userId_Gym_Text not found in request body.");
                return new JsonObject()
                        .put("status", "FAILURE")
                        .put("message", "User ID not found in request body.");
            }

            Bson userFilter = Filters.eq(USER_ID.getPropertyName(), userId);
            Document user = findSingleDocument(mongoDatabase, GYM_USERS_COLLECTION, userFilter);
            if (user == null) {
                LOGGER.info("User with userId {} not found.", userId);
                return new JsonObject()
                        .put("status", "NOT_FOUND")
                        .put("message", "User not found.");
            }

            String name = user.getString(NAME.getPropertyName());
            String userType = user.getString(USER_TYPE.getPropertyName());
            Boolean userStatus = user.getBoolean(STATUS.getPropertyName(), false); // Default to false if not present

            // Step 3: Validate active subscription
            List<Document> subscriptions = user.getList(SUBSCRIPTIONS_LIST.getPropertyName(), Document.class);
            if (subscriptions == null || subscriptions.isEmpty()) {
                LOGGER.info("No subscriptions found for userId {}.", userId);
                return new JsonObject()
                        .put("status", "NO_SUBSCRIPTION")
                        .put("message", "No subscriptions found.");
            }

            LocalDate today = LocalDate.now(ZoneId.of("Asia/Kolkata"));
            Document activeSubscription = null;
            for (Document subscription : subscriptions) {
                Long startDateLong = subscription.getLong(SUB_START_DATE.getPropertyName());
                Long endDateLong = subscription.getLong(SUB_END_DATE.getPropertyName());

                if (startDateLong == null || endDateLong == null) {
                    continue;
                }

                LocalDate startDate = Instant.ofEpochMilli(startDateLong).atZone(ZoneId.of("Asia/Kolkata")).toLocalDate();
                LocalDate endDate = Instant.ofEpochMilli(endDateLong).atZone(ZoneId.of("Asia/Kolkata")).toLocalDate();

                // Use user-level status and date range validation
                if (userStatus && !today.isBefore(startDate) && !today.isAfter(endDate)) {
                    activeSubscription = subscription;
                    break;
                }
            }

            if (activeSubscription == null) {
                LOGGER.info("No active subscription found for userId {}.", userId);
                return new JsonObject()
                        .put("status", "NO_SUBSCRIPTION")
                        .put("message", "No active subscription found.");
            }

            // Extract subscription and addon details
            Document subscriptionService = activeSubscription.get(SUBSCRIPTION_SERVICE_LIST.getPropertyName(), Document.class);
            String subscriptionName = (subscriptionService != null) ?
                    subscriptionService.getString(SERVICE_NAME.getPropertyName()) : "N/A";
            Long subscriptionEndDateLong = activeSubscription.getLong(SUB_END_DATE.getPropertyName());
            String subscriptionEndDate = subscriptionEndDateLong != null ?
                    Instant.ofEpochMilli(subscriptionEndDateLong).atZone(ZoneId.of("Asia/Kolkata")).format(DATE_FORMATTER) : "N/A";

            List<Document> addons = activeSubscription.getList(SUBSCRIPTION_ADDON_LIST.getPropertyName(), Document.class);
            JsonArray addonDetails = new JsonArray();
            if (addons != null) {
                for (Document addon : addons) {
                    String addonName = addon.getString(ADDON_NAME.getPropertyName());
                    Long addonEndDateLong = addon.getLong(ADDON_END_DATE.getPropertyName());
                    String addonEndDate = addonEndDateLong != null ?
                            Instant.ofEpochMilli(addonEndDateLong).atZone(ZoneId.of("Asia/Kolkata")).format(DATE_FORMATTER) : "N/A";
                    addonDetails.add(new JsonObject()
                            .put("addonName", addonName)
                            .put("endDate", addonEndDate));
                }
            }

            // Step 4: Check existing attendance for today
            String todayStr = today.format(DATE_FORMATTER);
            Long startOfDayMillis = DateUtils.getStartOfDayMillis(todayStr);
            Long endOfDayMillis = DateUtils.getEndOfDayMillis(todayStr);

            Bson attendanceFilter = Filters.and(
                    Filters.eq(USER_ID.getPropertyName(), userId),
                    Filters.gte(CHECK_IN.getPropertyName(), startOfDayMillis),
                    Filters.lte(CHECK_IN.getPropertyName(), endOfDayMillis)
            );
            Document existingAttendance = findSingleDocument(mongoDatabase, GYM_ATTENDANCE_COLLECTION, attendanceFilter);

            JsonObject result = new JsonObject()
                    .put(NAME.getPropertyName(), name)
                    .put(USER_TYPE.getPropertyName(), userType)
                    .put(USER_ID.getPropertyName(), userId)
                    .put("subscriptionName", subscriptionName)
                    .put("subscriptionEndDate", subscriptionEndDate)
                    .put("addonDetails", addonDetails);

            // Step 5: Determine whether to check-in or check-out
            if (existingAttendance == null) {
                // No attendance record for today: Perform check-in
                Long checkInTimestamp = DateUtils.currentDateTimeInMillis();
                LocalTime checkInTime = Instant.ofEpochMilli(checkInTimestamp)
                        .atZone(ZoneId.of("Asia/Kolkata"))
                        .toLocalTime();

                Document attendanceRecord = new Document()
                        .append(USER_ID.getPropertyName(), userId)
                        .append(CHECK_IN.getPropertyName(), checkInTimestamp) // Store as Long
                        .append(CHECK_OUT.getPropertyName(), null);

                ClientSession clientSession = getMongoDbSession(mongoClient);
                boolean insertResult = saveDocument(GYM_ATTENDANCE_COLLECTION, attendanceRecord, clientSession, mongoDatabase);

                if (insertResult) {
                    LOGGER.info("User {} checked in successfully at {}.", name, checkInTimestamp);
                    result.put(CHECK_IN.getPropertyName(), checkInTime.format(TIME_FORMATTER)) // Human-readable time
                            .put("status", "CHECK_IN_SUCCESS")
                            .put("message", "Check-in successful.");
                } else {
                    LOGGER.error("Failed to check in user {}.", name);
                    result.put("status", "FAILURE")
                            .put("message", "Failed to check in user.");
                }
            } else {
                // Attendance record exists: Check if check-out has already occurred
                Long existingCheckOutTimestamp = existingAttendance.getLong(CHECK_OUT.getPropertyName());
                if (existingCheckOutTimestamp != null) {
                    // User has already checked out
                    Long checkInTimestamp = existingAttendance.getLong(CHECK_IN.getPropertyName());
                    LocalTime checkInTime = Instant.ofEpochMilli(checkInTimestamp)
                            .atZone(ZoneId.of("Asia/Kolkata"))
                            .toLocalTime();
                    LocalTime checkOutTime = Instant.ofEpochMilli(existingCheckOutTimestamp)
                            .atZone(ZoneId.of("Asia/Kolkata"))
                            .toLocalTime();

                    LOGGER.info("User {} already checked out today at {}.", name, checkOutTime);
                    result.put(CHECK_IN.getPropertyName(), checkInTime.format(TIME_FORMATTER))
                            .put(CHECK_OUT.getPropertyName(), checkOutTime.format(TIME_FORMATTER))
                            .put("status", "ALREADY_CHECKED_OUT")
                            .put("message", "User has already checked out for today.");
                } else {
                    // No check-out recorded: Perform check-out
                    Long checkInTimestamp = existingAttendance.getLong(CHECK_IN.getPropertyName());
                    LocalTime checkInTime = Instant.ofEpochMilli(checkInTimestamp)
                            .atZone(ZoneId.of("Asia/Kolkata"))
                            .toLocalTime();

                    Long checkOutTimestamp = DateUtils.currentDateTimeInMillis();
                    LocalTime checkOutTime = Instant.ofEpochMilli(checkOutTimestamp)
                            .atZone(ZoneId.of("Asia/Kolkata"))
                            .toLocalTime();

                    Bson updateFilter = Filters.eq("_id", existingAttendance.getObjectId("_id"));
                    Document updateDoc = new Document("$set", new Document(CHECK_OUT.getPropertyName(), checkOutTimestamp)); // Store as Long

                    ClientSession clientSession = getMongoDbSession(mongoClient);
                    boolean updateResult = updateDocument(GYM_ATTENDANCE_COLLECTION, updateFilter, updateDoc, clientSession, mongoDatabase);

                    if (updateResult) {
                        LOGGER.info("User {} checked out successfully at {}.", name, checkOutTimestamp);
                        result.put(CHECK_IN.getPropertyName(), checkInTime.format(TIME_FORMATTER)) // Human-readable time
                                .put(CHECK_OUT.getPropertyName(), checkOutTime.format(TIME_FORMATTER)) // Human-readable time
                                .put("status", "CHECK_OUT_SUCCESS")
                                .put("message", "Check-out successful.");
                    } else {
                        LOGGER.error("Failed to check out user {}.", name);
                        result.put("status", "FAILURE")
                                .put("message", "Failed to check out user.");
                    }
                }
            }

            return result;
        } catch (Exception e) {
            LOGGER.error("Error in handleAttendance for userId {}: {}", requestBody.getString(USER_ID.getPropertyName()), e.getMessage(), e);
            return new JsonObject()
                    .put("status", "FAILURE")
                    .put("message", "Internal server error: " + e.getMessage());
        }
    }
}