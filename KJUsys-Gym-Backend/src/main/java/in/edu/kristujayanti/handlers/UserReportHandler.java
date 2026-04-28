package in.edu.kristujayanti.handlers;

import in.edu.kristujayanti.enums.ResponseType;
import in.edu.kristujayanti.enums.StatusCode;
import in.edu.kristujayanti.services.UserReport;
import in.edu.kristujayanti.util.ResponseUtil;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserReportHandler implements Handler<RoutingContext> {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserReportHandler.class);
    private final UserReport userReport;

    public UserReportHandler(UserReport userReport) {
        this.userReport = userReport;
    }

    @Override
    public void handle(RoutingContext routingContext) {
        HttpServerResponse response = routingContext.response();
        response.putHeader("Content-Type", "application/json");

        try {
            // Validate and process userType
            String userType = routingContext.queryParams().get("userType");
            if (userType != null &&
                    !userType.equalsIgnoreCase("student") &&
                    !userType.equalsIgnoreCase("staff")) {
                ResponseUtil.createResponse(
                        response,
                        ResponseType.ERROR,
                        StatusCode.BAD_REQUEST,
                        new JsonObject(),
                        new JsonArray().add(new JsonObject().put("error", "User type must be either 'student' or 'staff'.")));
                return;
            }

            // Get page and pageSize parameters (defaults to 1 and 10 if not provided)
            String pageStr = routingContext.queryParams().get("page");
            String sizeStr = routingContext.queryParams().get("pageSize");

            int page = 1;
            int pageSize = 10;

            // Process page parameter
            if (pageStr != null) {
                try {
                    page = Integer.parseInt(pageStr);
                    if (page < 1) {
                        ResponseUtil.createResponse(
                                response,
                                ResponseType.ERROR,
                                StatusCode.BAD_REQUEST,
                                new JsonObject(),
                                new JsonArray().add(new JsonObject().put("error", "Page number must be at least 1.")));
                        return;
                    }
                } catch (NumberFormatException e) {
                    ResponseUtil.createResponse(
                            response,
                            ResponseType.ERROR,
                            StatusCode.BAD_REQUEST,
                            new JsonObject(),
                            new JsonArray().add(new JsonObject().put("error", "Page number must be a valid integer.")));
                    return;
                }
            }

            // Process pageSize parameter
            if (sizeStr != null) {
                try {
                    pageSize = Integer.parseInt(sizeStr);
                    if (pageSize < 1 || pageSize > 500) {
                        ResponseUtil.createResponse(
                                response,
                                ResponseType.ERROR,
                                StatusCode.BAD_REQUEST,
                                new JsonObject(),
                                new JsonArray().add(new JsonObject().put("error", "Size must be between 1 and 500.")));
                        return;
                    }
                } catch (NumberFormatException e) {
                    ResponseUtil.createResponse(
                            response,
                            ResponseType.ERROR,
                            StatusCode.BAD_REQUEST,
                            new JsonObject(),
                            new JsonArray().add(new JsonObject().put("error", "Size must be a valid integer.")));
                    return;
                }
            }

            // Fetch the report data
            JsonObject result = userReport.getUserReport(userType != null ? userType.toUpperCase() : null, page, pageSize);
            handleReportResponse(response, result);

        } catch (Exception e) {
            LOGGER.error("Error in UserReportHandler: {}", e.getMessage(), e);
            ResponseUtil.createResponse(
                    response,
                    ResponseType.ERROR,
                    StatusCode.INTERNAL_SERVER_ERROR,
                    new JsonObject(),
                    new JsonArray().add(new JsonObject().put("error", "Internal server error.")));
        }
    }

    private void handleReportResponse(HttpServerResponse response, JsonObject result) {
        if (result.getString("status").equals("SUCCESS")) {
            ResponseUtil.createResponse(
                    response,
                    ResponseType.SUCCESS,
                    StatusCode.TWOHUNDRED,
                    result.getJsonObject("data"),
                    result.getJsonArray("message"));
        } else {
            ResponseUtil.createResponse(
                    response,
                    ResponseType.ERROR,
                    StatusCode.INTERNAL_SERVER_ERROR,
                    new JsonObject(),
                    result.getJsonArray("message"));
        }
    }
}