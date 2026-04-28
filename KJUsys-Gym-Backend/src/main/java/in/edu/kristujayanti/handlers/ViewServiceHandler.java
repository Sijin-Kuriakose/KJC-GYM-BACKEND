package in.edu.kristujayanti.handlers;

import in.edu.kristujayanti.enums.ResponseType;
import in.edu.kristujayanti.enums.StatusCode;
import in.edu.kristujayanti.services.ServiceManager;
import in.edu.kristujayanti.util.ResponseUtil;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ViewServiceHandler implements Handler<RoutingContext> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ViewServiceHandler.class);
    private final ServiceManager serviceManager;

    public ViewServiceHandler(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    @Override
    public void handle(RoutingContext routingContext) {
        HttpServerResponse response = routingContext.response();
        response.putHeader("Content-Type", "application/json");

        try {
            // Handle viewing a single service by ID
            String serviceId = routingContext.pathParam("id");
            if (serviceId != null) {
                if (!ObjectId.isValid(serviceId)) {
                    ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.BAD_REQUEST, new JsonArray(),
                            new JsonArray().add(new JsonObject().put("error", "Invalid service ID format. It must be a valid MongoDB ObjectId.")));
                    return;
                }

                JsonObject result = serviceManager.getServiceById(serviceId);
                handleSingleServiceResult(response, result);
                return;
            }

            // Handle viewing all services with pagination and filtering
            String memberType = routingContext.queryParams().get("memberType");
            if (memberType != null && !memberType.equalsIgnoreCase("student") && !memberType.equalsIgnoreCase("staff")) {
                ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.BAD_REQUEST, new JsonArray(),
                        new JsonArray().add(new JsonObject().put("error", "Member type must be either 'student' or 'staff'.")));
                return;
            }

            String isActiveStr = routingContext.queryParams().get("is_active");
            Boolean isActive = null;
            if (isActiveStr != null) {
                if (!isActiveStr.equalsIgnoreCase("true") && !isActiveStr.equalsIgnoreCase("false")) {
                    ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.BAD_REQUEST, new JsonArray(),
                            new JsonArray().add(new JsonObject().put("error", "is_active must be either 'true' or 'false'.")));
                    return;
                }
                isActive = Boolean.parseBoolean(isActiveStr);
            }

            String pageStr = routingContext.queryParams().get("page");
            String sizeStr = routingContext.queryParams().get("size");
            int page = 1;
            int size = 10;

            if (pageStr != null) {
                try {
                    page = Integer.parseInt(pageStr);
                    if (page < 1) {
                        ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.BAD_REQUEST, new JsonArray(),
                                new JsonArray().add(new JsonObject().put("error", "Page number must be at least 1.")));
                        return;
                    }
                } catch (NumberFormatException e) {
                    ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.BAD_REQUEST, new JsonArray(),
                            new JsonArray().add(new JsonObject().put("error", "Page number must be a valid integer.")));
                    return;
                }
            }

            if (sizeStr != null) {
                try {
                    size = Integer.parseInt(sizeStr);
                    if (size < 1 || size > 500) {
                        ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.BAD_REQUEST, new JsonArray(),
                                new JsonArray().add(new JsonObject().put("error", "Size must be between 1 and 500.")));
                        return;
                    }
                } catch (NumberFormatException e) {
                    ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.BAD_REQUEST, new JsonArray(),
                            new JsonArray().add(new JsonObject().put("error", "Size must be a valid integer.")));
                    return;
                }
            }

            JsonObject result = serviceManager.getAllServices(page, size, memberType != null ? memberType.toLowerCase() : null, isActive);
            handleMultipleServicesResult(response, result);

        } catch (Exception e) {
            LOGGER.error("Error in ViewServiceHandler: {}", e.getMessage(), e);
            ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.INTERNAL_SERVER_ERROR, new JsonArray(),
                    new JsonArray().add(new JsonObject().put("error", "Internal server error.")));
        }
    }

    private void handleSingleServiceResult(HttpServerResponse response, JsonObject result) {
        String status = result.getString("status");
        switch (status) {
            case "SUCCESS":
                JsonArray services = result.getJsonArray("services");
                JsonObject service = services.getJsonObject(0); // Extract the single service
                ResponseUtil.createResponse(response, ResponseType.SUCCESS, StatusCode.TWOHUNDRED, service,
                        new JsonArray());
                break;
            case "NOT_FOUND":
                ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.FILE_NOT_FOUND, new JsonObject(),
                        new JsonArray().add("Service not found."));
                break;
            case "FAILURE":
                ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.INTERNAL_SERVER_ERROR, new JsonObject(),
                        new JsonArray().add("Failed to retrieve service."));
                break;
            default:
                ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.INTERNAL_SERVER_ERROR, new JsonObject(),
                        new JsonArray().add("Unexpected error occurred."));
                break;
        }
    }

    private void handleMultipleServicesResult(HttpServerResponse response, JsonObject result) {
        String status = result.getString("status");
        switch (status) {
            case "SUCCESS":
                JsonArray services = result.getJsonArray("services");
                JsonObject pagination = result.getJsonObject("pagination");
                JsonObject responseData = new JsonObject()
                        .put("pagination", pagination)
                        .put("data", services);
                ResponseUtil.createResponse(response, ResponseType.SUCCESS, StatusCode.TWOHUNDRED, responseData,
                        new JsonArray().add("Services fetched successfully"));
                break;
            case "NO_DATA":
                ResponseUtil.createResponse(response, ResponseType.SUCCESS, StatusCode.TWOHUNDRED, new JsonObject()
                                .put("data", new JsonArray())
                                .put("pagination", new JsonObject()
                                        .put("currentPage", 1)
                                        .put("pageSize", 10)
                                        .put("totalRecords", 0)
                                        .put("totalPages", 0)),
                        new JsonArray().add("No services found."));
                break;
            case "FAILURE":
                ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.INTERNAL_SERVER_ERROR, new JsonArray(),
                        new JsonArray().add("Failed to retrieve services."));
                break;
            default:
                ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.INTERNAL_SERVER_ERROR, new JsonArray(),
                        new JsonArray().add("Unexpected error occurred."));
                break;
        }
    }
}