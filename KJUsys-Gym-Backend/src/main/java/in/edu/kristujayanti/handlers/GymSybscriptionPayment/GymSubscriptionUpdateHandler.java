package in.edu.kristujayanti.handlers.GymSybscriptionPayment;

import in.edu.kristujayanti.enums.ResponseType;
import in.edu.kristujayanti.enums.StatusCode;
import in.edu.kristujayanti.services.UserServices.SubscriptionService;
import in.edu.kristujayanti.util.DateUtils;
import in.edu.kristujayanti.util.DocumentParser;
import in.edu.kristujayanti.util.ResponseUtil;
import in.edu.kristujayanti.util.UserInfoUtil;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import static in.edu.kristujayanti.propertyBinder.Eform.EformCommonKeysPBinder.APPLICATION_NUMBER;
import static in.edu.kristujayanti.propertyBinder.FeeModule.FeeModuleKeysPBinder.FEE_PAYMENT_ORDER_ID;

//public class GymSubscriptionUpdateHandler {
//}

public class GymSubscriptionUpdateHandler implements Handler<RoutingContext> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GymSubscriptionUpdateHandler.class);
    private final SubscriptionService subscriptionService;


    public GymSubscriptionUpdateHandler(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @Override
    public void handle(RoutingContext routingContext) {
        JsonObject requestBody = routingContext.body().asJsonObject();
        HttpServerResponse response = routingContext.response();
        String loggedInUserEmail = UserInfoUtil.getLoggedInUserEmail(routingContext.request().headers());
        String currentDateTimeString = DateUtils.convertMillisToDateString(DateUtils.currentDateTimeInMillis());

        // Validate user authentication
        if (loggedInUserEmail == null) {
            LOGGER.warn("Access denied: User information is missing");
            ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.UNAUTHORIZED,
                    new JsonArray(), new JsonArray().add(new JsonObject().put("message", "User information is missing")));
            return;
        }

        // Validate required fields
        List<String> requiredFields = Arrays.asList(
                FEE_PAYMENT_ORDER_ID.getPropertyName()
        );

        try {
            //parse the request body to document
            Document paramsDocument = Document.parse(requestBody.toString());
            JsonArray validationResponse = DocumentParser.validateAndCleanDocument(paramsDocument, requiredFields);

            //validate the request body
            if (!validationResponse.isEmpty()) {
                ResponseUtil.createResponse(response, ResponseType.VALIDATION, StatusCode.BAD_REQUEST,  new JsonArray(), validationResponse);
                return;
            }

            if (!paramsDocument.containsKey(FEE_PAYMENT_ORDER_ID.getPropertyName())) {
                LOGGER.warn("[{}] Incomplete payment details for user [{}]", currentDateTimeString, loggedInUserEmail);
                ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.BAD_REQUEST,
                        new JsonArray(), new JsonArray().add("Incomplete payment details"));
                return;
            }

            // Process payment details
            ObjectId feePaymentRequestId = paramsDocument.getObjectId(FEE_PAYMENT_ORDER_ID.getPropertyName());
            Document paymentDetailsJson = this.subscriptionService.fetchFeePaymentRequestDetails(feePaymentRequestId);

            // Update student payment details
//            if (!feeSettingService.updateStudentPaymentDetails(paymentDetailsJson)) {
//                LOGGER.warn("Failed to update student payment details");
//                ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.INTERNAL_SERVER_ERROR,
//                        new JsonArray(), new JsonArray().add("Failed to update student payment details"));
//                return;
//            }

            // Process and classify payments
//            List<Document> processedPaymentDetails = feeSettingService.processThePaymentDetail(paymentDetailsJson);
//            String applicationNumber = paymentDetailsJson.getString(APPLICATION_NUMBER.getPropertyName());
//            Document groupPaymentsSummary = feeSettingService.classifyGroupPayments(applicationNumber);


//            paymentDetailsJson.put("feeGroupDetails", processedPaymentDetails);
//            paymentDetailsJson.put("feeDetails", groupPaymentsSummary);

//            if (!this.feeSettingService.updateFeePaymentOrderRequest(feePaymentRequestId, paymentDetailsJson)) {
//                LOGGER.error("Failed to update fee payment order request");
//                ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.INTERNAL_SERVER_ERROR,
//                        new JsonArray(), new JsonArray().add("Failed to update payment order"));
//                return;
//            }

            // Success response
            ResponseUtil.createResponse(response, ResponseType.SUCCESS, StatusCode.TWOHUNDRED,
                    new JsonArray().add(paymentDetailsJson), new JsonArray().add("Successfully Updated"));

        } catch (Exception e) {
            LOGGER.error("[{}] Unexpected error during payment verification for user [{}]: {}",
                    currentDateTimeString, loggedInUserEmail, e.getMessage(), e);
            ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.INTERNAL_SERVER_ERROR,
                    new JsonArray(), new JsonArray().add("Unexpected error occurred"));
        }
    }
}
