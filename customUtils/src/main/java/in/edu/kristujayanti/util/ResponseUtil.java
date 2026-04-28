package in.edu.kristujayanti.util;

import in.edu.kristujayanti.enums.ResponseCode;
import in.edu.kristujayanti.enums.ResponseType;
import in.edu.kristujayanti.enums.StatusCode;
import in.edu.kristujayanti.propertyBinder.HR.WorkShiftKeysPBinder;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.bson.Document;
import org.bson.types.ObjectId;
import software.amazon.awssdk.services.ses.endpoints.internal.Value;

import java.util.List;
import java.util.function.Function;

public final class ResponseUtil {
    private ResponseUtil() {
    }

    public static void createResponse(HttpServerResponse httpServerResponse, ResponseType responseType, StatusCode statusCode, JsonArray data, JsonArray message) {
        JsonObject response = new JsonObject();
        response.put(ResponseCode.STATUS_CODE.getProperty(), statusCode.getStatusCode());
        response.put(ResponseCode.TYPE.getProperty(), responseType);
        JsonObject responseDataJsonObject = new JsonObject();
        responseDataJsonObject.put(ResponseCode.DATA.getProperty(), data);
        responseDataJsonObject.put(ResponseCode.MESSAGE.getProperty(), message);
        response.put(ResponseCode.RESPONSE_DATA.getProperty(), responseDataJsonObject);
        httpServerResponse
                .putHeader("content-type", "application/json")
                .end(response.encodePrettily());
    }

    public static void createResponse(HttpServerResponse httpServerResponse, ResponseType responseType, StatusCode statusCode, JsonObject data, JsonArray message) {
        JsonObject response = new JsonObject();
        response.put(ResponseCode.STATUS_CODE.getProperty(), statusCode.getStatusCode());
        response.put(ResponseCode.TYPE.getProperty(), responseType);
        JsonObject responseDataJsonObject = new JsonObject();
        responseDataJsonObject.put(ResponseCode.DATA.getProperty(), data);
        responseDataJsonObject.put(ResponseCode.MESSAGE.getProperty(), message);
        response.put(ResponseCode.RESPONSE_DATA.getProperty(), responseDataJsonObject);
        httpServerResponse
                .putHeader("content-type", "application/json")
                .end(response.encodePrettily());
    }

    public static void createResponse(HttpServerResponse httpServerResponse, ResponseType responseType, Integer statusCode, JsonArray data, JsonArray message) {
        JsonObject response = new JsonObject();
        response.put(ResponseCode.STATUS_CODE.getProperty(), StatusCode.validateStatusCode(statusCode));
        response.put(ResponseCode.TYPE.getProperty(), responseType);
        JsonObject responseDataJsonObject = new JsonObject();
        responseDataJsonObject.put(ResponseCode.DATA.getProperty(), data);
        responseDataJsonObject.put(ResponseCode.MESSAGE.getProperty(), message);
        response.put(ResponseCode.RESPONSE_DATA.getProperty(), responseDataJsonObject);
        httpServerResponse
                .putHeader("content-type", "application/json")
                .end(response.encodePrettily());
    }

    /**
     * Processes the given MongoDB Document by identifying specific key patterns and updating their values accordingly.
     *
     * The method performs the following operations:
     * - Converts fields with suffixes "_Time", "_Date", and "_DateTime" from millisecond values to formatted strings.
     * - Converts fields with suffix "_ObjectId" or "_id" from ObjectId to String representation.
     * - Converts fields with suffix "_ObjectIdArray" from a list of ObjectIds to a list of Strings.
     * - Recursively processes nested documents for fields with suffix "_Document".
     * - Recursively processes arrays of nested documents for fields with suffix "_DocumentArray".
     *
     * @param doc the MongoDB Document to process. This document is modified in-place.
     */
    public static void processResponseDocument(Document doc) {
        // Handle keys ending with "_Time"
        updateKeys(doc, "_Time", Long.class, DateUtils::convertTimeMillisToTimeString);

        // Handle keys ending with "_Date"
        updateKeys(doc, "_Date", Long.class, DateUtils::convertMillisToDateString);

        // Handle keys ending with "_DateTime"
        updateKeys(doc, "_DateTime", Long.class, DateUtils::convertMillisToDateTimeStringAsia);

        // Handle ObjectId conversion for "_ObjectId" and "_id"
        updateKeys(doc, "_ObjectId", ObjectId.class, ObjectIdUtil::convertObjectIdToString);
        updateKeys(doc, "_id", ObjectId.class, ObjectIdUtil::convertObjectIdToString);

        // Handle ObjectId arrays
        doc.keySet().stream()
                .filter(key -> key.endsWith("_ObjectIdArray"))
                .forEach(key -> {
                    List<ObjectId> objectIdList = doc.getList(key, ObjectId.class);
                    if (objectIdList != null) {
                        doc.put(key, ObjectIdUtil.convertObjectIdListToStringList(objectIdList));
                    }
                });

        // Handle nested documents
        doc.keySet().stream()
                .filter(key -> key.endsWith("_Document"))
                .forEach(key -> {
                    Document nestedDoc = doc.get(key, Document.class);
                    if (nestedDoc != null) {
                        processResponseDocument(nestedDoc);
                    }
                });

        // Handle arrays of nested documents
        doc.keySet().stream()
                .filter(key -> key.endsWith("_DocumentArray"))
                .forEach(key -> {
                    List<Document> nestedDocs = doc.getList(key, Document.class);
                    if (nestedDocs != null) {
                        nestedDocs.forEach(ResponseUtil::processResponseDocument);
                    }
                });
    }

    /**
     * Updates all keys in the given MongoDB Document that match the specified suffix by applying a conversion function
     * to their values.
     *
     * This helper method filters keys in the document that end with the given suffix, retrieves their values of the
     * specified type, and updates the key with the result of the conversion function. Only non-null values are processed.
     *
     * @param <T>       the type of the values to be retrieved and converted.
     * @param doc       the MongoDB Document containing the keys to update. This document is modified in-place.
     * @param suffix    the suffix to match against the keys in the document.
     * @param type      the expected type of the key's value to process.
     * @param converter the conversion function to apply to the key's value.
     */
    private static <T> void updateKeys(Document doc, String suffix, Class<T> type, Function<T, Object> converter) {
        doc.keySet().stream()
                .filter(key -> key.endsWith(suffix))
                .forEach(key -> {
                    T value = doc.get(key, type);
                    if (value != null) {
                        doc.put(key, converter.apply(value));
                    }
                });
    }

}