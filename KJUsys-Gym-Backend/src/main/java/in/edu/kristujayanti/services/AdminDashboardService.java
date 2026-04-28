package in.edu.kristujayanti.services;

import com.mongodb.client.FindIterable;
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
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static in.edu.kristujayanti.constants.DatabaseCollectionNames.*;
import static in.edu.kristujayanti.propertyBinder.Gym.GymKeysPBinder.*;

/**
 * Service class to manage admin dashboard data retrieval.
 */
public class AdminDashboardService extends MongoDataAccess {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminDashboardService.class);
    private final MongoDatabase mongoDatabase;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    public AdminDashboardService(MongoDatabase mongoDatabase, MongoClient mongoClient) {
        this.mongoDatabase = mongoDatabase;
    }

    /**
     * Retrieves dashboard data including total active members, today's check-ins, members currently at the gym,
     * and a paginated list of members who checked in or out within the specified date range.
     * Dashboard metrics (totalActiveMembers, todaysCheckIns, currentlyAtGym) are always calculated for the current day,
     * while the paginated attendance data respects the provided from and to dates.
     *
     * @param page The page number for pagination (1-based index).
     * @param size The number of records per page for pagination.
     * @param from The start of the date range in milliseconds (inclusive) for paginated data.
     * @param to   The end of the date range in milliseconds (inclusive) for paginated data.
     * @return A JsonObject containing the status, pagination details, member data, and dashboard metrics.
     *         Possible statuses include:
     *         - "SUCCESS": Data retrieved successfully.
     *         - "NO_DATA": No data found for the given criteria.
     *         - "FAILURE": An error occurred during data retrieval.
     */
    public JsonObject getDashboardData(int page, int size, Long from, Long to) {
        JsonObject result = new JsonObject();
        try {
            // Step 1: Calculate the current day's date range for metrics
            LocalDate today = LocalDate.now();
            String todayStr = today.format(DATE_FORMATTER);
            Long startOfTodayMillis = DateUtils.getStartOfDayMillis(todayStr);
            Long endOfTodayMillis = DateUtils.getEndOfDayMillis(todayStr);

            // Step 2: Calculate total active members for the current day
            // A subscription is active if it overlaps with the current day
            long totalActiveMembers = mongoDatabase.getCollection(GYM_USERS_COLLECTION).countDocuments(
                    Filters.and(
                            Filters.exists(SUBSCRIPTIONS_LIST.getPropertyName()),
                            Filters.eq(STATUS.getPropertyName(), true), // Check user-level status is true
                            Filters.elemMatch(SUBSCRIPTIONS_LIST.getPropertyName(),
                                    Filters.and(
                                            Filters.lte(SUB_START_DATE.getPropertyName(), endOfTodayMillis),
                                            Filters.gte(SUB_END_DATE.getPropertyName(), startOfTodayMillis)
                                    )
                            )
                    )
            );

            // Step 3: Count check-ins for the current day
            long todaysCheckIns = mongoDatabase.getCollection(GYM_ATTENDANCE_COLLECTION).countDocuments(
                    Filters.and(
                            Filters.gte(CHECK_IN.getPropertyName(), startOfTodayMillis),
                            Filters.lte(CHECK_IN.getPropertyName(), endOfTodayMillis)
                    )
            );

            // Step 4: Count members currently at the gym (checked in today, not checked out or checked out after today)
            long currentlyAtGym = mongoDatabase.getCollection(GYM_ATTENDANCE_COLLECTION).countDocuments(
                    Filters.and(
                            Filters.gte(CHECK_IN.getPropertyName(), startOfTodayMillis),
                            Filters.lte(CHECK_IN.getPropertyName(), endOfTodayMillis),
                            Filters.or(
                                    Filters.eq(CHECK_OUT.getPropertyName(), null),
                                    Filters.gt(CHECK_OUT.getPropertyName(), endOfTodayMillis)
                            )
                    )
            );
            // Step 5: Count total payments made in the current month
            // Calculate the start and end of the current month
            LocalDate firstDayOfMonth = today.withDayOfMonth(1);
            String firstDayStr = firstDayOfMonth.format(DATE_FORMATTER);
            LocalDate lastDayOfMonth = today.withDayOfMonth(today.lengthOfMonth());
            String lastDayStr = lastDayOfMonth.format(DATE_FORMATTER);

            // Convert to seconds (since captured_at is in seconds)
            long startOfMonthSeconds = DateUtils.getStartOfDayMillis(firstDayStr) / 1000;
            long endOfMonthSeconds = DateUtils.getEndOfDayMillis(lastDayStr) / 1000;

            // Count payments in the current month
            long paymentsThisMonth = mongoDatabase.getCollection(GYM_PAYMENTS_COLLECTION).countDocuments(
                    Filters.and(
                            Filters.gte("captured_at", startOfMonthSeconds),
                            Filters.lte("captured_at", endOfMonthSeconds)
                    )
            );
            // Step 6: Retrieve paginated attendance data within the provided date range (from and to)
            Bson attendanceFilter = Filters.and(
                    Filters.gte(CHECK_IN.getPropertyName(), from),
                    Filters.lte(CHECK_IN.getPropertyName(), to)
            );
            long totalRecords = mongoDatabase.getCollection(GYM_ATTENDANCE_COLLECTION).countDocuments(attendanceFilter);
            int totalPages = (int) Math.ceil((double) totalRecords / size);
            int skip = (page - 1) * size;

            FindIterable<Document> attendanceDocs = findDocuments(mongoDatabase, GYM_ATTENDANCE_COLLECTION, attendanceFilter, new Document())
                    .skip(skip)
                    .limit(size);
            JsonArray members = new JsonArray();
            for (Document doc : attendanceDocs) {
                String userId = doc.getString(USER_ID.getPropertyName());
                Bson userFilter = Filters.eq(USER_ID.getPropertyName(), userId);
                Document user = findSingleDocument(mongoDatabase, GYM_USERS_COLLECTION, userFilter);
                if (user != null) {
                    JsonObject member = new JsonObject()
                            .put(NAME.getPropertyName(), user.getString(NAME.getPropertyName()))
                            .put(USER_TYPE.getPropertyName(), user.getString(USER_TYPE.getPropertyName()))
                            .put(USER_ID.getPropertyName(), userId)
                            .put(CHECK_IN.getPropertyName(), Instant.ofEpochMilli(doc.getLong(CHECK_IN.getPropertyName()))
                                    .atZone(ZoneId.of("Asia/Kolkata")).toLocalTime().format(TIME_FORMATTER));
                    Long checkOutTimestamp = doc.getLong(CHECK_OUT.getPropertyName());
                    if (checkOutTimestamp != null) {
                        member.put(CHECK_OUT.getPropertyName(), Instant.ofEpochMilli(checkOutTimestamp)
                                .atZone(ZoneId.of("Asia/Kolkata")).toLocalTime().format(TIME_FORMATTER));
                    } else {
                        member.put(CHECK_OUT.getPropertyName(), null);
                    }
                    members.add(member);
                }
            }

            // Step 7: Build pagination and metrics objects
            JsonObject pagination = new JsonObject()
                    .put("currentPage", page)
                    .put("pageSize", size)
                    .put("totalRecords", totalRecords)
                    .put("totalPages", totalPages);

            JsonObject dashboardMetrics = new JsonObject()
                    .put("totalActiveMembers", totalActiveMembers)
                    .put("todaysCheckIns", todaysCheckIns)
                    .put("currentlyAtGym", currentlyAtGym)
                    .put("paymentsThisMonth",paymentsThisMonth);

            // Step 8: Construct the final result
            result.put("status", "SUCCESS")
                    .put("pagination", pagination)
                    .put("data", members)
                    .put("dashboardMetrics", dashboardMetrics);

            // Check if paginated data is empty and adjust status if needed
            if (members.isEmpty() && totalRecords == 0) {
                result.put("status", "NO_DATA");
            }

            return result;
        } catch (Exception e) {
            LOGGER.error("Error in getDashboardData: {}", e.getMessage(), e);
            return new JsonObject().put("status", "FAILURE");
        }
    }
}