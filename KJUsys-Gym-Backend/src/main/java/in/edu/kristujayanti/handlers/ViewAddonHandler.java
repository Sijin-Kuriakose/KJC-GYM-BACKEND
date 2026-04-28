package in.edu.kristujayanti.handlers;

import in.edu.kristujayanti.enums.ResponseType;
import in.edu.kristujayanti.enums.StatusCode;
import in.edu.kristujayanti.services.AddonService;
import in.edu.kristujayanti.util.ResponseUtil;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ViewAddonHandler implements Handler<RoutingContext> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ViewAddonHandler.class);
    private final AddonService addonService;

    public ViewAddonHandler(AddonService addonService) {
        this.addonService = addonService;
    }

    @Override
    public void handle(RoutingContext routingContext) {
        HttpServerResponse response = routingContext.response();
        response.putHeader("Content-Type", "application/json");

        try {

            String addonId = routingContext.pathParam("addonId");
            if (addonId != null) {
                if (!ObjectId.isValid(addonId)) {
                    ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.BAD_REQUEST, new JsonObject(),
                            new JsonArray().add(new JsonObject().put("error", "Invalid addon ID format. It must be a valid MongoDB ObjectId.")));
                    return;
                }

                JsonObject result = addonService.getAddonById(addonId);
                handleSingleAddonResult(response, result);
                return;
            }

            String memberType = routingContext.queryParams().get("memberType");
            if (memberType != null && !memberType.equalsIgnoreCase("student") && !memberType.equalsIgnoreCase("staff")) {
                ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.BAD_REQUEST, new JsonObject(),
                        new JsonArray().add(new JsonObject().put("error", "Member type must be either 'student' or 'staff'.")));
                return;
            }

            String pageStr = routingContext.queryParams().get("page");
            String sizeStr = routingContext.queryParams().get("size");
            String statusStr = routingContext.queryParams().get("status");
            int page = 1;
            int size = 10;
            Boolean status = null;

            // Parse page and size
            if (pageStr != null) {
                try {
                    page = Integer.parseInt(pageStr);
                    if (page < 1) {
                        ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.BAD_REQUEST, new JsonObject(),
                                new JsonArray().add(new JsonObject().put("error", "Page number must be at least 1.")));
                        return;
                    }
                } catch (NumberFormatException e) {
                    ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.BAD_REQUEST, new JsonObject(),
                            new JsonArray().add(new JsonObject().put("error", "Page number must be a valid integer.")));
                    return;
                }
            }

            // Parse size
            if (sizeStr != null) {
                try {
                    size = Integer.parseInt(sizeStr);
                    if (size < 1 || size > 500) {
                        ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.BAD_REQUEST, new JsonObject(),
                                new JsonArray().add(new JsonObject().put("error", "Size must be between 1 and 500.")));
                        return;
                    }
                } catch (NumberFormatException e) {
                    ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.BAD_REQUEST, new JsonObject(),
                            new JsonArray().add(new JsonObject().put("error", "Size must be a valid integer.")));
                    return;
                }
            }

            // Parse status filter
            if (statusStr != null) {
                if (statusStr.equalsIgnoreCase("true")) {
                    status = true;
                } else if (statusStr.equalsIgnoreCase("false")) {
                    status = false;
                } else {
                    ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.BAD_REQUEST, new JsonObject(),
                            new JsonArray().add(new JsonObject().put("error", "Status must be 'true' or 'false'.")));
                    return;
                }
            }

            // Fetch the addons with filters
            JsonObject result = addonService.getAllAddons(page, size, memberType != null ? memberType.toLowerCase() : null, status);
            handleMultipleAddonsResult(response, result);

        } catch (Exception e) {
            LOGGER.error("Error in ViewAddonHandler: {}", e.getMessage(), e);
            ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.INTERNAL_SERVER_ERROR, new JsonObject(),
                    new JsonArray().add(new JsonObject().put("error", "Internal server error.")));
        }
    }

    private void handleSingleAddonResult(HttpServerResponse response, JsonObject result) {
        String status = result.getString("status");

        if (status == null) {
            ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.INTERNAL_SERVER_ERROR, new JsonObject(),
                    new JsonArray().add(new JsonObject().put("error", "Invalid response format.")));
            return;
        }

        switch (status) {
            case "SUCCESS":
                JsonObject addon = result.getJsonObject("addon");

                if (addon == null) {
                    ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.FILE_NOT_FOUND, new JsonObject(),
                            new JsonArray().add(new JsonObject().put("error", "Addon not found.")));
                    return;
                }

                JsonArray messages = result.getJsonArray("message", new JsonArray());
                ResponseUtil.createResponse(response, ResponseType.SUCCESS, StatusCode.TWOHUNDRED, addon, messages);
                break;

            case "NOT_FOUND":
                ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.FILE_NOT_FOUND, new JsonObject(),
                        new JsonArray().add(new JsonObject().put("error", "Addon not found.")));
                break;

            case "FAILURE":
                ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.INTERNAL_SERVER_ERROR, new JsonObject(),
                        new JsonArray().add(new JsonObject().put("error", "Failed to retrieve addon.")));
                break;

            default:
                ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.INTERNAL_SERVER_ERROR, new JsonObject(),
                        new JsonArray().add(new JsonObject().put("error", "Unexpected error occurred.")));
                break;
        }
    }

    private void handleMultipleAddonsResult(HttpServerResponse response, JsonObject result) {
        String status = result.getString("status");

        if (status == null) {
            ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.INTERNAL_SERVER_ERROR, new JsonObject(),
                    new JsonArray().add(new JsonObject().put("error", "Invalid response format.")));
            return;
        }

        switch (status) {
            case "SUCCESS":
                JsonObject responseData = result.getJsonObject("data", new JsonObject());
                JsonArray messages = result.getJsonArray("message", new JsonArray());

                ResponseUtil.createResponse(response, ResponseType.SUCCESS, StatusCode.TWOHUNDRED, responseData, messages);
                break;

            case "NOT_FOUND":
                ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.FILE_NOT_FOUND, new JsonObject(),
                        new JsonArray().add(new JsonObject().put("error", "No addons found.")));
                break;

            case "FAILURE":
                ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.INTERNAL_SERVER_ERROR, new JsonObject(),
                        new JsonArray().add(new JsonObject().put("error", "Failed to retrieve addons.")));
                break;

            default:
                ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.INTERNAL_SERVER_ERROR, new JsonObject(),
                        new JsonArray().add(new JsonObject().put("error", "Unexpected error occurred.")));
                break;
        }
    }
}