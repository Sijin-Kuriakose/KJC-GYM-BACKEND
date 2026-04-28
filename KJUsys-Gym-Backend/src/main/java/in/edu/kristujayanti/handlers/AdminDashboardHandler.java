package in.edu.kristujayanti.handlers;

import in.edu.kristujayanti.enums.ResponseType;
import in.edu.kristujayanti.enums.StatusCode;
import in.edu.kristujayanti.services.AdminDashboardService;
import in.edu.kristujayanti.util.DateUtils;
import in.edu.kristujayanti.util.ResponseUtil;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Handler for admin dashboard POST requests, processing the request body to fetch dashboard data
 * with pagination and date range filtering.
 */
public class AdminDashboardHandler implements Handler<RoutingContext> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminDashboardHandler.class);
    private final AdminDashboardService dashboardService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public AdminDashboardHandler(AdminDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @Override
    public void handle(RoutingContext routingContext) {
        HttpServerResponse response = routingContext.response();
        response.putHeader("Content-Type", "application/json");

        try {
            // Step 1: Parse the request body as JSON
            JsonObject requestBody = routingContext.getBodyAsJson();
            if (requestBody == null) {
                ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.BAD_REQUEST, new JsonArray(),
                        new JsonArray().add(new JsonObject().put("error", "Request body is required and must be valid JSON.")));
                return;
            }

            // Step 2: Extract and validate pagination parameters
            Integer page = requestBody.getInteger("page", 1); // Default to 1 if not provided
            Integer size = requestBody.getInteger("size", 10); // Default to 10 if not provided

            // Validate page number
            if (page < 1) {
                ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.BAD_REQUEST, new JsonArray(),
                        new JsonArray().add(new JsonObject().put("error", "Page number must be at least 1.")));
                return;
            }

            // Validate size parameter
            if (size < 1 || size > 500) {
                ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.BAD_REQUEST, new JsonArray(),
                        new JsonArray().add(new JsonObject().put("error", "Size must be between 1 and 500.")));
                return;
            }

            // Step 3: Extract and validate date range parameters (from_Gym_DateTime and to_Gym_DateTime)
            String fromStr = requestBody.getString("from_Gym_DateTime");
            String toStr = requestBody.getString("to_Gym_DateTime");
            Long from = null;
            Long to = null;

            // If from or to is not provided, default to today
            LocalDate today = LocalDate.now();
            String todayStr = today.format(DATE_FORMATTER);

            if (fromStr != null && !fromStr.isEmpty()) {
                try {
                    from = DateUtils.getStartOfDayMillis(fromStr);
                } catch (DateTimeParseException e) {
                    ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.BAD_REQUEST, new JsonArray(),
                            new JsonArray().add(new JsonObject().put("error", "Invalid 'from_Gym_DateTime' format. Use dd-MM-yyyy.")));
                    return;
                }
            } else {
                // Default to start of today
                from = DateUtils.getStartOfDayMillis(todayStr);
                fromStr = todayStr; // Set fromStr for response
            }

            if (toStr != null && !toStr.isEmpty()) {
                try {
                    to = DateUtils.getEndOfDayMillis(toStr);
                } catch (DateTimeParseException e) {
                    ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.BAD_REQUEST, new JsonArray(),
                            new JsonArray().add(new JsonObject().put("error", "Invalid 'to_Gym_DateTime' format. Use dd-MM-yyyy.")));
                    return;
                }
            } else {
                // Default to end of today
                to = DateUtils.getEndOfDayMillis(todayStr);
                toStr = todayStr; // Set toStr for response
            }

            // Step 4: Validate that 'from' is not after 'to'
            if (from > to) {
                ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.BAD_REQUEST, new JsonArray(),
                        new JsonArray().add(new JsonObject().put("error", "'from_Gym_DateTime' cannot be after 'to_Gym_DateTime'.")));
                return;
            }

            // Step 5: Fetch dashboard data with the provided date range
            JsonObject result = dashboardService.getDashboardData(page, size, from, to);
            handleDashboardDataResult(response, result, fromStr, toStr);

        } catch (Exception e) {
            LOGGER.error("Error in AdminDashboardHandler: {}", e.getMessage(), e);
            ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.INTERNAL_SERVER_ERROR, new JsonArray(),
                    new JsonArray().add(new JsonObject().put("error", "Internal server error.")));
        }
    }

    /**
     * Handles the result from the AdminDashboardService and constructs the appropriate HTTP response,
     * including the from_Gym_DateTime and to_Gym_DateTime in the response.
     *
     * @param response The HTTP server response object.
     * @param result   The result from the AdminDashboardService.
     * @param fromStr  The from_Gym_DateTime string to include in the response.
     * @param toStr    The to_Gym_DateTime string to include in the response.
     */
    private void handleDashboardDataResult(HttpServerResponse response, JsonObject result, String fromStr, String toStr) {
        String status = result.getString("status");
        switch (status) {
            case "SUCCESS":
                JsonArray members = result.getJsonArray("data"); // Members array from the service
                JsonObject pagination = result.getJsonObject("pagination"); // Pagination details
                JsonObject dashboardMetrics = result.getJsonObject("dashboardMetrics"); // Dashboard metrics
                JsonObject responseData = new JsonObject()
                        .put("pagination", pagination)
                        .put("data", members)
                        .put("dashboardMetrics", dashboardMetrics);
                        //.put("from_Gym_DateTime", fromStr)
                        //.put("to_Gym_DateTime", toStr);
                ResponseUtil.createResponse(response, ResponseType.SUCCESS, StatusCode.TWOHUNDRED, responseData,
                        new JsonArray().add("Dashboard data fetched successfully"));
                break;
            case "NO_DATA":
                ResponseUtil.createResponse(response, ResponseType.SUCCESS, StatusCode.TWOHUNDRED, new JsonObject()
                                .put("data", new JsonArray())
                                .put("pagination", new JsonObject()
                                        .put("currentPage", 1)
                                        .put("pageSize", 10)
                                        .put("totalRecords", 0)
                                        .put("totalPages", 0)),
                               // .put("from_Gym_DateTime", fromStr)
                                //.put("to_Gym_DateTime", toStr),
                        new JsonArray().add("No data in dashboard"));
                break;
            case "FAILURE":
                ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.INTERNAL_SERVER_ERROR, new JsonArray(),
                        new JsonArray().add("Failed to fetch dashboard data."));
                break;
            default:
                ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.INTERNAL_SERVER_ERROR, new JsonArray(),
                        new JsonArray().add("Unexpected error occurred."));
                break;
        }
    }
}