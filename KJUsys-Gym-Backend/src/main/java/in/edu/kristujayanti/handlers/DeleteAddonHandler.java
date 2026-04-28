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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeleteAddonHandler implements Handler<RoutingContext> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteAddonHandler.class);
    private final AddonService addonService;

    public DeleteAddonHandler(AddonService addonService) {
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

        try {
            JsonObject deleteResult = addonService.deleteAddonById(addonId);
            String status = deleteResult.getString("status");

            switch (status) {
                case "SUCCESS":
                    ResponseUtil.createResponse(response, ResponseType.SUCCESS, StatusCode.TWOHUNDRED, new JsonArray(),
                            new JsonArray().add("Addon deleted successfully"));
                    break;
                case "NOT_FOUND":
                    ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.FILE_NOT_FOUND, new JsonArray(),
                            new JsonArray().add("Addon not found."));
                    break;
                case "FAILURE":
                    ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.INTERNAL_SERVER_ERROR, new JsonArray(),
                            new JsonArray().add("Failed to delete addon."));
                    break;
                default:
                    ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.INTERNAL_SERVER_ERROR, new JsonArray(),
                            new JsonArray().add("Unexpected error occurred."));
                    break;
            }
        } catch (Exception e) {
            LOGGER.error("Error in deleteAddonsHandler: {}", e.getMessage(), e);
            ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.INTERNAL_SERVER_ERROR, new JsonArray(),
                    new JsonArray().add("Internal server error."));
        }
    }
}