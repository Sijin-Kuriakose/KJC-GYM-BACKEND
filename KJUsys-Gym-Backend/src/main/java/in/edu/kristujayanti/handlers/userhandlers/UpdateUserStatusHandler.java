package in.edu.kristujayanti.handlers.userhandlers;

import in.edu.kristujayanti.services.UserServices.SubscriptionService;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static in.edu.kristujayanti.propertyBinder.Gym.GymKeysPBinder.*; // Static import for property binder

public class UpdateUserStatusHandler implements Handler<RoutingContext> {
    private static final Logger LOGGER = LogManager.getLogger(UpdateUserStatusHandler.class);
    private final SubscriptionService subscriptionService;

    public UpdateUserStatusHandler(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @Override
    public void handle(RoutingContext routingContext) {
        try {
            JsonObject requestBody = routingContext.getBodyAsJson();
            if (requestBody == null) {
                LOGGER.warn("Request body is missing or invalid");
                sendErrorResponse(routingContext, 400, "Request body is missing or invalid");
                return;
            }

            String emailId = requestBody.getString(EMAIL_ID.getPropertyName()); // Use EMAIL_ID from GymKeysPBinder
            String statusText = requestBody.getString(STATUS.getPropertyName()); // Use STATUS from GymKeysPBinder

            if (emailId == null || emailId.trim().isEmpty()) {
                LOGGER.warn("Email ID is missing or empty in request");
                sendErrorResponse(routingContext, 400, "Email ID is required");
                return;
            }

            if (statusText == null || statusText.trim().isEmpty()) {
                LOGGER.warn("Status text is missing or empty in request for emailID: {}", emailId);
                sendErrorResponse(routingContext, 400, "Status text is required");
                return;
            }

            LOGGER.info("Processing status update request for emailID: {} with status: {}", emailId, statusText);

            subscriptionService.updateUserStatus(emailId, statusText)
                    .onSuccess(v -> {
                        JsonObject response = new JsonObject()
                                .put("message", "User status updated successfully");
                        // Reflect the actual status change in the response
                        if ("Inactive".equalsIgnoreCase(statusText.trim())) {
                            response.put(STATUS.getPropertyName(), false);
                        } else if ("Active".equalsIgnoreCase(statusText.trim())) {
                            response.put(STATUS.getPropertyName(), true);
                        } else {
                            response.put(STATUS.getPropertyName(), "No change");
                            response.put("message", "User status unchanged: Invalid status value provided");
                        }
                        routingContext.response()
                                .setStatusCode(200)
                                .putHeader("Content-Type", "application/json")
                                .end(response.encode());
                        LOGGER.info("Status update processed for emailID: {}. Requested status: {}", emailId, statusText);
                    })
                    .onFailure(throwable -> {
                        LOGGER.error("Failed to update status for emailID {}: {}", emailId, throwable.getMessage(), throwable);
                        sendErrorResponse(routingContext, 500, "Failed to update user status: " + throwable.getMessage());
                    });

        } catch (Exception e) {
            LOGGER.error("Unexpected error processing status update request: {}", e.getMessage(), e);
            sendErrorResponse(routingContext, 500, "Internal server error: " + e.getMessage());
        }
    }

    private void sendErrorResponse(RoutingContext routingContext, int statusCode, String message) {
        JsonObject errorResponse = new JsonObject()
                .put("error", message);
        routingContext.response()
                .setStatusCode(statusCode)
                .putHeader("Content-Type", "application/json")
                .end(errorResponse.encode());
    }
}