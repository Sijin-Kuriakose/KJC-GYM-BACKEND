package in.edu.kristujayanti.services;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import in.edu.kristujayanti.dbaccess.MongoDataAccess;
import in.edu.kristujayanti.exception.DataAccessException;
import in.edu.kristujayanti.util.ResponseUtil;
import in.edu.kristujayanti.util.DateUtils;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static in.edu.kristujayanti.constants.DatabaseCollectionNames.GYM_ADDONS_COLLECTION;
import static in.edu.kristujayanti.propertyBinder.Gym.GymKeysPBinder.*;

public class AddonService extends MongoDataAccess {
    private static final Logger LOGGER = LoggerFactory.getLogger(AddonService.class);
    private final MongoDatabase database;
    private final MongoClient client;

    public AddonService(MongoDatabase database, MongoClient client) {
        this.database = database;
        this.client = client;
    }

    // Create addon
    public JsonObject createAddon(Document addonData) {
        try {
            normalizeAddonFields(addonData);

            Bson queryFilter = Filters.and(
                    Filters.eq(ADDON_NAME.getPropertyName(), addonData.getString(ADDON_NAME.getPropertyName())),
                    Filters.eq(MEMBER_TYPE.getPropertyName(), addonData.getString(MEMBER_TYPE.getPropertyName()))
            );

            if (documentExists(queryFilter)) {
                LOGGER.info("Duplicate addon found.");
                return new JsonObject().put("status", "DUPLICATE");
            }

            Document newAddon = buildAddonDocument(addonData);
            boolean isInserted = saveDocument(GYM_ADDONS_COLLECTION, newAddon, getMongoDbSession(client), database);

            LOGGER.info(isInserted ? "Addon inserted successfully." : "Addon insertion failed.");
            return new JsonObject().put("status", isInserted ? "SUCCESS" : "FAILURE");
        } catch (Exception e) {
            LOGGER.error("Error inserting addon: {}", e.getMessage());
            throw new DataAccessException("Error in createAddon: " + e.getMessage());
        }
    }

    private Document buildAddonDocument(Document addonData) {
        return new Document()
                .append(MEMBER_TYPE.getPropertyName(), addonData.getString(MEMBER_TYPE.getPropertyName()))
                .append(ADDON_NAME.getPropertyName(), addonData.getString(ADDON_NAME.getPropertyName()))
                .append(DESCRIPTION_ADDON.getPropertyName(), addonData.getString(DESCRIPTION_ADDON.getPropertyName()))
                .append(DURATION_ADDON.getPropertyName(), addonData.getInteger(DURATION_ADDON.getPropertyName()))
                .append(AMOUNT_ADDON.getPropertyName(), addonData.getDouble(AMOUNT_ADDON.getPropertyName()))
                .append(ADDON_STATUS.getPropertyName(), addonData.getBoolean(ADDON_STATUS.getPropertyName()))
                .append("createdBy_Gym_Text", addonData.getString("createdBy_Gym_Text"))
                .append("createdAt_Gym_Long", addonData.getLong("createdAt_Gym_Long"));
    }

    // View addons
    public JsonObject getAllAddons(int page, int size, String memberType, Boolean status) {
        try {
            Bson queryFilter = new Document(); // Default empty filter

            // Check for memberType filter
            if (memberType != null) {
                queryFilter = Filters.eq("memberType_Gym_Text", memberType.toUpperCase());
            }

            // Check for status filter (either true or false)
            if (status != null) {
                queryFilter = Filters.and(queryFilter, Filters.eq("addon_Status_Bool", status));
            }

            // Query the database with the filters applied
            FindIterable<Document> addons = findDocuments(database, GYM_ADDONS_COLLECTION, queryFilter, new Document())
                    .skip((page - 1) * size)
                    .limit(size);

            JsonArray addonListJson = new JsonArray();
            for (Document addon : addons) {
                addon.remove("createdBy_Gym_Text");
                addon.remove("createdAt_Gym_Long");
                addon.remove("lastModifiedBy_Gym_Text");
                addon.remove("lastModifiedAt_Gym_Long");
                ResponseUtil.processResponseDocument(addon);
                addonListJson.add(new JsonObject(addon.toJson()));
            }

            long totalRecords = database.getCollection(GYM_ADDONS_COLLECTION).countDocuments(queryFilter);
            int totalPages = (int) Math.ceil((double) totalRecords / size);

            JsonObject result = new JsonObject();
            if (addonListJson.isEmpty()) {
                LOGGER.info("No addons found.");
                result.put("status", "NOT_FOUND");
            } else {
                LOGGER.info("Successfully retrieved {} addons.", addonListJson.size());

                JsonObject dataObject = new JsonObject()
                        .put("pagination", new JsonObject()
                                .put("currentPage", page)
                                .put("pageSize", size)
                                .put("totalRecords", totalRecords)
                                .put("totalPages", totalPages))
                        .put("data", addonListJson);

                result.put("status", "SUCCESS")
                        .put("data", dataObject)
                        .put("message", new JsonArray().add("Addons fetched successfully"));
            }
            return result;
        } catch (Exception e) {
            LOGGER.error("Error retrieving addons: {}", e.getMessage(), e);
            return new JsonObject().put("status", "FAILURE");
        }
    }

