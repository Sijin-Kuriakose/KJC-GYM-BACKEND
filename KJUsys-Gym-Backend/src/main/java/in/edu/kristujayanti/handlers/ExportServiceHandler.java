package in.edu.kristujayanti.handlers;

import in.edu.kristujayanti.enums.ResponseType;
import in.edu.kristujayanti.enums.StatusCode;
import in.edu.kristujayanti.services.ServiceManager;
import in.edu.kristujayanti.util.ExcelDownloadUtil;
import in.edu.kristujayanti.util.ResponseUtil;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.apache.poi.ss.usermodel.Workbook;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static in.edu.kristujayanti.propertyBinder.Gym.GymKeysPBinder.*;

public class ExportServiceHandler implements Handler<RoutingContext> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExportServiceHandler.class);
    private final ServiceManager serviceManager;

    public ExportServiceHandler(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    @Override
    public void handle(RoutingContext routingContext) {
        HttpServerResponse response = routingContext.response();

        try {
            // Extract query parameters
            String memberType = routingContext.queryParams().get("memberType");
            if (memberType != null && !memberType.equalsIgnoreCase("student") && !memberType.equalsIgnoreCase("staff")) {
                ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.BAD_REQUEST, new JsonArray(),
                        new JsonArray().add(new JsonObject().put("error", "Member type must be either 'student' or 'staff'.")));
                return;
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
                    if (size < 1 || size > 100) {
                        ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.BAD_REQUEST, new JsonArray(),
                                new JsonArray().add(new JsonObject().put("error", "Size must be between 1 and 100.")));
                        return;
                    }
                } catch (NumberFormatException e) {
                    ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.BAD_REQUEST, new JsonArray(),
                            new JsonArray().add(new JsonObject().put("error", "Size must be a valid integer.")));
                    return;
                }
            }

            // Fetch services as a list of Documents
            List<Document> services = serviceManager.getAllServicesAsDocuments(page, size, memberType != null ? memberType.toLowerCase() : null);

            if (services.isEmpty()) {
                ResponseUtil.createResponse(response, ResponseType.SUCCESS, StatusCode.TWOHUNDRED, new JsonArray(),
                        new JsonArray().add(new JsonObject().put("message", "No services found to export.")));
                return;
            }

            // Define the fields (columns) to include in the Excel file
            List<String> fields = Arrays.asList(
                    MEMBER_TYPE.getPropertyName(),
                    SERVICE_NAME.getPropertyName(),
                    DESCRIPTION_SERVICE.getPropertyName(),
                    DURATION_SERVICE.getPropertyName(),
                    AMOUNT_SERVICE.getPropertyName(),
                    SERVICE_STATUS.getPropertyName()
            );

            // Create the Excel workbook using ExcelDownloadUtil
            ExcelDownloadUtil excelUtil = new ExcelDownloadUtil(null); // We don't need MongoDatabase since we're passing the documents directly
            Workbook workbook = ExcelDownloadUtil.createExcelWorkbook(fields, services);

            // Convert the workbook to a byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            byte[] excelBytes = baos.toByteArray();
            workbook.close();
            baos.close();

            // Set response headers for Excel file download
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = "gym_services_" + timestamp + ".xlsx";
            response.putHeader("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.putHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
            response.putHeader("Content-Length", String.valueOf(excelBytes.length));

            // Send the Excel file as the response
            response.end(Buffer.buffer(excelBytes));

        } catch (Exception e) {
            LOGGER.error("Error in ExportServiceHandler: {}", e.getMessage(), e);
            ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.INTERNAL_SERVER_ERROR, new JsonArray(),
                    new JsonArray().add(new JsonObject().put("error", "Internal server error while exporting services.")));
        }
    }
}