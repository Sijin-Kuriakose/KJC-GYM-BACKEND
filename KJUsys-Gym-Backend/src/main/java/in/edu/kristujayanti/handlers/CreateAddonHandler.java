package in.edu.kristujayanti.handlers;

import in.edu.kristujayanti.enums.ResponseType;
import in.edu.kristujayanti.enums.StatusCode;
import in.edu.kristujayanti.services.AddonService;

import in.edu.kristujayanti.util.DocumentParser;
import in.edu.kristujayanti.util.ResponseUtil;
import in.edu.kristujayanti.util.DateUtils;
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


public class CreateAddonHandler implements Handler<RoutingContext> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateAddonHandler.class);
    private final AddonService addonService;

    public CreateAddonHandler(AddonService addonService) {

        this.addonService = addonService;
    }

    @Override
    public void handle(RoutingContext context) {
        HttpServerResponse response = context.response();
        response.putHeader("Content-Type", "application/json");

        JsonObject requestBody = context.getBodyAsJson();
        if (requestBody == null) {
            ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.BAD_REQUEST, new JsonArray(),
                    new JsonArray().add("Invalid or missing JSON body"));
            return;
        }

        Document addonDetails = Document.parse(requestBody.toString());

        // Required fields (excluding addon_Status_Bool, since it's set internally)
        List<String> requiredFields = Arrays.asList(
                MEMBER_TYPE.getPropertyName(),
                ADDON_NAME.getPropertyName(),
                DESCRIPTION_ADDON.getPropertyName(),
                DURATION_ADDON.getPropertyName(),
                AMOUNT_ADDON.getPropertyName()
        );

        JsonArray validationResponse = DocumentParser.validateAndCleanDocument(addonDetails, requiredFields);
        if (!validationResponse.isEmpty()) {
            ResponseUtil.createResponse(response, ResponseType.VALIDATION, StatusCode.TWOHUNDRED, new JsonArray(), validationResponse);
            return;
        }

        String memberType = addonDetails.getString(MEMBER_TYPE.getPropertyName());
        if ((!memberType.equalsIgnoreCase("student") && !memberType.equalsIgnoreCase("staff"))) {
            ResponseUtil.createResponse(response, ResponseType.VALIDATION, StatusCode.TWOHUNDRED, new JsonArray(),
                    new JsonArray().add("Member type must be either 'student' or 'staff'"));
            return;
        }

        addonDetails.put(MEMBER_TYPE.getPropertyName(), memberType.toUpperCase());

        // Set internal fields
        String loggedInUserInfo = UserInfoUtil.getLoggedInUserId(context.request().headers());
        long createdAt = DateUtils.getCurrentTimeInMillis();

        addonDetails.put("createdBy_Gym_Text", loggedInUserInfo);
        addonDetails.put("createdAt_Gym_Long", createdAt);

        // ✅ Automatically set addon status to true
        addonDetails.put(ADDON_STATUS.getPropertyName(), true);

        try {
            JsonObject insertResult = addonService.createAddon(addonDetails);
            LOGGER.info("Insert result: {}", insertResult.toString());

            String status = insertResult.getString("status");

            switch (status) {
                case "SUCCESS":
                    ResponseUtil.createResponse(response, ResponseType.SUCCESS, StatusCode.TWOHUNDRED, new JsonArray(),
                            new JsonArray().add("Addon created successfully"));
                    break;
                case "DUPLICATE":
                    ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.CONFLICT, new JsonArray(),
                            new JsonArray().add("Addon with the same name and member type already exists"));
                    break;
                case "FAILURE":
                    ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.INTERNAL_SERVER_ERROR, new JsonArray(),
                            new JsonArray().add("Failed to create addon"));
                    break;
                default:
                    ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.INTERNAL_SERVER_ERROR, new JsonArray(),
                            new JsonArray().add("Unexpected error occurred"));
                    break;
            }
        } catch (Exception e) {
            LOGGER.error("Error in CreateAddonsHandler: {}", e.getMessage(), e);
            ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.INTERNAL_SERVER_ERROR, new JsonArray(),
                    new JsonArray().add("Internal server error while creating addon"));
        }
    }
}
