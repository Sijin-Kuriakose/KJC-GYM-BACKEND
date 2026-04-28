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
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static in.edu.kristujayanti.constants.DatabaseCollectionNames.*;
import static in.edu.kristujayanti.propertyBinder.Gym.GymKeysPBinder.*;

public class PaymentReport extends MongoDataAccess {
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentReport.class);
    private final MongoDatabase database;
    private final MongoClient client;

    public PaymentReport(MongoDatabase database, MongoClient client) {
        this.database = database;
        this.client = client;
    }

    public JsonObject getPaymentReport(String userTypeFilter, Long fromDateMillis, Long toDateMillis, int page, int pageSize) {
        JsonArray reportArray = new JsonArray();

        try (ClientSession session = getMongoDbSession(client)) {
            session.startTransaction();

            MongoCollection<Document> paymentCollection = database.getCollection(GYM_PAYMENTS_COLLECTION);
            MongoCollection<Document> userCollection = database.getCollection(GYM_USERS_COLLECTION);

            int skip = (page - 1) * pageSize;

            // Build the query filter
            List<Bson> filters = new ArrayList<>();
            if (fromDateMillis != null && toDateMillis != null) {
                filters.add(Filters.gte("paymentDate_Gym_Date", fromDateMillis));
                filters.add(Filters.lte("paymentDate_Gym_Date", toDateMillis));
            }

            Bson query = filters.isEmpty() ? new Document() : Filters.and(filters);

            // Count total records for pagination
            long totalRecords = paymentCollection.countDocuments(session, query);
            int totalPages = (int) Math.ceil(totalRecords / (double) pageSize);

            List<Document> paymentDocs = paymentCollection.find(session, query)
                    .skip(skip)
                    .limit(pageSize)
                    .into(new ArrayList<>());

            for (Document paymentDoc : paymentDocs) {
                String userId = paymentDoc.getString("userId_Gym_Text");
                String plans = paymentDoc.getString("plans_Gym_Text");
                Double paymentAmount = null;

                // Handle both Long and String date types
                Object dateObj = paymentDoc.get("paymentDate_Gym_Date");
                Long millis = null;
                if (dateObj instanceof Long) {
                    millis = (Long) dateObj;
                } else if (dateObj instanceof String) {
                    try {
                        millis = Long.parseLong((String) dateObj);
                    } catch (NumberFormatException e) {
                        millis = null; // Skip if not a valid number
                    }
                }

                String formattedDate = null;
                if (millis != null) {
                    Instant instant = Instant.ofEpochMilli(millis);
                    LocalDate localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();
                    formattedDate = localDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                }

                Object amountObj = paymentDoc.get("amount_Payable_Gym_Double");
                if (amountObj instanceof Double) {
                    paymentAmount = (Double) amountObj;
                } else if (amountObj instanceof Integer) {
                    paymentAmount = ((Integer) amountObj).doubleValue();
                }

                // Fetch user details
                Bson userFilter = Filters.eq(USER_ID.getPropertyName(), userId);
                Document userDoc = userCollection.find(session, userFilter).first();

                if (userDoc != null) {
                    String name = userDoc.getString(NAME.getPropertyName());
                    String userType = userDoc.getString(USER_TYPE.getPropertyName());

                    if (userTypeFilter == null || userType.equalsIgnoreCase(userTypeFilter)) {
                        JsonObject flatObj = new JsonObject()
                                .put(NAME.getPropertyName(), name)
                                .put(USER_TYPE.getPropertyName(), userType)
                                .put(USER_ID.getPropertyName(), userId)
                                .put("plans_Gym_Text", plans)
                                .put(AMOUNT_PAYABLE.getPropertyName(), paymentAmount)
                                .put(PAYMENT_DATE.getPropertyName(), formattedDate)
                                .put("paymentMode_Gym_Text", (String) null); // Placeholder

                        reportArray.add(flatObj);
                    }
                }
            }

            session.commitTransaction();

            JsonObject pagination = new JsonObject()
                    .put("currentPage", page)
                    .put("pageSize", pageSize)
                    .put("totalRecords", totalRecords)
                    .put("totalPages", totalPages);

            JsonObject result = new JsonObject()
                    .put("pagination", pagination)
                    .put("data", reportArray);

            return new JsonObject()
                    .put("status", "SUCCESS")
                    .put("data", result)
                    .put("message", new JsonArray().add("Report fetched successfully"));

        } catch (Exception e) {
            LOGGER.error("Error generating user report: {}", e.getMessage(), e);
            return new JsonObject()
                    .put("status", "FAILURE")
                    .put("message", new JsonArray().add("Internal server error."));
        }
    }
}