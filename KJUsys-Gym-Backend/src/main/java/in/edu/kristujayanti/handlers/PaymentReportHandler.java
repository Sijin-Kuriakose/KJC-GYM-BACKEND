package in.edu.kristujayanti.handlers;

import in.edu.kristujayanti.enums.ResponseType;
import in.edu.kristujayanti.enums.StatusCode;
import in.edu.kristujayanti.services.PaymentReport;
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
import java.time.ZoneId;

public class PaymentReportHandler implements Handler<RoutingContext> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentReportHandler.class);
    private final PaymentReport paymentReport;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public PaymentReportHandler(PaymentReport paymentReport) {
        this.paymentReport = paymentReport;
    }

    @Override
    public void handle(RoutingContext routingContext) {
        HttpServerResponse response = routingContext.response();
        response.putHeader("Content-Type", "application/json");

        try {
            JsonObject requestBody = routingContext.getBodyAsJson();

            // Validate and process userType from query params
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

            // Get page and pageSize parameters from query params (defaults to 1 and 10 if not provided)
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

            // Handle date filters
            Long fromDateMillis = null;
            Long toDateMillis = null;

            if (requestBody == null || requestBody.isEmpty()) {
                // No body or empty body: filter for current date
                LocalDate currentDate = LocalDate.now();
                fromDateMillis = currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
                toDateMillis = currentDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() - 1;
                LOGGER.info("No date filter provided, using current date: {}", currentDate.format(DATE_FORMATTER));
            } else {
                // Process from_Gym_Datetime and to_Gym_Datetime from JSON body
                String fromDateStr = requestBody.getString("from_Gym_DateTime");
                String toDateStr = requestBody.getString("to_Gym_DateTime");

                if (fromDateStr != null && toDateStr != null) {
                    try {
                        LocalDate fromDate = LocalDate.parse(fromDateStr, DATE_FORMATTER);
                        LocalDate toDate = LocalDate.parse(toDateStr, DATE_FORMATTER);

                        // Validate date range
                        if (toDate.isBefore(fromDate)) {
                            ResponseUtil.createResponse(
                                    response,
                                    ResponseType.ERROR,
                                    StatusCode.BAD_REQUEST,
                                    new JsonObject(),
                                    new JsonArray().add(new JsonObject().put("error", "to_Gym_DateTime must be greater than from_Gym_DaTetime.")));
                            return;
                        }

                        // Convert to milliseconds (start and end of day)
                        fromDateMillis = fromDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
                        toDateMillis = toDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() - 1;
                    } catch (DateTimeParseException e) {
                        ResponseUtil.createResponse(
                                response,
                                ResponseType.ERROR,
                                StatusCode.BAD_REQUEST,
                                new JsonObject(),
                                new JsonArray().add(new JsonObject().put("error", "Invalid date format. Use dd-MM-yyyy.")));
                        return;
                    }
                } else if (fromDateStr != null || toDateStr != null) {
                    ResponseUtil.createResponse(
                            response,
                            ResponseType.ERROR,
                            StatusCode.BAD_REQUEST,
                            new JsonObject(),
                            new JsonArray().add(new JsonObject().put("error", "Both from_Gym_DateTime and to_Gym_DateTime must be provided.")));
                    return;
                } else {
                    // Empty body with no dates: filter for current date
                    LocalDate currentDate = LocalDate.now();
                    fromDateMillis = currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
                    toDateMillis = currentDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() - 1;
                    LOGGER.info("Empty date filter provided, using current date: {}", currentDate.format(DATE_FORMATTER));
                }
            }

            // Fetch the report data with date filters
            JsonObject result = paymentReport.getPaymentReport(
                    userType != null ? userType.toLowerCase() : null,
                    fromDateMillis,
                    toDateMillis,
                    page,
                    pageSize
            );
            handleReportResponse(response, result);

        } catch (Exception e) {
            LOGGER.error("Error in PaymentReportHandler: {}", e.getMessage(), e);
            ResponseUtil.createResponse(
                    response,
                    ResponseType.ERROR,
                    StatusCode.INTERNAL_SERVER_ERROR,
                    new JsonObject(),
                    new JsonArray().add(new JsonObject().put("error", "Internal server error.")));
        }
    }

    private void handleReportResponse(HttpServerResponse response, JsonObject result) {
        String status = result.getString("status");

        if (status == null) {
            ResponseUtil.createResponse(
                    response,
                    ResponseType.ERROR,
                    StatusCode.INTERNAL_SERVER_ERROR,
                    new JsonObject(),
                    new JsonArray().add(new JsonObject().put("error", "Invalid response format.")));
            return;
        }

        switch (status) {
            case "SUCCESS":
                JsonObject data = result.getJsonObject("data", new JsonObject());
                LOGGER.info("Report generated with {} records", data.getJsonArray("data").size());
                ResponseUtil.createResponse(
                        response,
                        ResponseType.SUCCESS,
                        StatusCode.TWOHUNDRED,
                        data,
                        new JsonArray().add("Report fetched successfully")
                );
                break;

            case "FAILURE":
                ResponseUtil.createResponse(
                        response,
                        ResponseType.ERROR,
                        StatusCode.INTERNAL_SERVER_ERROR,
                        new JsonObject(),
                        result.getJsonArray("message", new JsonArray().add(new JsonObject().put("error", "Failed to retrieve report."))));
                break;

            default:
                ResponseUtil.createResponse(
                        response,
                        ResponseType.ERROR,
                        StatusCode.INTERNAL_SERVER_ERROR,
                        new JsonObject(),
                        new JsonArray().add(new JsonObject().put("error", "Unexpected error occurred.")));
                break;
        }
    }
}