    public JsonObject getAddonById(String addonId) {
        if (!ObjectId.isValid(addonId)) {
            LOGGER.info("Invalid addon ID.");
            return new JsonObject().put("status", "NOT_FOUND");
        }

        try {
            Bson filter = Filters.and(
                    Filters.eq("_id", new ObjectId(addonId)),
                    Filters.eq("addon_Status_Bool", true)
            );

            Document addon = findSingleDocument(database, GYM_ADDONS_COLLECTION, filter);

            if (addon == null) {
                LOGGER.info("Addon not found or inactive.");
                return new JsonObject().put("status", "NOT_FOUND");
            }

            addon.remove("createdBy_Gym_Text");
            addon.remove("createdAt_Gym_Long");
            addon.remove("lastModifiedBy_Gym_Text");
            addon.remove("lastModifiedAt_Gym_Long");
            ResponseUtil.processResponseDocument(addon);

            return new JsonObject()
                    .put("status", "SUCCESS")
                    .put("addon", new JsonObject(addon.toJson()))
                    .put("message", new JsonArray().add("Addon fetched successfully"));
        } catch (Exception e) {
            LOGGER.error("Error fetching addon by ID: {}", e.getMessage());
            return new JsonObject().put("status", "FAILURE");
        }
    }


    // Update addon
    public JsonObject updateAddon(String addonId, Document updateData, String modifiedBy) {
        if (!ObjectId.isValid(addonId)) {
            return new JsonObject().put("status", "NOT_FOUND");
        }

        try {
            Bson queryFilter = Filters.eq("_id", new ObjectId(addonId));
            Document existingAddon = findSingleDocument(database, GYM_ADDONS_COLLECTION, queryFilter);

            if (existingAddon == null) {
                LOGGER.info("Addon not found.");
                return new JsonObject().put("status", "NOT_FOUND");
            }

            updateData.remove("_id");
            normalizeAddonFields(updateData);

            Bson duplicationCheckFilter = Filters.and(
                    Filters.eq(ADDON_NAME.getPropertyName(), updateData.getString(ADDON_NAME.getPropertyName())),
                    Filters.eq(MEMBER_TYPE.getPropertyName(), updateData.getString(MEMBER_TYPE.getPropertyName())),
                    Filters.ne("_id", new ObjectId(addonId))
            );

            if (documentExists(duplicationCheckFilter)) {
                LOGGER.info("Duplicate addon found.");
                return new JsonObject().put("status", "DUPLICATE");
            }

            boolean isSame =
                    updateData.getString(MEMBER_TYPE.getPropertyName()).equalsIgnoreCase(existingAddon.getString(MEMBER_TYPE.getPropertyName())) &&
                            updateData.getString(ADDON_NAME.getPropertyName()).equalsIgnoreCase(existingAddon.getString(ADDON_NAME.getPropertyName())) &&
                            updateData.getString(DESCRIPTION_ADDON.getPropertyName()).equalsIgnoreCase(existingAddon.getString(DESCRIPTION_ADDON.getPropertyName())) &&
                            updateData.getInteger(DURATION_ADDON.getPropertyName()).equals(existingAddon.getInteger(DURATION_ADDON.getPropertyName())) &&
                            updateData.getDouble(AMOUNT_ADDON.getPropertyName()).equals(existingAddon.getDouble(AMOUNT_ADDON.getPropertyName()));

            if (isSame) {
                LOGGER.info("No changes detected.");
                return new JsonObject()
                        .put("status", "NO_CHANGE")
                        .put("message", "No changes made. All fields are same as before.");
            }

            updateData.put("lastModifiedBy_Gym_Text", modifiedBy);
            updateData.put("lastModifiedAt_Gym_Long", DateUtils.getCurrentTimeInMillis());

            Document updateDoc = new Document("$set", updateData);
            boolean isUpdated = updateDocument(GYM_ADDONS_COLLECTION, queryFilter, updateDoc, getMongoDbSession(client), database);

            LOGGER.info(isUpdated ? "Addon updated successfully." : "Addon update failed.");
            return new JsonObject().put("status", isUpdated ? "SUCCESS" : "FAILURE");
        } catch (Exception e) {
            LOGGER.error("Error updating addon: {}", e.getMessage(), e);
            return new JsonObject().put("status", "FAILURE");
        }
    }

