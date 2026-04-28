package in.edu.kristujayanti.handlers;

import in.edu.kristujayanti.enums.ResponseType;
import in.edu.kristujayanti.enums.StatusCode;
import in.edu.kristujayanti.services.AddonService;
import in.edu.kristujayanti.util.DocumentParser;
import in.edu.kristujayanti.util.ResponseUtil;
import in.edu.kristujayanti.util.UserInfoUtil;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import static in.edu.kristujayanti.propertyBinder.Gym.GymKeysPBinder.*;

public class UpdateAddonHandler implements Handler<RoutingContext> {
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateAddonHandler.class);
    private final AddonService addonService;

    public UpdateAddonHandler(AddonService addonService) {
        this.addonService = addonService;
    }

    @Override
    public void handle(RoutingContext routingContext) {
        HttpServerResponse response = routingContext.response();
        response.putHeader("Content-Type", "application/json");

        String addonId = routingContext.pathParam("addonId");
        if (addonId == null || addonId.isEmpty()) {
            ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.BAD_REQUEST, new JsonArray(),
                    new JsonArray().add("Addon ID is required."));
            return;
        }

        String statusParam = routingContext.request().getParam("status");

        // Get logged-in user
        String loggedInUserId = UserInfoUtil.getLoggedInUserId(routingContext.request().headers());

        if (statusParam != null) {
            // Handle status update separately
            Boolean statusToUpdate = null;
            if (statusParam.equalsIgnoreCase("true")) {
                statusToUpdate = true;
            } else if (statusParam.equalsIgnoreCase("false")) {
                statusToUpdate = false;
            } else {
                ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.BAD_REQUEST, new JsonArray(),
                        new JsonArray().add("Invalid status value. Use true or false."));
                return;
            }

            try {
                JsonObject result = addonService.updateStatusAddon(addonId, statusToUpdate, loggedInUserId);
                String status = result.getString("status");

                switch (status) {
                    case "SUCCESS":
                        ResponseUtil.createResponse(response, ResponseType.SUCCESS, StatusCode.TWOHUNDRED, new JsonArray(),
                                new JsonArray().add("Addon status updated successfully."));
                        break;
                    case "NO_CHANGE":
                        ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.CONFLICT, new JsonArray(),
                                new JsonArray().add(result.getString("message")));
                        break;
                    case "NOT_FOUND":
                        ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.FILE_NOT_FOUND, new JsonArray(),
                                new JsonArray().add("Addon not found."));
                        break;
                    default:
                        ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.INTERNAL_SERVER_ERROR, new JsonArray(),
                                new JsonArray().add("Failed to update addon status."));
                        break;
                }
            } catch (Exception e) {
                LOGGER.error("Error in status update: {}", e.getMessage(), e);
                ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.INTERNAL_SERVER_ERROR, new JsonArray(),
                        new JsonArray().add("Internal server error."));
            }
        } else {
            // Handle regular addon field updates
            JsonObject requestBody = routingContext.getBodyAsJson();
            if (requestBody == null) {
                ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.BAD_REQUEST, new JsonArray(),
                        new JsonArray().add("No data provided to update."));
                return;
            }

            Document addonDetailsReceived = Document.parse(requestBody.toString());

            List<String> requiredFields = Arrays.asList(
                    MEMBER_TYPE.getPropertyName(),
                    ADDON_NAME.getPropertyName(),
                    DESCRIPTION_ADDON.getPropertyName(),
                    DURATION_ADDON.getPropertyName(),
                    AMOUNT_ADDON.getPropertyName()
            );

            JsonArray validationResponse = DocumentParser.validateAndCleanDocument(addonDetailsReceived, requiredFields);
            if (!validationResponse.isEmpty()) {
                ResponseUtil.createResponse(response, ResponseType.VALIDATION, StatusCode.TWOHUNDRED, new JsonArray(), validationResponse);
                return;
            }

            String memberType = addonDetailsReceived.getString(MEMBER_TYPE.getPropertyName());
            if (memberType == null || (!memberType.equalsIgnoreCase("student") && !memberType.equalsIgnoreCase("staff"))) {
                ResponseUtil.createResponse(response, ResponseType.VALIDATION, StatusCode.TWOHUNDRED, new JsonArray(),
                        new JsonArray().add("Member type must be either 'student' or 'staff'"));
                return;
            }

            addonDetailsReceived.put(MEMBER_TYPE.getPropertyName(), memberType.toUpperCase());

            try {
                JsonObject updateResult = addonService.updateAddon(addonId, addonDetailsReceived, loggedInUserId);
                String status = updateResult.getString("status");

                switch (status) {
                    case "SUCCESS":
                        ResponseUtil.createResponse(response, ResponseType.SUCCESS, StatusCode.TWOHUNDRED, new JsonArray(),
                                new JsonArray().add("Addon updated successfully."));
                        break;
                    case "NOT_FOUND":
                        ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.FILE_NOT_FOUND, new JsonArray(),
                                new JsonArray().add("Addon not found."));
                        break;
                    case "DUPLICATE":
                        ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.CONFLICT, new JsonArray(),
                                new JsonArray().add("An addon with the same name already exists."));
                        break;
                    case "FAILURE":
                        ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.INTERNAL_SERVER_ERROR, new JsonArray(),
                                new JsonArray().add("Failed to update addon."));
                        break;
                    case "NO_CHANGE":
                        ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.CONFLICT, new JsonArray(),
                                new JsonArray().add("No changes made. All fields are the same as before."));
                        break;
                    default:
                        ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.INTERNAL_SERVER_ERROR, new JsonArray(),
                                new JsonArray().add("Unexpected error occurred."));
                        break;
                }
            } catch (Exception e) {
                LOGGER.error("Error in UpdateAddonHandler: {}", e.getMessage(), e);
                ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.INTERNAL_SERVER_ERROR, new JsonArray(),
                        new JsonArray().add("Internal server error."));
            }
        }
    }
}
