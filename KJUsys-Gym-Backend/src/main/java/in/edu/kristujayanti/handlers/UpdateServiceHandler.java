package in.edu.kristujayanti.handlers;

import in.edu.kristujayanti.enums.ResponseType;
import in.edu.kristujayanti.enums.StatusCode;
import in.edu.kristujayanti.services.ServiceManager;
import in.edu.kristujayanti.util.DocumentParser;
import in.edu.kristujayanti.util.ResponseUtil;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import static in.edu.kristujayanti.propertyBinder.Gym.GymKeysPBinder.*;

public class UpdateServiceHandler implements Handler<RoutingContext> {
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateServiceHandler.class);
    private final ServiceManager serviceManager;

    public UpdateServiceHandler(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    @Override
    public void handle(RoutingContext routingContext) {
        HttpServerResponse response = routingContext.response();
        response.putHeader("Content-Type", "application/json");

        String serviceId = routingContext.pathParam("id");
        if (serviceId == null || serviceId.isEmpty()) {
            ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.BAD_REQUEST, new JsonArray(),
                    new JsonArray().add("Service ID is required."));
            return;
        }

        try {
            if (!ObjectId.isValid(serviceId)) {
                ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.BAD_REQUEST, new JsonArray(),
                        new JsonArray().add(new JsonObject().put("error", "Invalid service ID format. It must be a valid MongoDB ObjectId.")));
                return;
            }

            // Check for is_status query parameter first
            String isStatusStr = routingContext.queryParams().get("is_status");
            if (isStatusStr != null) {
                // Handle status update
                boolean isActive;
                try {
                    isActive = Boolean.parseBoolean(isStatusStr.toLowerCase());
                } catch (Exception e) {
                    ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.BAD_REQUEST, new JsonArray(),
                            new JsonArray().add(new JsonObject().put("error", "Invalid is_status value. Must be 'true' or 'false'.")));
                    return;
                }
                JsonObject result = serviceManager.updateServiceStatus(serviceId, isActive);
                String status = result.getString("status");

                switch (status) {
                    case "SUCCESS":
                        ResponseUtil.createResponse(response, ResponseType.SUCCESS, StatusCode.TWOHUNDRED, new JsonArray(),
                                new JsonArray().add(result.getString("message", "Service marked as " + (isActive ? "active" : "inactive") + " successfully.")));
                        break;
                    case "NOT_FOUND":
                        ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.FILE_NOT_FOUND, new JsonArray(),
                                new JsonArray().add(result.getString("message", "Service not found.")));
                        break;
                    case "FAILURE":
                        ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.INTERNAL_SERVER_ERROR, new JsonArray(),
                                new JsonArray().add(result.getString("message", "Failed to mark service as " + (isActive ? "active" : "inactive") + ".")));
                        break;
                    default:
                        ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.INTERNAL_SERVER_ERROR, new JsonArray(),
                                new JsonArray().add("Unexpected error occurred."));
                        break;
                }
                return;
            }

            // Handle general update only if no is_status is present
            if (routingContext.body().isEmpty()) {
                ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.BAD_REQUEST, new JsonArray(),
                        new JsonArray().add("No update data or is_status parameter provided."));
                return;
            }

            JsonObject requestBody = routingContext.getBodyAsJson();
            if (requestBody == null || requestBody.isEmpty()) {
                ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.BAD_REQUEST, new JsonArray(),
                        new JsonArray().add("Invalid or missing JSON body."));
                return;
            }

            // Handle general update
            Document updateServiceDetails = Document.parse(requestBody.toString());

            List<String> updatableFields = Arrays.asList(
                    MEMBER_TYPE.getPropertyName(),
                    SERVICE_NAME.getPropertyName(),
                    //DESCRIPTION_SERVICE.getPropertyName(),
                    DURATION_SERVICE.getPropertyName(),
                    AMOUNT_SERVICE.getPropertyName()
            );

            JsonArray validationResponse = DocumentParser.validateAndCleanDocument(updateServiceDetails, updatableFields);
            if (!validationResponse.isEmpty()) {
                ResponseUtil.createResponse(response, ResponseType.VALIDATION, StatusCode.TWOHUNDRED, new JsonArray(), validationResponse);
                return;
            }

            String memberType = updateServiceDetails.getString(MEMBER_TYPE.getPropertyName());
            if (memberType != null && !memberType.equalsIgnoreCase("student") && !memberType.equalsIgnoreCase("staff")) {
                ResponseUtil.createResponse(response, ResponseType.VALIDATION, StatusCode.TWOHUNDRED, new JsonArray(),
                        new JsonArray().add("Member type must be either 'student' or 'staff'"));
                return;
            }

            JsonObject result = serviceManager.updateService(serviceId, updateServiceDetails, routingContext);
            String status = result.getString("status");

            switch (status) {
                case "SUCCESS":
                    ResponseUtil.createResponse(response, ResponseType.SUCCESS, StatusCode.TWOHUNDRED, new JsonArray(),
                            new JsonArray().add(result.getString("message", "Service updated successfully.")));
                    break;
                case "NOT_FOUND":
                    ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.FILE_NOT_FOUND, new JsonArray(),
                            new JsonArray().add(result.getString("message", "Service not found.")));
                    break;
                case "DUPLICATE":
                    ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.CONFLICT, new JsonArray(),
                            new JsonArray().add("A service with the same name and member type already exists."));
                    break;
                case "FAILURE":
                    ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.INTERNAL_SERVER_ERROR, new JsonArray(),
                            new JsonArray().add(result.getString("message", "Failed to update service.")));
                    break;
                case "INACTIVE":
                    ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.BAD_REQUEST, new JsonArray(),
                            new JsonArray().add("Cannot update an inactive service."));
                    break;
                default:
                    ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.INTERNAL_SERVER_ERROR, new JsonArray(),
                            new JsonArray().add("Unexpected error occurred."));
                    break;
            }
        } catch (Exception e) {
            LOGGER.error("Error in UpdateServiceHandler: {}", e.getMessage(), e);
            ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.INTERNAL_SERVER_ERROR, new JsonArray(),
                    new JsonArray().add("Internal server error."));
        }
    }
}