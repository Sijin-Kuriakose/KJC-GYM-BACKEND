package in.edu.kristujayanti.handlers.userhandlers;

import in.edu.kristujayanti.enums.ResponseType;
import in.edu.kristujayanti.enums.StatusCode;
import in.edu.kristujayanti.services.UserServices.SubscriptionService;
import in.edu.kristujayanti.util.ResponseUtil;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import static in.edu.kristujayanti.propertyBinder.Gym.GymKeysPBinder.*;

/**
 * SubscriptionHandler manages subscription-related HTTP requests, routing them to the SubscriptionService.
 */
public class SubscriptionHandler {
    private static final Logger LOGGER = LogManager.getLogger(SubscriptionHandler.class);
    private final SubscriptionService subscriptionService;

    public SubscriptionHandler(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    /**
     * Handles POST requests to add a subscription (service and/or add-ons).
     * Expects query param 'emailId' and a JSON body with 'serviceOid', 'preferredStartDate', and optional 'subscriptionAddOnList'.
     */
    public void handleSubscriptionRequest(RoutingContext routingContext) {
        try {
            String emailId = (routingContext.request().getParam("emailId")).toUpperCase();
            JsonObject requestBody = routingContext.getBodyAsJson();

            // Validate inputs
            if (emailId == null || emailId.trim().isEmpty()) {
                LOGGER.warn("Missing or empty emailId parameter");
                ResponseUtil.createResponse(
                        routingContext.response(),
                        ResponseType.VALIDATION,
                        StatusCode.BAD_REQUEST,
                        new JsonArray(),
                        new JsonArray().add("Email ID is required")
                );
                return;
            }

            if (requestBody == null || requestBody.isEmpty()) {
                LOGGER.warn("Missing or empty request body for emailID: {}", emailId);
                ResponseUtil.createResponse(
                        routingContext.response(),
                        ResponseType.VALIDATION,
                        StatusCode.BAD_REQUEST,
                        new JsonArray(),
                        new JsonArray().add("Request body is required")
                );
                return;
            }

            LOGGER.debug("Processing subscription request for emailID: {}", emailId);
            Future<Document> future = subscriptionService.addSubscription(emailId, requestBody);

            future.onSuccess(memberDetails -> {
                LOGGER.info("Subscription processed successfully for emailID: {}", emailId);
                ResponseUtil.createResponse(
                        routingContext.response(),
                        ResponseType.SUCCESS,
                        StatusCode.TWOHUNDRED,
                        new JsonArray().add(memberDetails),
                        new JsonArray().add("Subscription processed successfully")
                );
            }).onFailure(error -> {
                LOGGER.error("Failed to process subscription for emailID {}: {}", emailId, error.getMessage());
                StatusCode statusCode = determineStatusCode(error.getMessage());
                ResponseType responseType = statusCode == StatusCode.INTERNAL_SERVER_ERROR ? ResponseType.ERROR : ResponseType.VALIDATION;
                ResponseUtil.createResponse(
                        routingContext.response(),
                        responseType,
                        statusCode,
                        new JsonArray(),
                        new JsonArray().add(
                                new JsonObject()
                                        .put("message", "Failed to process subscription")
                                        .put("details", error.getMessage())
                        )
                );
            });

        } catch (Exception e) {
            LOGGER.error("Unexpected error processing subscription for emailID: {}", routingContext.request().getParam("emailId"), e);
            ResponseUtil.createResponse(
                    routingContext.response(),
                    ResponseType.ERROR,
                    StatusCode.INTERNAL_SERVER_ERROR,
                    new JsonArray(),
                    new JsonArray().add(
                            new JsonObject()
                                    .put("message", "Unexpected error processing subscription")
                                    .put("details", e.getMessage())
                    )
            );
        }
    }

    /**
     * Handles PUT requests to update user status.
     * Expects query params 'emailId' and 'status' (Active/Inactive).
     */
    public void updateUserStatus(RoutingContext routingContext) {
        try {
            String emailId = routingContext.request().getParam("emailId");
            String statusText = routingContext.request().getParam("status");

            // Validate inputs
            if (emailId == null || emailId.trim().isEmpty()) {
                LOGGER.warn("Missing or empty emailId parameter");
                ResponseUtil.createResponse(
                        routingContext.response(),
                        ResponseType.VALIDATION,
                        StatusCode.BAD_REQUEST,
                        new JsonArray(),
                        new JsonArray().add("Email ID is required")
                );
                return;
            }

            if (statusText == null || statusText.trim().isEmpty()) {
                LOGGER.warn("Missing or empty status parameter for emailID: {}", emailId);
                ResponseUtil.createResponse(
                        routingContext.response(),
                        ResponseType.VALIDATION,
                        StatusCode.BAD_REQUEST,
                        new JsonArray(),
                        new JsonArray().add("Status is required")
                );
                return;
            }

            LOGGER.debug("Updating status to '{}' for emailID: {}", statusText, emailId);

            Future<Void> future = subscriptionService.updateUserStatus(emailId, statusText);

            future.onSuccess(v -> {
                LOGGER.info("Status updated successfully for emailID: {}", emailId);
                ResponseUtil.createResponse(
                        routingContext.response(),
                        ResponseType.SUCCESS,
                        StatusCode.TWOHUNDRED,
                        new JsonArray(),
                        new JsonArray().add("User status updated successfully")
                );
            }).onFailure(error -> {
                LOGGER.error("Failed to update status for emailID {}: {}", emailId, error.getMessage());
                StatusCode statusCode = determineStatusCode(error.getMessage());
                ResponseType responseType = statusCode == StatusCode.INTERNAL_SERVER_ERROR ? ResponseType.ERROR : ResponseType.VALIDATION;
                ResponseUtil.createResponse(
                        routingContext.response(),
                        responseType,
                        statusCode,
                        new JsonArray(),
                        new JsonArray().add(
                                new JsonObject()
                                        .put("message", "Failed to update user status")
                                        .put("details", error.getMessage())
                        )
                );
            });

        } catch (Exception e) {
            LOGGER.error("Unexpected error updating status for emailID: {}", routingContext.request().getParam("emailId"), e);
            ResponseUtil.createResponse(
                    routingContext.response(),
                    ResponseType.ERROR,
                    StatusCode.INTERNAL_SERVER_ERROR,
                    new JsonArray(),
                    new JsonArray().add(
                            new JsonObject()
                                    .put("message", "Unexpected error updating user status")
                                    .put("details", e.getMessage())
                    )
            );
        }
    }

    /**
     * Handles GET requests to retrieve subscription details.
     * Expects query param 'emailId'.
     */
    public void getCurrentSubscriptionDetails(RoutingContext routingContext) {
        try {
            String emailId = routingContext.request().getParam("emailId");

            // Validate inputs
            if (emailId == null || emailId.trim().isEmpty()) {
                LOGGER.warn("Missing or empty emailId parameter");
                ResponseUtil.createResponse(
                        routingContext.response(),
                        ResponseType.VALIDATION,
                        StatusCode.BAD_REQUEST,
                        new JsonArray(),
                        new JsonArray().add("Email ID is required")
                );
                return;
            }

            LOGGER.debug("Retrieving subscription details for emailID: {}", emailId);
            Future<JsonObject> future = subscriptionService.getCurrentSubscriptionDetails(emailId);

            future.onSuccess(result -> {
                LOGGER.info("Subscription details retrieved successfully for emailID: {}", emailId);
                ResponseUtil.createResponse(
                        routingContext.response(),
                        ResponseType.SUCCESS,
                        StatusCode.TWOHUNDRED,
                        new JsonArray().add(result),
                        new JsonArray().add("Subscription details retrieved successfully")
                );
            }).onFailure(error -> {
                LOGGER.error("Failed to retrieve subscription details for emailID {}: {}", emailId, error.getMessage());
                StatusCode statusCode = determineStatusCode(error.getMessage());
                ResponseType responseType = statusCode == StatusCode.INTERNAL_SERVER_ERROR ? ResponseType.ERROR : ResponseType.VALIDATION;
                ResponseUtil.createResponse(
                        routingContext.response(),
                        responseType,
                        statusCode,
                        new JsonArray(),
                        new JsonArray().add(
                                new JsonObject()
                                        .put("message", "Failed to retrieve subscription details")
                                        .put("details", error.getMessage())
                        )
                );
            });

        } catch (Exception e) {
            LOGGER.error("Unexpected error retrieving subscription details for emailID: {}", routingContext.request().getParam("emailId"), e);
            ResponseUtil.createResponse(
                    routingContext.response(),
                    ResponseType.ERROR,
                    StatusCode.INTERNAL_SERVER_ERROR,
                    new JsonArray(),
                    new JsonArray().add(
                            new JsonObject()
                                    .put("message", "Unexpected error retrieving subscription details")
                                    .put("details", e.getMessage())
                    )
            );
        }
    }

    /**
     * Determines the appropriate status code based on the error message.
     */
    private StatusCode determineStatusCode(String errorMessage) {
        if (errorMessage.contains("does not exist") || errorMessage.contains("No active or future subscription found")) {
            return StatusCode.FILE_NOT_FOUND;
        } else if (errorMessage.contains("MongoDB error")) {
            return StatusCode.INTERNAL_SERVER_ERROR;
        }
        return StatusCode.BAD_REQUEST;
    }
}