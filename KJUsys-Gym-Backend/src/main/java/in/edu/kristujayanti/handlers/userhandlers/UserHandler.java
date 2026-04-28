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
public class UserHandler {
    private static final Logger LOGGER = LogManager.getLogger(UserHandler.class);
    private final UserService userService;

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public void addMembership(RoutingContext routingContext) {
        try{
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

            LOGGER.debug("Received registration request: {}", requestBody);
            Document registerDetails = DocumentParser.parseToDocument(requestBody);
            String name = requestBody.getString(NAME.getPropertyName());
            String userid = requestBody.getString(USER_ID.getPropertyName());
            String usertype = requestBody.getString(USER_TYPE.getPropertyName());
            String contactInfo = requestBody.getString(CONTACT_NUMBER.getPropertyName());
            String emailid = requestBody.getString(EMAIL_ID.getPropertyName());

            List<String> topLevelMandatoryFields = Arrays.asList(
                    USER_ID.getPropertyName(),
                    NAME.getPropertyName(),
                    USER_TYPE.getPropertyName(),
                    CONTACT_NUMBER.getPropertyName(),
                    EMAIL_ID.getPropertyName()
            );


            JsonArray topLevelValidationErrors = new JsonArray();

            for (String field : topLevelMandatoryFields) {
                Object value = requestBody.getValue(field);
                if (value == null || value.toString().trim().isEmpty()) {
                    topLevelValidationErrors.add(new JsonObject()
                            .put("field", field)
                            .put("error", field + " is missing or empty"));
                    LOGGER.warn("Missing or empty field: {}", field);
                }
            }

            System.out.println(topLevelValidationErrors);
            if (!topLevelValidationErrors.isEmpty()) {
                LOGGER.warn("Validation failed for top-level fields in request: {}. Errors: {}", requestBody, topLevelValidationErrors);
                ResponseUtil.createResponse(
                        routingContext.response(),
                        ResponseType.VALIDATION,
                        StatusCode.BAD_REQUEST,
                        new JsonArray(),
                        topLevelValidationErrors
                );
                return;
            }
//            System.out.println();
//            if (name == null || userid == null || usertype == null || contactInfo == null || emailid == null)
//            {
//                routingContext.response()
//                        .setStatusCode(400)
//                        .putHeader("content-type", "application/json")
//                        .end(new JsonObject().put("error", "Missing required fields").encode());
//
//            }
//            LOGGER.info("Registering member with emailID: {}", );
//            userService.addmembership(name, userid, usertype, contactInfo, emailid)
//                    .onSuccess(result -> {
//                        routingContext.response()
//                                .setStatusCode(201)
//                                .putHeader("content-type", "application/json")
//                                .end(result.encode());
//                    })
//                    .onFailure(error -> {
//                        routingContext.response()
//                                .setStatusCode(500)
//                                .putHeader("content-type", "application/json")
//                                .end(new JsonObject().put("error", error.getMessage()).encode());
//                    });

            LOGGER.info("Registering member with emailID: {}", emailid);
            userService.addmembership(name, userid, usertype, contactInfo, emailid,registerDetails)
                    .onSuccess(memberDetails -> {
                        LOGGER.info("Member added successfully with emailId_Gym_Text: {}", memberDetails.getString(EMAIL_ID.getPropertyName()));
                        ResponseUtil.createResponse(
                                routingContext.response(),
                                ResponseType.SUCCESS,
                                StatusCode.TWOHUNDRED,
                                new JsonArray().add(memberDetails),
                                new JsonArray().add("Member created successfully!")
                        );
                    })
                    .onFailure(err -> {
                        LOGGER.error("Failed to add member with emailID {}: {}", emailid, err.getMessage(), err);
                        ResponseUtil.createResponse(routingContext.response(), ResponseType.ERROR, StatusCode.BAD_REQUEST, new JsonArray(), new JsonArray().add(new JsonObject()
                                                .put("message", "Failed to add member")
                                                .put("details", err.getMessage())
                                )
                        );
                    });
        }catch (Exception e) {
            LOGGER.error("Unexpected error while processing registration request: {}", e.getMessage(), e);
            ResponseUtil.createResponse(
                    routingContext.response(),
                    ResponseType.ERROR,
                    StatusCode.INTERNAL_SERVER_ERROR,
                    new JsonArray(),
                    new JsonArray().add(
                            new JsonObject()
                                    .put("message", "Unexpected error while adding member")
                                    .put("details", e.getMessage())
                    )
            );
        }

    }
}
