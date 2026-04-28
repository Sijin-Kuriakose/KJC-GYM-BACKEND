package in.edu.kristujayanti.handlers;

import in.edu.kristujayanti.enums.ResponseType;
import in.edu.kristujayanti.enums.StatusCode;
import in.edu.kristujayanti.services.GymAttendanceManager;
import in.edu.kristujayanti.util.ResponseUtil;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static in.edu.kristujayanti.propertyBinder.Gym.GymKeysPBinder.*;

public class AttendanceHandler implements Handler<RoutingContext> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AttendanceHandler.class);
    private final GymAttendanceManager attendanceManager;

    public AttendanceHandler(GymAttendanceManager attendanceManager) {
        this.attendanceManager = attendanceManager;
    }

    @Override
    public void handle(RoutingContext routingContext) {
        HttpServerResponse response = routingContext.response();
        response.putHeader("Content-Type", "application/json");

        JsonObject requestBody = routingContext.getBodyAsJson();
        if (requestBody == null || !requestBody.containsKey(USER_ID.getPropertyName())) {
            ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.BAD_REQUEST, new JsonArray(),
                    new JsonArray().add("User ID is required."));
            return;
        }

        String userId = requestBody.getString(USER_ID.getPropertyName());
        if (userId == null || userId.isEmpty()) {
            ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.BAD_REQUEST, new JsonArray(),
                    new JsonArray().add("User ID cannot be empty."));
            return;
        }

        try {
            JsonObject wrappedRequestBody = new JsonObject().put(USER_ID.getPropertyName(), userId);
            JsonObject result = attendanceManager.handleAttendance(wrappedRequestBody);
            String status = result.getString("status");
            String message = result.getString("message");

            switch (status) {
                case "CHECK_IN_SUCCESS":
                    JsonObject checkInData = new JsonObject()
                            .put(NAME.getPropertyName(), result.getString(NAME.getPropertyName()))
                            .put(USER_TYPE.getPropertyName(), result.getString(USER_TYPE.getPropertyName()))
                            .put(USER_ID.getPropertyName(), result.getString(USER_ID.getPropertyName()))
                            .put(CHECK_IN.getPropertyName(), result.getString(CHECK_IN.getPropertyName()))
                            .put("subscriptionName", result.getString("subscriptionName"))
                            .put("subscriptionEndDate", result.getString("subscriptionEndDate"))
                            .put("addonDetails", result.getJsonArray("addonDetails"));
                    ResponseUtil.createResponse(response, ResponseType.SUCCESS, StatusCode.TWOHUNDRED, checkInData,
                            new JsonArray().add(message));
                    break;
                case "CHECK_OUT_SUCCESS":
                    JsonObject checkOutData = new JsonObject()
                            .put(NAME.getPropertyName(), result.getString(NAME.getPropertyName()))
                            .put(USER_TYPE.getPropertyName(), result.getString(USER_TYPE.getPropertyName()))
                            .put(USER_ID.getPropertyName(), result.getString(USER_ID.getPropertyName()))
                            .put(CHECK_IN.getPropertyName(), result.getString(CHECK_IN.getPropertyName()))
                            .put(CHECK_OUT.getPropertyName(), result.getString(CHECK_OUT.getPropertyName()))
                            .put("subscriptionName", result.getString("subscriptionName"))
                            .put("subscriptionEndDate", result.getString("subscriptionEndDate"))
                            .put("addonDetails", result.getJsonArray("addonDetails"));
                    ResponseUtil.createResponse(response, ResponseType.SUCCESS, StatusCode.TWOHUNDRED, checkOutData,
                            new JsonArray().add(message));
                    break;
                case "NO_SUBSCRIPTION":
                    ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.FORBIDDEN, new JsonArray(),
                            new JsonArray().add(message));
                    break;
                case "NOT_FOUND":
                    ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.FILE_NOT_FOUND, new JsonArray(),
                            new JsonArray().add(message));
                    break;
                case "ALREADY_CHECKED_OUT":
                    ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.BAD_REQUEST, new JsonArray(),
                            new JsonArray().add(message));
                    break;
                case "FAILURE":
                    ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.INTERNAL_SERVER_ERROR, new JsonArray(),
                            new JsonArray().add(message));
                    break;
                default:
                    ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.INTERNAL_SERVER_ERROR, new JsonArray(),
                            new JsonArray().add("Unexpected error occurred."));
                    break;
            }
        } catch (Exception e) {
            LOGGER.error("Error in AttendanceHandler for userId {}: {}", userId, e.getMessage(), e);
            ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.INTERNAL_SERVER_ERROR, new JsonArray(),
                    new JsonArray().add("Internal server error."));
        }
    }
}