    public JsonObject updateStatusAddon(String addonId, boolean newStatus, String modifiedBy) {
        if (!ObjectId.isValid(addonId)) {
            return new JsonObject().put("status", "NOT_FOUND");
        }

        try {
            Bson queryFilter = Filters.eq("_id", new ObjectId(addonId));
            Document existingAddon = findSingleDocument(database, GYM_ADDONS_COLLECTION, queryFilter);

            if (existingAddon == null) {
                LOGGER.info("Addon not found.");
                return new JsonObject().put("status", "NOT_FOUND");
            }

            Boolean currentStatus = existingAddon.getBoolean("addon_Status_Bool");

            if (currentStatus != null && currentStatus.equals(newStatus)) {
                LOGGER.info("No change in status.");
                return new JsonObject()
                        .put("status", "NO_CHANGE")
                        .put("message", "Addon status is already " + newStatus);
            }

            Document updateDoc = new Document("$set", new Document("addon_Status_Bool", newStatus)
                    .append("lastModifiedBy_Gym_Text", modifiedBy)
                    .append("lastModifiedAt_Gym_Long", DateUtils.getCurrentTimeInMillis()));

            boolean isUpdated = updateDocument(GYM_ADDONS_COLLECTION, queryFilter, updateDoc, getMongoDbSession(client), database);

            LOGGER.info(isUpdated ? "Addon status updated successfully." : "Addon status update failed.");
            return new JsonObject().put("status", isUpdated ? "SUCCESS" : "FAILURE");
        } catch (Exception e) {
            LOGGER.error("Error updating addon status: {}", e.getMessage(), e);
            return new JsonObject().put("status", "FAILURE");
        }
    }


    // Delete addon
    public JsonObject deleteAddonById(String addonId) {
        if (!ObjectId.isValid(addonId)) return new JsonObject().put("status", "NOT_FOUND");

        try {
            Bson queryFilter = Filters.eq("_id", new ObjectId(addonId));
            if (!documentExists(queryFilter)) {
                LOGGER.info("Addon not found.");
                return new JsonObject().put("status", "NOT_FOUND");
            }

            boolean isDeleted = deleteDocument(GYM_ADDONS_COLLECTION, queryFilter, getMongoDbSession(client), database);
            LOGGER.info(isDeleted ? "Addon deleted successfully." : "Addon deletion failed.");

            return new JsonObject().put("status", isDeleted ? "SUCCESS" : "FAILURE");
        } catch (Exception e) {
            LOGGER.error("Error deleting addon: {}", e.getMessage());
            return new JsonObject().put("status", "FAILURE");
        }
    }

    // Export addons
    public List<Document> getAddonsForExport(int page, int size, String memberType) {
        try {
            FindIterable<Document> addons;

            if (memberType != null) {
                Bson filter = Filters.eq(MEMBER_TYPE.getPropertyName(), memberType.toUpperCase());
                addons = findDocuments(database, GYM_ADDONS_COLLECTION, filter, new Document())
                        .skip((page - 1) * size)
                        .limit(size);
            } else {
                addons = findDocuments(database, GYM_ADDONS_COLLECTION, new Document(), new Document())
                        .skip((page - 1) * size)
                        .limit(size);
            }

            List<Document> addonList = new ArrayList<>();
            for (Document addon : addons) {
                ResponseUtil.processResponseDocument(addon);
                addonList.add(addon);
            }

            return addonList;
        } catch (Exception e) {
            LOGGER.error("Error retrieving addons for export: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    // Normalize case to UPPER for consistency
    private void normalizeAddonFields(Document data) {
        if (data.containsKey(MEMBER_TYPE.getPropertyName())) {
            data.put(MEMBER_TYPE.getPropertyName(),
                    data.getString(MEMBER_TYPE.getPropertyName()).toUpperCase());
        }

        if (data.containsKey(ADDON_NAME.getPropertyName())) {
            data.put(ADDON_NAME.getPropertyName(),
                    data.getString(ADDON_NAME.getPropertyName()).toUpperCase());
        }

        if (data.containsKey(DESCRIPTION_ADDON.getPropertyName())) {
            data.put(DESCRIPTION_ADDON.getPropertyName(),
                    data.getString(DESCRIPTION_ADDON.getPropertyName()).toUpperCase());
        }
    }

    private boolean documentExists(Bson queryFilter) {
        return findSingleDocument(database, GYM_ADDONS_COLLECTION, queryFilter) != null;
    }
}
