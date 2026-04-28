package in.edu.kristujayanti.handlers.userhandlers;

import in.edu.kristujayanti.enums.ResponseType;
import in.edu.kristujayanti.enums.StatusCode;
import in.edu.kristujayanti.services.UserServices.UserService;
import in.edu.kristujayanti.util.DocumentParser;
import in.edu.kristujayanti.util.ResponseUtil;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import java.util.Arrays;
import java.util.List;

import static in.edu.kristujayanti.propertyBinder.Gym.GymKeysPBinder.*;

public class UpdateUserHandler {
    private static final Logger LOGGER = LogManager.getLogger(UpdateUserHandler.class);
    private final UserService userService;

    public UpdateUserHandler(UserService userService) {
        this.userService = userService;
    }

    public void updateUserDetails(RoutingContext routingContext) {
        try {
            JsonObject requestBody = routingContext.body().asJsonObject();
            if (requestBody == null) {
                LOGGER.warn("Request body is empty");
                ResponseUtil.createResponse(
                        routingContext.response(),
                        ResponseType.VALIDATION,
                        StatusCode.BAD_REQUEST,
                        new JsonArray(),
                        new JsonArray().add("Request body is empty")
                );
                return;
            }

            LOGGER.debug("Received update request: {}", requestBody);
            Document updateDetails = DocumentParser.parseToDocument(requestBody);
            String emailId = requestBody.getString(EMAIL_ID.getPropertyName());
            String newName = requestBody.getString(NAME.getPropertyName());
            Long newContactNumber = requestBody.getLong(CONTACT_NUMBER.getPropertyName());
            List<String> mandatoryFields = Arrays.asList(
                    EMAIL_ID.getPropertyName(),
                    NAME.getPropertyName(),
                    CONTACT_NUMBER.getPropertyName()
            );

            JsonArray validationErrors = DocumentParser.validateAndCleanDocument(updateDetails, mandatoryFields);
            if (!validationErrors.isEmpty()) {
                LOGGER.warn("Validation failed for update fields in request: {}. Errors: {}", requestBody, validationErrors);
                ResponseUtil.createResponse(
                        routingContext.response(),
                        ResponseType.VALIDATION,
                        StatusCode.BAD_REQUEST,
                        new JsonArray(),
                        validationErrors
                );
                return;
            }

            LOGGER.info("Updating member details for emailID: {}", emailId);
            userService.updateUserDetails(emailId, newName, newContactNumber)
                    .onSuccess(updatedDetails -> {
                        LOGGER.info("Member details updated successfully for emailID: {}", emailId);
                        ResponseUtil.createResponse(
                                routingContext.response(),
                                ResponseType.SUCCESS,
                                StatusCode.TWOHUNDRED,
                                new JsonArray().add(updatedDetails),
                                new JsonArray().add("Member details updated successfully!")
                        );
                    })
                    .onFailure(err -> {
                        LOGGER.error("Failed to update member details for emailID {}: {}", emailId, err.getMessage(), err);
                        ResponseUtil.createResponse(
                                routingContext.response(),
                                ResponseType.ERROR,
                                StatusCode.BAD_REQUEST,
                                new JsonArray(),
                                new JsonArray().add(
                                        new JsonObject()
                                                .put("message", "Failed to update member details")
                                                .put("details", err.getMessage())
                                )
                        );
                    });
        } catch (Exception e) {
            LOGGER.error("Unexpected error while processing update request: {}", e.getMessage(), e);
            ResponseUtil.createResponse(
                    routingContext.response(),
                    ResponseType.ERROR,
                    StatusCode.INTERNAL_SERVER_ERROR,
                    new JsonArray(),
                    new JsonArray().add(
                            new JsonObject()
                                    .put("message", "Unexpected error while updating member")
                                    .put("details", e.getMessage())
                    )
            );
        }
    }
}