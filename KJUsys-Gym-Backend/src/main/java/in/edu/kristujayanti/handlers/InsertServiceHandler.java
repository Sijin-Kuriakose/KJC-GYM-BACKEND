package in.edu.kristujayanti.handlers;

import in.edu.kristujayanti.enums.ResponseType;
import in.edu.kristujayanti.enums.StatusCode;
import in.edu.kristujayanti.services.ServiceManager;
import in.edu.kristujayanti.util.DocumentParser;
import in.edu.kristujayanti.util.ResponseUtil;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.bson.Document;
import org.slf4j.Logger;
import in.edu.kristujayanti.util.DateUtils;
import in.edu.kristujayanti.util.UserInfoUtil;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import static in.edu.kristujayanti.propertyBinder.Gym.GymKeysPBinder.*;

public class InsertServiceHandler implements Handler<RoutingContext> {
    private static final Logger LOGGER = LoggerFactory.getLogger(InsertServiceHandler.class);
    private final ServiceManager serviceManager;

    public InsertServiceHandler(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    @Override
    public void handle(RoutingContext routingContext) {
        HttpServerResponse response = routingContext.response();
        HttpServerRequest request = routingContext.request();

        response.putHeader("Content-Type", "application/json");

        JsonObject requestBody = routingContext.getBodyAsJson();
        if (requestBody == null) {
            ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.BAD_REQUEST, new JsonArray(),
                    new JsonArray().add("Invalid or missing JSON body"));
            return;
        }

        Document serviceDetailsReceived = Document.parse(requestBody.toString());

        List<String> requiredFields = Arrays.asList(
                MEMBER_TYPE.getPropertyName(),
                SERVICE_NAME.getPropertyName(),
                //DESCRIPTION_SERVICE.getPropertyName(),
                DURATION_SERVICE.getPropertyName(),
                AMOUNT_SERVICE.getPropertyName()
        );

        JsonArray validationResponse = DocumentParser.validateAndCleanDocument(serviceDetailsReceived, requiredFields);

        if (!validationResponse.isEmpty()) {
            ResponseUtil.createResponse(response, ResponseType.VALIDATION, StatusCode.TWOHUNDRED, new JsonArray(), validationResponse);
            return;
        }

        String memberType = serviceDetailsReceived.getString(MEMBER_TYPE.getPropertyName());
        if (!memberType.equalsIgnoreCase("student") && !memberType.equalsIgnoreCase("staff")) {
            ResponseUtil.createResponse(response, ResponseType.VALIDATION, StatusCode.TWOHUNDRED, new JsonArray(),
                    new JsonArray().add("Member type must be either 'student' or 'staff'"));
            return;
        }

        try {
            JsonObject insertResult = serviceManager.insertServiceDetails(serviceDetailsReceived, routingContext);
            LOGGER.info("Insert result: {}", insertResult.toString());

            String status = insertResult.getString("status");

            switch (status) {
                case "SUCCESS":
                    ResponseUtil.createResponse(response, ResponseType.SUCCESS, StatusCode.TWOHUNDRED, new JsonArray(),
                            new JsonArray().add("Service added successfully"));
                    break;
                case "DUPLICATE":
                    ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.CONFLICT, new JsonArray(),
                            new JsonArray().add("Service with the same name and member type already exists"));
                    break;
                case "FAILURE":
                    ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.INTERNAL_SERVER_ERROR, new JsonArray(),
                            new JsonArray().add("Failed to add new service"));
                    break;
                default:
                    ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.INTERNAL_SERVER_ERROR, new JsonArray(),
                            new JsonArray().add("Unexpected error occurred"));
                    break;
            }
        } catch (Exception e) {
            LOGGER.error("Error in InsertServiceHandler: {}", e.getMessage(), e);
            ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.INTERNAL_SERVER_ERROR, new JsonArray(),
                    new JsonArray().add("Internal server error while adding service"));
        }
    }
}