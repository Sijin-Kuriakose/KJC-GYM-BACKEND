package in.edu.kristujayanti.handlers.GymSybscriptionPayment;

import in.edu.kristujayanti.enums.ResponseType;
import in.edu.kristujayanti.enums.StatusCode;
import in.edu.kristujayanti.services.UserServices.SubscriptionService;
import in.edu.kristujayanti.util.CreateHttpClientUtil;
import in.edu.kristujayanti.util.DocumentParser;
import in.edu.kristujayanti.util.ResponseUtil;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.WebClient;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisAPI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static in.edu.kristujayanti.propertyBinder.FeeModule.FeeModuleKeysPBinder.*;
import static in.edu.kristujayanti.propertyBinder.Gym.GymKeysPBinder.*;

/**
 * SubscriptionHandler manages subscription-related HTTP requests, routing them to the SubscriptionService.
 */
public class SubscriptionHandler {
    private static final Logger LOGGER = LogManager.getLogger(SubscriptionHandler.class);
    private final SubscriptionService subscriptionService;
    private final WebClient webClient;
    private final Redis redisCommandConnection;
    private final String redisHashKey;
    private static final long TIMEOUT_MS = 10000; // 10 seconds timeout for async operations

    public SubscriptionHandler(SubscriptionService subscriptionService, Redis redisCommandConnection, String redisHashKey, WebClient webClient) {
        this.subscriptionService = subscriptionService;
        this.webClient = webClient;
        this.redisCommandConnection = redisCommandConnection;
        this.redisHashKey = redisHashKey;
    }
//
//    /**
//     * Handles POST requests to add a subscription (service and/or add-ons).
//     * Expects query param 'emailId' and a JSON body with 'serviceOid', 'preferredStartDate', and optional 'subscriptionAddOnList'.
//     */
//    public void handleSubscriptionRequest(RoutingContext routingContext) {
//        Vertx vertx = routingContext.vertx();
//        try {
//            String emailId = routingContext.request().getParam("emailId");
//            if (emailId == null || emailId.trim().isEmpty()) {
//                LOGGER.warn("Missing or empty emailId parameter");
//                ResponseUtil.createResponse(
//                        routingContext.response(),
//                        ResponseType.VALIDATION,
//                        StatusCode.BAD_REQUEST,
//                        new JsonArray(),
//                        new JsonArray().add("Email ID is required")
//                );
//                return;
//            }
//            emailId = emailId.toUpperCase();
//
//            JsonObject requestBody = routingContext.getBodyAsJson();
//            HttpServerResponse response = routingContext.response();
//
//            if (requestBody == null || requestBody.isEmpty()) {
//                LOGGER.warn("Missing or empty request body for emailID: {}", emailId);
//                ResponseUtil.createResponse(
//                        routingContext.response(),
//                        ResponseType.VALIDATION,
//                        StatusCode.BAD_REQUEST,
//                        new JsonArray(),
//                        new JsonArray().add("Request body is required")
//                );
//                return;
//            }
//            // Parse and validate the request document
//            Document paramsDocument = Document.parse(requestBody.toString());
//            JsonArray validationResponse = DocumentParser.validateAndCleanDocument(paramsDocument, requiredFields);
//            // Check validation response
//            if (!validationResponse.isEmpty()) {
//                ResponseUtil.createResponse(response, ResponseType.VALIDATION, StatusCode.BAD_REQUEST, new JsonArray(), validationResponse);
//                return;
//            }
//
//            LOGGER.debug("Processing subscription request for emailID: {}", emailId);
//            Promise<Document> subscriptionPromise = Promise.promise();
//            vertx.setTimer(TIMEOUT_MS, id -> {
//                if (!subscriptionPromise.future().isComplete()) {
//                    subscriptionPromise.fail(new TimeoutException("Subscription processing timed out"));
//                }
//            });
//            subscriptionService.addSubscription(emailId, requestBody).onComplete(subscriptionPromise);
//
//            String finalEmailId = emailId;
//            subscriptionPromise.future().onSuccess(memberDetails -> {
//                LOGGER.info("Subscription processed successfully for emailID: {}", finalEmailId);
//                if (memberDetails == null) {
//                    LOGGER.error("Member details are null for emailID: {}", finalEmailId);
//                    ResponseUtil.createResponse(
//                            routingContext.response(),
//                            ResponseType.ERROR,
//                            StatusCode.INTERNAL_SERVER_ERROR,
//                            new JsonArray(),
//                            new JsonArray().add("Subscription details are empty")
//                    );
//                    return;
//                }
//                System.out.println(memberDetails);
//                // Process fee payment request
//                Promise<ObjectId> feePaymentPromise = Promise.promise();
//                vertx.setTimer(TIMEOUT_MS, id -> {
//                    if (!feePaymentPromise.future().isComplete()) {
//                        feePaymentPromise.fail(new TimeoutException("Fee payment request save timed out"));
//                    }
//                });
//                subscriptionService.saveFeePaymentRequestFuture(memberDetails).onComplete(feePaymentPromise);
//
//                feePaymentPromise.future().onComplete(ar -> {
//                    if (ar.succeeded() && ar.result() != null) {
//                        ObjectId feePaymentRequestId = ar.result();
//                        LOGGER.debug("Fee payment request saved with ID: {} for emailID: {}", feePaymentRequestId, finalEmailId);
//                        Promise<Document> feeResponsePromise = Promise.promise();
//                        vertx.setTimer(TIMEOUT_MS, id -> {
//                            if (!feeResponsePromise.future().isComplete()) {
//                                feeResponsePromise.fail(new TimeoutException("Fee payment response fetch timed out"));
//                            }
//                        });
//                        subscriptionService.fetchFeePaymentResponseFuture(feePaymentRequestId).onComplete(feeResponsePromise);
//
//                        feeResponsePromise.future().onComplete(futureResponse -> {
//                            if (futureResponse.succeeded() && futureResponse.result() != null) {
//                                Document request = futureResponse.result();
//                                // Construct requestData with only emailId, orderObjectId, and amountPayable
//                                Document requestData = new Document();
//                                requestData.put("emailId", finalEmailId);
//                                requestData.put(NAME.getPropertyName(),request.getString(NAME.getPropertyName()));
//                                requestData.put(FEE_PAYMENT_ORDER_ID.getPropertyName(), ObjectIdUtil.convertObjectIdToString(request.getObjectId("_id")));
//                                requestData.put(AMOUNT_PAYABLE.getPropertyName(), request.getDouble(AMOUNT_PAYABLE.getPropertyName()));
//                                JsonObject jsonPayload = new JsonObject(requestData.toJson());
//                                LOGGER.debug("Fee payment payload for emailID {}: {}", finalEmailId, jsonPayload);
//                                System.out.println("payload: " + jsonPayload);
//
////                                RedisAPI redis = RedisAPI.api(redisCommandConnection);
////                                Promise<String> redisPromise = Promise.promise();
////                                vertx.setTimer(TIMEOUT_MS, id -> {
////                                    if (!redisPromise.future().isComplete()) {
////                                        redisPromise.fail(new TimeoutException("Redis config fetch timed out"));
////                                    }
////                                });
////                                redis.hget(redisHashKey, "payment-server-config").onComplete(result -> {
////                                    if (result.succeeded()) {
////                                        redisPromise.complete(result.result() != null ? result.result().toString() : null);
////                                    } else {
////                                        redisPromise.fail(result.cause());
////                                    }
////                                });
////
////                                redisPromise.future().onComplete(configAr -> {
////                                    if (configAr.succeeded() && configAr.result() != null) {
////                                        String paymentServerConfig = configAr.result();
////                                        try {
////                                            JsonObject config = new JsonObject(paymentServerConfig);
////                                            String ip = config.getString("ip");
////                                            int port = config.getInteger("port");
////                                            String createFeeSettingsOrderRoot = config.getString("create_fee_settings_order_root");
////                                            LOGGER.debug("Payment server config - ip: {}, port: {}, root: {}", ip, port, createFeeSettingsOrderRoot);
////
////                                            Promise<JsonObject> paymentPromise = Promise.promise();
////                                            vertx.setTimer(TIMEOUT_MS, id -> {
////                                                if (!paymentPromise.future().isComplete()) {
////                                                    paymentPromise.fail(new TimeoutException("Payment server request timed out"));
////                                                }
////                                            });
////                                            CreateHttpClientUtil.sendJsonPostRequest(webClient, ip, createFeeSettingsOrderRoot, jsonPayload, port).onComplete(paymentPromise);
////
////                                            paymentPromise.future().onComplete(result -> {
////                                                if (result.succeeded() && result.result() != null) {
////                                                    JsonObject resultData = result.result();
////                                                    JsonObject responseData = resultData.getJsonObject("data", new JsonObject())
////                                                            .getJsonObject("responseData", new JsonObject());
////                                                    LOGGER.info("Payment processed successfully for emailID: {}", finalEmailId);
////                                                    ResponseUtil.createResponse(
////                                                            routingContext.response(),
////                                                            ResponseType.SUCCESS,
////                                                            StatusCode.TWOHUNDRED,
////                                                            new JsonArray().add(responseData.getJsonArray("data") != null && !responseData.getJsonArray("data").isEmpty() ? responseData.getJsonArray("data").getJsonObject(0) : new JsonObject()),
////                                                            new JsonArray().add(responseData.getJsonArray("message") != null && !responseData.getJsonArray("message").isEmpty() ? responseData.getJsonArray("message").getString(0) : "Subscription and payment processed successfully")
////                                                    );
////                                                } else {
////                                                    LOGGER.error("Payment server request failed for emailID {}: {}", finalEmailId, result.cause() != null ? result.cause().getMessage() : "Unknown error");
////                                                    ResponseUtil.createResponse(
////                                                            routingContext.response(),
////                                                            ResponseType.ERROR,
////                                                            StatusCode.INTERNAL_SERVER_ERROR,
////                                                            new JsonArray(),
////                                                            new JsonArray().add("Failed to process payment request: " + (result.cause() != null ? result.cause().getMessage() : "Unknown error"))
////                                                    );
////                                                }
////                                            });
////                                        } catch (Exception e) {
////                                            LOGGER.error("Error parsing payment server config JSON for emailID {}: {}", finalEmailId, e.getMessage());
////                                            ResponseUtil.createResponse(
////                                                    routingContext.response(),
////                                                    ResponseType.ERROR,
////                                                    StatusCode.INTERNAL_SERVER_ERROR,
////                                                    new JsonArray(),
////                                                    new JsonArray().add("Error parsing payment server configuration: " + e.getMessage())
////                                            );
////                                        }
////                                    } else {
////                                        LOGGER.error("Payment server config not found or Redis error for emailID {}: {}", finalEmailId, configAr.cause() != null ? configAr.cause().getMessage() : "Unknown error");
////                                        ResponseUtil.createResponse(
////                                                routingContext.response(),
////                                                ResponseType.ERROR,
////                                                StatusCode.INTERNAL_SERVER_ERROR,
////                                                new JsonArray(),
////                                                new JsonArray().add("Payment server configuration not found or Redis error: " + (configAr.cause() != null ? configAr.cause().getMessage() : "Unknown error"))
////                                        );
////                                    }
////                                });
////                            } else {
////                                LOGGER.error("Failed to fetch fee payment response for emailID {}: {}", finalEmailId, futureResponse.cause() != null ? futureResponse.cause().getMessage() : "Response is null");
////                                ResponseUtil.createResponse(
////                                        routingContext.response(),
////                                        ResponseType.ERROR,
////                                        StatusCode.INTERNAL_SERVER_ERROR,
////                                        new JsonArray(),
////                                        new JsonArray().add("Failed to fetch fee payment response: " + (futureResponse.cause() != null ? futureResponse.cause().getMessage() : "Response is null"))
////                                );
////                            }
////                        });
////                    } else {
////                        LOGGER.error("Failed to save fee payment request for emailID {}: {}", finalEmailId, ar.cause() != null ? ar.cause().getMessage() : "Result is null");
////                        ResponseUtil.createResponse(
////                                routingContext.response(),
////                                ResponseType.ERROR,
////                                StatusCode.INTERNAL_SERVER_ERROR,
////                                new JsonArray(),
////                                new JsonArray().add("Failed to save fee payment request: " + (ar.cause() != null ? ar.cause().getMessage() : "Result is null"))
////                        );
////                    }
////                });
////            }).onFailure(error -> {
////                LOGGER.error("Failed to process subscription for emailID {}: {}", finalEmailId, error.getMessage());
////                String errorMessage = extractErrorMessage(error);
////                StatusCode statusCode = determineStatusCode(errorMessage);
////                ResponseType responseType = statusCode == StatusCode.INTERNAL_SERVER_ERROR ? ResponseType.ERROR : ResponseType.VALIDATION;
////                ResponseUtil.createResponse(
////                        routingContext.response(),
////                        responseType,
////                        statusCode,
////                        new JsonArray(),
////                        new JsonArray().add(
////                                new JsonObject()
////                                        .put("message", "Failed to process subscription")
////                                        .put("details", errorMessage)
////                        )
////                );
////            });
////        } catch (Exception e) {
////            LOGGER.error("Unexpected error processing subscription for emailID: {}", routingContext.request().getParam("emailId"), e);
////            ResponseUtil.createResponse(
////                    routingContext.response(),
////                    ResponseType.ERROR,
////                    StatusCode.INTERNAL_SERVER_ERROR,
////                    new JsonArray(),
////                    new JsonArray().add(
////                            new JsonObject()
////                                    .put("message", "Unexpected error processing subscription")
////                                    .put("details", e.getMessage())
////                    )
////            );
////        }
////    }
//                                // webclient API
//                                RedisAPI redis = RedisAPI.api(redisCommandConnection);
//                                redis.hget(redisHashKey, "payment-server-config").onComplete(configAr -> {
//                                    if (configAr.succeeded()) {
//                                        String paymentServerConfig = configAr.result().toString();
//                                        if (paymentServerConfig != null && !paymentServerConfig.isEmpty()) {
//                                            try {
//                                                JsonObject config = new JsonObject(paymentServerConfig);
//
//                                                String ip = config.getString("ip");
//                                                int port = config.getInteger("port");
//                                                String createFeeSettingsOrderRoot = config.getString("create_fee_settings_order_root");
//                                                Future<JsonObject> jsonResponse = CreateHttpClientUtil.sendJsonPostRequest(
//                                                        webClient,
//                                                        ip,
//                                                        createFeeSettingsOrderRoot,
//                                                        jsonPayload,
//                                                        port
//                                                );
//
//                                                jsonResponse.onComplete(result -> {
//                                                    if (result.succeeded()) {
//                                                        JsonObject resultData = result.result();
//                                                        JsonObject responseData = resultData.getJsonObject("data")
//                                                                .getJsonObject("responseData");
//
//                                                        // Directly send the response data back to the UI
//                                                        ResponseUtil.createResponse(
//                                                                response,
//                                                                ResponseType.SUCCESS,
//                                                                StatusCode.TWOHUNDRED,
//                                                                new JsonArray().add(responseData.getJsonArray("data") != null && !responseData.getJsonArray("data").isEmpty() ? responseData.getJsonArray("data").getJsonObject(0) : new JsonObject()),
//                                                                new JsonArray().add(responseData.getJsonArray("message") != null && !responseData.getJsonArray("message").isEmpty() ? responseData.getJsonArray("message").getString(0) : new JsonObject())
//                                                        );
//
//                                                    } else {
//                                                        LOGGER.error("Operation failed: " + result.cause().getMessage());
//                                                        ResponseUtil.createResponse(
//                                                                response,
//                                                                ResponseType.ERROR,
//                                                                StatusCode.INTERNAL_SERVER_ERROR,
//                                                                new JsonArray(),
//                                                                new JsonArray().add("Failed to process request")
//                                                        );
//                                                    }
//                                                });
//                                            } catch (DecodeException e) {
//                                                LOGGER.error("Error parsing payment server config JSON: " + e.getMessage());
//                                                ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.INTERNAL_SERVER_ERROR,
//                                                        new JsonArray(), new JsonArray().add("Error parsing payment server configuration"));
//                                            }
//                                        } else {
//                                            LOGGER.error("Payment server config not found or empty");
//                                            ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.INTERNAL_SERVER_ERROR,
//                                                    new JsonArray(), new JsonArray().add("Payment server configuration not found"));
//                                        }
//                                    } else {
//                                        LOGGER.error("Error fetching payment-server-config from Redis: {}", ar.cause().getMessage());
//                                        ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.INTERNAL_SERVER_ERROR,
//                                                new JsonArray(), new JsonArray().add("Error fetching payment server configuration"));
//                                    }
//                                });
//
//
//                            }
//                        });
//                    }
//                });
//
//            } else {
//                LOGGER.error(" Fee Payment Request is null");
//                ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.BAD_REQUEST,
//                        new JsonArray(), new JsonArray().add("Fee Payment Details is Empty"));
//
//            }
//
//        } catch (Exception e) {
//            LOGGER.error("Error while Creating fee settings payment :{}", e.getMessage(), e);
//            throw new RuntimeException("Failed to generate fee setting order", e);
//        }
//    }


public void handleSubscriptionRequest(RoutingContext routingContext) {
    Vertx vertx = routingContext.vertx();
    try {
        String emailId = routingContext.request().getParam("emailId");
        if (emailId == null || emailId.trim().isEmpty()) {
            LOGGER.warn("Missing or empty emailId parameter");
            ResponseUtil.createResponse(
                    routingContext.response(),
                    ResponseType.VALIDATION,
                    StatusCode.BAD_REQUEST,
                    new JsonArray(),
                    new JsonArray().add("Email ID is required")
            );
            return;
        }
        emailId = emailId.toUpperCase();

        JsonObject requestBody = routingContext.getBodyAsJson();
        HttpServerResponse response = routingContext.response();
//        JsonObject transformedBody = new JsonObject();
//        // Map serviceOid if present
//        if (requestBody.containsKey(SERVICE_OID.getPropertyName())) {
//            transformedBody.put(SERVICE_OID.getPropertyName(), requestBody.getString(SERVICE_OID.getPropertyName()));
//        }
//        // Map preferredStartDate_Gym_Date to preferredStartDate
//        if (requestBody.containsKey("preferredStartDate_Gym_Date")) {
//            String preferredStartDate = requestBody.getString("preferredStartDate_Gym_Date");
//            transformedBody.put("preferredStartDate", preferredStartDate);
//        }
//        // Map subscr1ptionAddonList_Gym_ObjectIdArxay to subscriptionAddonList
//        if (requestBody.containsKey(SUBSCRIPTION_ADDON_LIST.getPropertyName())) {
//            JsonArray addonIds = requestBody.getJsonArray(SUBSCRIPTION_ADDON_LIST.getPropertyName(), new JsonArray());
//            JsonArray subscriptionAddonList = new JsonArray();
//            for (int i = 0; i < addonIds.size(); i++) {
//                String addonId = addonIds.getString(i);
//                if (addonId == null || !ObjectId.isValid(addonId)) {
//                    LOGGER.warn("Invalid add-on ID in subscriptionAddonList_Gym_ObjectIdArray for emailID: {}", emailId);
//                    ResponseUtil.createResponse(
//                            routingContext.response(),
//                            ResponseType.VALIDATION,
//                            StatusCode.BAD_REQUEST,
//                            new JsonArray(),
//                            new JsonArray().add("Invalid add-on ID: " + addonId)
//                    );
//                    return;
//                }
//                JsonObject addonEntry = new JsonObject().put(ADDON_OID.getPropertyName(), addonId);
//                subscriptionAddonList.add(addonEntry);
//            }
//            transformedBody.put(SUBSCRIPTION_ADDON_LIST.getPropertyName(), subscriptionAddonList);
//        }
//
//        // Validate that at least one of serviceOid or subscriptionAddonList is present
//        if (!transformedBody.containsKey(SERVICE_OID.getPropertyName()) && !transformedBody.containsKey(SUBSCRIPTION_ADDON_LIST.getPropertyName())) {
//            LOGGER.warn("No service or add-ons provided in the request for emailID: {}", emailId);
//            ResponseUtil.createResponse(
//                    routingContext.response(),
//                    ResponseType.VALIDATION,
//                    StatusCode.BAD_REQUEST,
//                    new JsonArray(),
//                    new JsonArray().add("At least a service or add-ons are required")
//            );
//            return;
//        }

//        if (requestBody == null || requestBody.isEmpty()) {
//            LOGGER.warn("Missing or empty request body for emailID: {}", emailId);
//            ResponseUtil.createResponse(
//                    response,
//                    ResponseType.VALIDATION,
//                    StatusCode.BAD_REQUEST,
//                    new JsonArray(),
//                    new JsonArray().add("Request body is required")
//            );
//            return;
//        }
        List<String> requiredFields = Arrays.asList(
//                EMAIL_ID.getPropertyName()

//                APPLICATION_NUMBER.getPropertyName(),
//                LIST_OF_FEE_STRUCTURE.getPropertyName(),
//                PROGRAM_NAME.getPropertyName(),
//                EDUCATION_TYPE.getPropertyName(),
//                BATCH_CODE.getPropertyName(),
//                APPLICANT_AUTH_OBJECTID.getPropertyName()
        );

        // Parse and validate the request document
        Document paramsDocument = Document.parse(requestBody.toString());
        JsonArray validationResponse = DocumentParser.validateAndCleanDocument(paramsDocument, requiredFields);

        // Check validation response
        if (!validationResponse.isEmpty()) {
            ResponseUtil.createResponse(
                    response,
                    ResponseType.VALIDATION,
                    StatusCode.BAD_REQUEST,
                    new JsonArray(),
                    validationResponse
            );
            return;
        }

        LOGGER.debug("Processing subscription request for emailID: {}", emailId);
        Promise<Document> subscriptionPromise = Promise.promise();
        vertx.setTimer(TIMEOUT_MS, id -> {
            if (!subscriptionPromise.future().isComplete()) {
                subscriptionPromise.fail(new TimeoutException("Subscription processing timed out"));
            }
        });
        subscriptionService.addSubscription(emailId, requestBody).onComplete(subscriptionPromise);

        ///  for object id array --uncomment
//        subscriptionService.addSubscription(emailId, transformedBody).onComplete(subscriptionPromise);
        String finalEmailId = emailId;
        subscriptionPromise.future().onSuccess(memberDetails -> {
            LOGGER.info("Subscription processed successfully for emailID: {}", finalEmailId);
            if (memberDetails == null) {
                LOGGER.error("Member details are null for emailID: {}", finalEmailId);
                ResponseUtil.createResponse(
                        response,
                        ResponseType.ERROR,
                        StatusCode.INTERNAL_SERVER_ERROR,
                        new JsonArray(),
                        new JsonArray().add("Subscription details are empty")
                );
                return;
            }
            System.out.println("member details=="+memberDetails);
            // Process fee payment request
            Promise<ObjectId> feePaymentPromise = Promise.promise();
            vertx.setTimer(TIMEOUT_MS, id -> {
                if (!feePaymentPromise.future().isComplete()) {
                    feePaymentPromise.fail(new TimeoutException("Fee payment request save timed out"));
                }
            });
            subscriptionService.saveFeePaymentRequestFuture(memberDetails).onComplete(feePaymentPromise);

            feePaymentPromise.future().onComplete(ar -> {
                if (ar.succeeded() && ar.result() != null) {
                    ObjectId feePaymentRequestId = ar.result();
                    System.out.println("feePaymentRequestId"+feePaymentRequestId);
                    LOGGER.debug("Fee payment request saved with ID: {} for emailID: {}", feePaymentRequestId, finalEmailId);
                    Promise<Document> feeResponsePromise = Promise.promise();
                    vertx.setTimer(TIMEOUT_MS, id -> {
                        if (!feeResponsePromise.future().isComplete()) {
                            feeResponsePromise.fail(new TimeoutException("Fee payment response fetch timed out"));
                        }
                    });
                    subscriptionService.fetchFeePaymentResponseFuture(feePaymentRequestId).onComplete(feeResponsePromise);
//                    JsonObject requestData = new JsonObject();

                    feeResponsePromise.future().onComplete(futureResponse -> {
                        if (futureResponse.succeeded() && futureResponse.result() != null) {
                            Document request = futureResponse.result();
                            System.out.println("document request"+request);
                            Document requestData = new Document();
                            // Construct requestData with only emailId, orderObjectId, and amountPayable
                            requestData.put("emailId_Gym_Text", finalEmailId);
                            requestData.put("feePaymentMode_FeeModule_Text","RAZORPAY");
                            requestData.put(NAME.getPropertyName(), request.getString(NAME.getPropertyName()));
//                            requestData.put(FEE_PAYMENT_ORDER_ID.getPropertyName(), ObjectIdUtil.convertObjectIdToString(request.getObjectId("_id")));
                            requestData.put(FEE_PAYMENT_ORDER_ID.getPropertyName(), feePaymentRequestId);
                            requestData.put(AMOUNT_PAYABLE.getPropertyName(), request.getDouble(AMOUNT_PAYABLE.getPropertyName()));
                            System.out.println("request data"+requestData);
                            JsonObject jsonPayload = new JsonObject(requestData.toJson());

                            LOGGER.debug("Fee payment payload for emailID {}: {}", finalEmailId, requestData);
//            subscriptionService.saveFeePaymentRequestFuture(memberDetails).onComplete(ar -> {
//                if (ar.succeeded() && ar.result() != null) {
//                    ObjectId feePaymentRequestId = ar.result();
//                    LOGGER.debug("Fee payment request saved with ID: {} for emailID: {}", feePaymentRequestId, finalEmailId);
//
//                    subscriptionService.fetchFeePaymentResponseFuture(feePaymentRequestId).onComplete(futureResponse -> {
//                        if (futureResponse.succeeded() && futureResponse.result() != null) {
//                            Document request = futureResponse.result();
//                            Document requestData = new Document(request);
//
//                            requestData.put("emailId", finalEmailId);
//                            requestData.put("feePaymentMode_FeeModule_Text", "RAZORPAY");
//                            requestData.put(NAME.getPropertyName(), request.getString(NAME.getPropertyName()));
//                            requestData.put(FEE_PAYMENT_ORDER_ID.getPropertyName(), ObjectIdUtil.convertObjectIdToString(request.getObjectId("_id")));
//                            requestData.put(AMOUNT_PAYABLE.getPropertyName(), request.getDouble(AMOUNT_PAYABLE.getPropertyName()));
//
//                            JsonObject jsonPayload = new JsonObject(requestData.toJson());
//                            System.out.println("paAYLOAD"+jsonPayload);
//                            LOGGER.debug("Fee payment payload for emailID {}: {}", finalEmailId, requestData);

                            // Use Redis to get payment server configuration
                            RedisAPI redis = RedisAPI.api(redisCommandConnection);
                            redis.hget(redisHashKey, "payment-server-config").onComplete(configAr -> {

                                if (configAr.succeeded() && configAr.result() != null) {
                                    String paymentServerConfig = configAr.result().toString();
                                    if (paymentServerConfig != null && !paymentServerConfig.isEmpty()) {
                                        try {
                                            JsonObject config = new JsonObject(paymentServerConfig);
                                            String ip = config.getString("ip");
                                            int port = config.getInteger("port");
                                            String createFeeSettingsOrderRoot = config.getString("create_gym_order_root");
                                            LOGGER.debug("Payment server config - ip: {}, port: {}, root: {}", ip, port, createFeeSettingsOrderRoot);

                                            System.out.println(requestData);
                                            Future<JsonObject> jsonResponse = CreateHttpClientUtil.sendJsonPostRequest(
                                                    webClient,
                                                    ip,
                                                    createFeeSettingsOrderRoot,
                                                    jsonPayload,
                                                    port
                                            );

                                            jsonResponse.onComplete(result -> {
                                                if (result.succeeded() && result.result() != null) {
                                                    JsonObject resultData = result.result();
                                                    JsonObject responseData = resultData.getJsonObject("data", new JsonObject())
                                                            .getJsonObject("responseData", new JsonObject());

                                                    // Directly send the response data back to the UI
                                                    ResponseUtil.createResponse(
                                                            response,
                                                            ResponseType.SUCCESS,
                                                            StatusCode.TWOHUNDRED,
                                                            new JsonArray().add(responseData.getJsonArray("data") != null && !responseData.getJsonArray("data").isEmpty() ? responseData.getJsonArray("data").getJsonObject(0) : new JsonObject()),
                                                            new JsonArray().add(responseData.getJsonArray("message") != null && !responseData.getJsonArray("message").isEmpty() ? responseData.getJsonArray("message").getString(0) : "Subscription and payment processed successfully")
                                                    );
                                                } else {
                                                    LOGGER.error("Operation failed: {}", result.cause() != null ? result.cause().getMessage() : "Unknown error");
                                                    ResponseUtil.createResponse(
                                                            response,
                                                            ResponseType.ERROR,
                                                            StatusCode.INTERNAL_SERVER_ERROR,
                                                            new JsonArray(),
                                                            new JsonArray().add("Failed to process payment request: " + (result.cause() != null ? result.cause().getMessage() : "Unknown error"))
                                                    );
                                                }
                                            });
                                        } catch (DecodeException e) {
                                            LOGGER.error("Error parsing payment server config JSON for emailID {}: {}", finalEmailId, e.getMessage());
                                            ResponseUtil.createResponse(
                                                    response,
                                                    ResponseType.ERROR,
                                                    StatusCode.INTERNAL_SERVER_ERROR,
                                                    new JsonArray(),
                                                    new JsonArray().add("Error parsing payment server configuration: " + e.getMessage())
                                            );
                                        }
                                    } else {
                                        LOGGER.error("Payment server config not found or empty for emailID: {}", finalEmailId);
                                        ResponseUtil.createResponse(
                                                response,
                                                ResponseType.ERROR,
                                                StatusCode.INTERNAL_SERVER_ERROR,
                                                new JsonArray(),
                                                new JsonArray().add("Payment server configuration not found or empty")
                                        );
                                    }
                                } else {
                                    LOGGER.error("Error fetching payment-server-config from Redis for emailID {}: {}",
                                            finalEmailId, configAr.cause() != null ? configAr.cause().getMessage() : "Unknown error");
                                    ResponseUtil.createResponse(
                                            response,
                                            ResponseType.ERROR,
                                            StatusCode.INTERNAL_SERVER_ERROR,
                                            new JsonArray(),
                                            new JsonArray().add("Error fetching payment server configuration: " +
                                                    (configAr.cause() != null ? configAr.cause().getMessage() : "Unknown error"))
                                    );
                                }
                            });
                        } else {
                            LOGGER.error("Failed to fetch fee payment response for emailID {}: {}",
                                    finalEmailId, futureResponse.cause() != null ? futureResponse.cause().getMessage() : "Response is null");
                            ResponseUtil.createResponse(
                                    response,
                                    ResponseType.ERROR,
                                    StatusCode.INTERNAL_SERVER_ERROR,
                                    new JsonArray(),
                                    new JsonArray().add("Failed to fetch fee payment response: " +
                                            (futureResponse.cause() != null ? futureResponse.cause().getMessage() : "Response is null"))
                            );
                        }
                    });
                } else {
                    LOGGER.error("Fee Payment Request is null or failed for emailID {}: {}",
                            finalEmailId, ar.cause() != null ? ar.cause().getMessage() : "Unknown error");
                    ResponseUtil.createResponse(
                            response,
                            ResponseType.ERROR,
                            StatusCode.BAD_REQUEST,
                            new JsonArray(),
                            new JsonArray().add("Fee Payment Details is Empty")
                    );
                }
            });
        }).onFailure(error -> {
            LOGGER.error("Failed to process subscription for emailID {}: {}", finalEmailId, error.getMessage());
            String errorMessage = extractErrorMessage(error);
            StatusCode statusCode = determineStatusCode(errorMessage);
            ResponseType responseType = statusCode == StatusCode.INTERNAL_SERVER_ERROR ? ResponseType.ERROR : ResponseType.VALIDATION;
            ResponseUtil.createResponse(
                    response,
                    responseType,
                    statusCode,
                    new JsonArray(),
                    new JsonArray().add(
                            new JsonObject()
                                    .put("message", "Failed to process subscription")
                                    .put("details", errorMessage)
                    )
            );
        });
    } catch (Exception e) {
        LOGGER.error("Error while processing subscription request: {}", e.getMessage(), e);
        ResponseUtil.createResponse(
                routingContext.response(),
                ResponseType.ERROR,
                StatusCode.INTERNAL_SERVER_ERROR,
                new JsonArray(),
                new JsonArray().add(
                        new JsonObject()
                                .put("message", "Unexpected error processing subscription")
                                .put("details", e.getMessage())
                )
        );
    }
}
    /**
     * Handles PUT requests to update user status.
     * Expects query params 'emailId' and 'status' (Active/Inactive).
     */
    public void updateUserStatus(RoutingContext routingContext) {
        Vertx vertx = routingContext.vertx();
        try {
            String emailId = routingContext.request().getParam("emailId");
            String statusText = routingContext.request().getParam("status");

            if (emailId == null || emailId.trim().isEmpty()) {
                LOGGER.warn("Missing or empty emailId parameter");
                ResponseUtil.createResponse(
                        routingContext.response(),
                        ResponseType.VALIDATION,
                        StatusCode.BAD_REQUEST,
                        new JsonArray(),
                        new JsonArray().add("Email ID is required")
                );
                return;
            }

            if (statusText == null || statusText.trim().isEmpty()) {
                LOGGER.warn("Missing or empty status parameter for emailID: {}", emailId);
                ResponseUtil.createResponse(
                        routingContext.response(),
                        ResponseType.VALIDATION,
                        StatusCode.BAD_REQUEST,
                        new JsonArray(),
                        new JsonArray().add("Status is required")
                );
                return;
            }

            LOGGER.debug("Updating status to '{}' for emailID: {}", statusText, emailId);
            Promise<Void> statusPromise = Promise.promise();
            vertx.setTimer(TIMEOUT_MS, id -> {
                if (!statusPromise.future().isComplete()) {
                    statusPromise.fail(new TimeoutException("Update status timed out"));
                }
            });
            subscriptionService.updateUserStatus(emailId, statusText).onComplete(statusPromise);

            statusPromise.future().onSuccess(v -> {
                LOGGER.info("Status updated successfully for emailID: {}", emailId);
                ResponseUtil.createResponse(
                        routingContext.response(),
                        ResponseType.SUCCESS,
                        StatusCode.TWOHUNDRED,
                        new JsonArray(),
                        new JsonArray().add("User status updated successfully")
                );
            }).onFailure(error -> {
                LOGGER.error("Failed to update status for emailID {}: {}", emailId, error.getMessage());
                String errorMessage = extractErrorMessage(error);
                StatusCode statusCode = determineStatusCode(errorMessage);
                ResponseType responseType = statusCode == StatusCode.INTERNAL_SERVER_ERROR ? ResponseType.ERROR : ResponseType.VALIDATION;
                ResponseUtil.createResponse(
                        routingContext.response(),
                        responseType,
                        statusCode,
                        new JsonArray(),
                        new JsonArray().add(
                                new JsonObject()
                                        .put("message", "Failed to update user status")
                                        .put("details", errorMessage)
                        )
                );
            });
        } catch (Exception e) {
            LOGGER.error("Unexpected error updating status for emailID: {}", routingContext.request().getParam("emailId"), e);
            ResponseUtil.createResponse(
                    routingContext.response(),
                    ResponseType.ERROR,
                    StatusCode.INTERNAL_SERVER_ERROR,
                    new JsonArray(),
                    new JsonArray().add(
                            new JsonObject()
                                    .put("message", "Unexpected error updating user status")
                                    .put("details", e.getMessage())
                    )
            );
        }
    }

    /**
     * Handles GET requests to retrieve subscription details.
     * Expects query param 'emailId'.
     */
    public void getCurrentSubscriptionDetails(RoutingContext routingContext) {
        Vertx vertx = routingContext.vertx();
        try {
            String emailId = routingContext.request().getParam("emailId");
            if (emailId == null || emailId.trim().isEmpty()) {
                LOGGER.warn("Missing or empty emailId parameter");
                ResponseUtil.createResponse(
                        routingContext.response(),
                        ResponseType.VALIDATION,
                        StatusCode.BAD_REQUEST,
                        new JsonArray(),
                        new JsonArray().add("Email ID is required")
                );
                return;
            }

            LOGGER.debug("Retrieving subscription details for emailID: {}", emailId);
            Promise<JsonObject> detailsPromise = Promise.promise();
            vertx.setTimer(TIMEOUT_MS, id -> {
                if (!detailsPromise.future().isComplete()) {
                    detailsPromise.fail(new TimeoutException("Get subscription details timed out"));
                }
            });
            subscriptionService.getCurrentSubscriptionDetails(emailId).onComplete(detailsPromise);

            detailsPromise.future().onSuccess(result -> {
                LOGGER.info("Subscription details retrieved successfully for emailID: {}", emailId);
                ResponseUtil.createResponse(
                        routingContext.response(),
                        ResponseType.SUCCESS,
                        StatusCode.TWOHUNDRED,
                        new JsonArray().add(result),
                        new JsonArray().add("Subscription details retrieved successfully")
                );
            }).onFailure(error -> {
                LOGGER.error("Failed to retrieve subscription details for emailID {}: {}", emailId, error.getMessage());
                String errorMessage = extractErrorMessage(error);
                StatusCode statusCode = determineStatusCode(errorMessage);
                ResponseType responseType = statusCode == StatusCode.INTERNAL_SERVER_ERROR ? ResponseType.ERROR : ResponseType.VALIDATION;
                ResponseUtil.createResponse(
                        routingContext.response(),
                        responseType,
                        statusCode,
                        new JsonArray(),
                        new JsonArray().add(
                                new JsonObject()
                                        .put("message", "Failed to retrieve subscription details")
                                        .put("details", errorMessage)
                        )
                );
            });
        } catch (Exception e) {
            LOGGER.error("Unexpected error retrieving subscription details for emailID: {}", routingContext.request().getParam("emailId"), e);
            ResponseUtil.createResponse(
                    routingContext.response(),
                    ResponseType.ERROR,
                    StatusCode.INTERNAL_SERVER_ERROR,
                    new JsonArray(),
                    new JsonArray().add(
                            new JsonObject()
                                    .put("message", "Unexpected error retrieving subscription details")
                                    .put("details", e.getMessage())
                    )
            );
        }
    }

    /**
     * Extracts the error message from a Throwable, handling cases where the message is a stringified Document.
     */
    private String extractErrorMessage(Throwable error) {
        String message = error.getMessage();
        if (message != null) {
            try {
                Document errorDoc = Document.parse(message);
                return errorDoc.getString("error") != null ? errorDoc.getString("error") : message;
            } catch (Exception e) {
                // If parsing fails, return the raw message
                return message;
            }
        }
        return "Unknown error";
    }

    /**
     * Determines the appropriate status code based on the error message.
     */
    private StatusCode determineStatusCode(String errorMessage) {
        if (errorMessage.contains("does not exist") || errorMessage.contains("No active or future subscription found")) {
            return StatusCode.FILE_NOT_FOUND;
        } else if (errorMessage.contains("MongoDB error")) {
            return StatusCode.INTERNAL_SERVER_ERROR;
        }
        return StatusCode.BAD_REQUEST;
    }
}

//
//public class SubscriptionHandler {
//    private static final Logger LOGGER = LogManager.getLogger(SubscriptionHandler.class);
//    private final SubscriptionService subscriptionService;
//    private final WebClient webClient;
//    private final Redis redisCommandConnection;
//    private final String redisHashKey;
//    private static final long TIMEOUT_MS = 10000; // 10 seconds timeout for async operations
//
//    public SubscriptionHandler(SubscriptionService subscriptionService, Redis redisCommandConnection, String redisHashKey, WebClient webClient) {
//        this.subscriptionService = subscriptionService;
//        this.webClient = webClient;
//        this.redisCommandConnection = redisCommandConnection;
//        this.redisHashKey = redisHashKey;
//    }
//
//    /**
//     * Handles POST requests to add a subscription (service and/or add-ons).
//     * Expects query param 'emailId' and a JSON body with 'serviceOid', 'preferredStartDate', and optional 'subscriptionAddOnList'.
//     */
//    public void handleSubscriptionRequest(RoutingContext routingContext) {
//        Vertx vertx = routingContext.vertx();
//        try {
//            String emailId = routingContext.request().getParam("emailId");
//            if (emailId == null || emailId.trim().isEmpty()) {
//                LOGGER.warn("Missing or empty emailId parameter");
//                ResponseUtil.createResponse(
//                        routingContext.response(),
//                        ResponseType.VALIDATION,
//                        StatusCode.BAD_REQUEST,
//                        new JsonArray(),
//                        new JsonArray().add("Email ID is required")
//                );
//                return;
//            }
//            emailId = emailId.toUpperCase();
//
//            JsonObject requestBody = routingContext.getBodyAsJson();
//            HttpServerResponse response = routingContext.response();
//
//            if (requestBody == null || requestBody.isEmpty()) {
//                LOGGER.warn("Missing or empty request body for emailID: {}", emailId);
//                ResponseUtil.createResponse(
//                        routingContext.response(),
//                        ResponseType.VALIDATION,
//                        StatusCode.BAD_REQUEST,
//                        new JsonArray(),
//                        new JsonArray().add("Request body is required")
//                );
//                return;
//            }
//
//            LOGGER.debug("Processing subscription request for emailID: {}", emailId);
//            Promise<Document> subscriptionPromise = Promise.promise();
//            vertx.setTimer(TIMEOUT_MS, id -> {
//                if (!subscriptionPromise.future().isComplete()) {
//                    subscriptionPromise.fail(new TimeoutException("Subscription processing timed out"));
//                }
//            });
//            subscriptionService.addSubscription(emailId, requestBody).onComplete(subscriptionPromise);
//
//            String finalEmailId = emailId;
//            subscriptionPromise.future().onSuccess(memberDetails -> {
//                LOGGER.info("Subscription processed successfully for emailID: {}", finalEmailId);
//                if (memberDetails == null) {
//                    LOGGER.error("Member details are null for emailID: {}", finalEmailId);
//                    ResponseUtil.createResponse(
//                            response,
//                            ResponseType.ERROR,
//                            StatusCode.INTERNAL_SERVER_ERROR,
//                            new JsonArray(),
//                            new JsonArray().add("Subscription details are empty")
//                    );
//                    return;
//                }
//
//                // Process fee payment request
//                Promise<ObjectId> feePaymentPromise = Promise.promise();
//                vertx.setTimer(TIMEOUT_MS, id -> {
//                    if (!feePaymentPromise.future().isComplete()) {
//                        feePaymentPromise.fail(new TimeoutException("Fee payment request save timed out"));
//                    }
//                });
//                subscriptionService.saveFeePaymentRequestFuture(memberDetails).onComplete(feePaymentPromise);
//
//                feePaymentPromise.future().onComplete(ar -> {
//                    if (ar.succeeded() && ar.result() != null) {
//                        ObjectId feePaymentRequestId = ar.result();
//                        LOGGER.debug("Fee payment request saved with ID: {} for emailID: {}", feePaymentRequestId, finalEmailId);
//                        Promise<Document> feeResponsePromise = Promise.promise();
//                        vertx.setTimer(TIMEOUT_MS, id -> {
//                            if (!feeResponsePromise.future().isComplete()) {
//                                feeResponsePromise.fail(new TimeoutException("Fee payment response fetch timed out"));
//                            }
//                        });
//                        subscriptionService.fetchFeePaymentResponseFuture(feePaymentRequestId).onComplete(feeResponsePromise);
//
//                        feeResponsePromise.future().onComplete(futureResponse -> {
//                            if (futureResponse.succeeded() && futureResponse.result() != null) {
//                                Document request = futureResponse.result();
//                                // Construct requestData with only emailId, orderObjectId, and amountPayable
//                                Document requestData = new Document();
//                                requestData.put("emailId", finalEmailId);
//                                requestData.put(NAME.getPropertyName(), request.getString(NAME.getPropertyName()));
//                                requestData.put(FEE_PAYMENT_ORDER_ID.getPropertyName(), ObjectIdUtil.convertObjectIdToString(request.getObjectId("_id")));
//                                requestData.put(AMOUNT_PAYABLE.getPropertyName(), request.getDouble(AMOUNT_PAYABLE.getPropertyName()));
//                                JsonObject jsonPayload = new JsonObject(requestData.toJson());
//                                LOGGER.debug("Fee payment payload for emailID {}: {}", finalEmailId, jsonPayload);
//
//                                // Use Redis to get payment server configuration
//                                RedisAPI redis = RedisAPI.api(redisCommandConnection);
//                                redis.hget(redisHashKey, "payment-server-config").onComplete(configAr -> {
//                                    if (configAr.succeeded() && configAr.result() != null) {
//                                        String paymentServerConfig = configAr.result().toString();
//                                        if (paymentServerConfig != null && !paymentServerConfig.isEmpty()) {
//                                            try {
//                                                JsonObject config = new JsonObject(paymentServerConfig);
//                                                String ip = config.getString("ip");
//                                                int port = config.getInteger("port");
//                                                String createFeeSettingsOrderRoot = config.getString("create_fee_settings_order_root");
//                                                LOGGER.debug("Payment server config - ip: {}, port: {}, root: {}", ip, port, createFeeSettingsOrderRoot);
//
//                                                Promise<JsonObject> paymentPromise = Promise.promise();
//                                                vertx.setTimer(TIMEOUT_MS, id -> {
//                                                    if (!paymentPromise.future().isComplete()) {
//                                                        paymentPromise.fail(new TimeoutException("Payment server request timed out"));
//                                                    }
//                                                });
//                                                CreateHttpClientUtil.sendJsonPostRequest(webClient, ip, createFeeSettingsOrderRoot, jsonPayload, port).onComplete(paymentPromise);
//
//                                                paymentPromise.future().onComplete(result -> {
//                                                    if (result.succeeded() && result.result() != null) {
//                                                        JsonObject resultData = result.result();
//                                                        JsonObject responseData = resultData.getJsonObject("data", new JsonObject())
//                                                                .getJsonObject("responseData", new JsonObject());
//                                                        LOGGER.info("Payment processed successfully for emailID: {}", finalEmailId);
//                                                        ResponseUtil.createResponse(
//                                                                response,
//                                                                ResponseType.SUCCESS,
//                                                                StatusCode.TWOHUNDRED,
//                                                                new JsonArray().add(responseData.getJsonArray("data") != null && !responseData.getJsonArray("data").isEmpty() ? responseData.getJsonArray("data").getJsonObject(0) : new JsonObject()),
//                                                                new JsonArray().add(responseData.getJsonArray("message") != null && !responseData.getJsonArray("message").isEmpty() ? responseData.getJsonArray("message").getString(0) : "Subscription and payment processed successfully")
//                                                        );
//                                                    } else {
//                                                        LOGGER.error("Payment server request failed for emailID {}: {}", finalEmailId, result.cause() != null ? result.cause().getMessage() : "Unknown error");
//                                                        ResponseUtil.createResponse(
//                                                                response,
//                                                                ResponseType.ERROR,
//                                                                StatusCode.INTERNAL_SERVER_ERROR,
//                                                                new JsonArray(),
//                                                                new JsonArray().add("Failed to process payment request: " + (result.cause() != null ? result.cause().getMessage() : "Unknown error"))
//                                                        );
//                                                    }
//                                                });
//                                            } catch (Exception e) {
//                                                LOGGER.error("Error parsing payment server config JSON for emailID {}: {}", finalEmailId, e.getMessage());
//                                                ResponseUtil.createResponse(
//                                                        response,
//                                                        ResponseType.ERROR,
//                                                        StatusCode.INTERNAL_SERVER_ERROR,
//                                                        new JsonArray(),
//                                                        new JsonArray().add("Error parsing payment server configuration: " + e.getMessage())
//                                                );
//                                            }
//                                        } else {
//                                            LOGGER.error("Payment server config is empty for emailID: {}", finalEmailId);
//                                            ResponseUtil.createResponse(
//                                                    response,
//                                                    ResponseType.ERROR,
//                                                    StatusCode.INTERNAL_SERVER_ERROR,
//                                                    new JsonArray(),
//                                                    new JsonArray().add("Payment server configuration is empty")
//                                            );
//                                        }
//                                    } else {
//                                        LOGGER.error("Payment server config not found or Redis error for emailID {}: {}", finalEmailId, configAr.cause() != null ? configAr.cause().getMessage() : "Unknown error");
//                                        ResponseUtil.createResponse(
//                                                response,
//                                                ResponseType.ERROR,
//                                                StatusCode.INTERNAL_SERVER_ERROR,
//                                                new JsonArray(),
//                                                new JsonArray().add("Payment server configuration not found or Redis error: " + (configAr.cause() != null ? configAr.cause().getMessage() : "Unknown error"))
//                                        );
//                                    }
//                                });
//                            } else {
//                                LOGGER.error("Failed to fetch fee payment response for emailID {}: {}", finalEmailId, futureResponse.cause() != null ? futureResponse.cause().getMessage() : "Response is null");
//                                ResponseUtil.createResponse(
//                                        response,
//                                        ResponseType.ERROR,
//                                        StatusCode.INTERNAL_SERVER_ERROR,
//                                        new JsonArray(),
//                                        new JsonArray().add("Failed to fetch fee payment response: " + (futureResponse.cause() != null ? futureResponse.cause().getMessage() : "Response is null"))
//                                );
//                            }
//                        });
//                    } else {
//                        LOGGER.error("Failed to save fee payment request for emailID {}: {}", finalEmailId, ar.cause() != null ? ar.cause().getMessage() : "Result is null");
//                        ResponseUtil.createResponse(
//                                response,
//                                ResponseType.ERROR,
//                                StatusCode.INTERNAL_SERVER_ERROR,
//                                new JsonArray(),
//                                new JsonArray().add("Failed to save fee payment request: " + (ar.cause() != null ? ar.cause().getMessage() : "Result is null"))
//                        );
//                    }
//                });
//            }).onFailure(error -> {
//                LOGGER.error("Failed to process subscription for emailID {}: {}", finalEmailId, error.getMessage());
//                String errorMessage = extractErrorMessage(error);
//                StatusCode statusCode = determineStatusCode(errorMessage);
//                ResponseType responseType = statusCode == StatusCode.INTERNAL_SERVER_ERROR ? ResponseType.ERROR : ResponseType.VALIDATION;
//                ResponseUtil.createResponse(
//                        response,
//                        responseType,
//                        statusCode,
//                        new JsonArray(),
//                        new JsonArray().add(
//                                new JsonObject()
//                                        .put("message", "Failed to process subscription")
//                                        .put("details", errorMessage)
//                        )
//                );
//            });
//        } catch (Exception e) {
//            LOGGER.error("Unexpected error processing subscription for emailID: {}", routingContext.request().getParam("emailId"), e);
//            ResponseUtil.createResponse(
//                    routingContext.response(),
//                    ResponseType.ERROR,
//                    StatusCode.INTERNAL_SERVER_ERROR,
//                    new JsonArray(),
//                    new JsonArray().add(
//                            new JsonObject()
//                                    .put("message", "Unexpected error processing subscription")
//                                    .put("details", e.getMessage())
//                    )
//            );
//        }
//    }
//
//    private String extractErrorMessage(Throwable error) {
//        if (error == null) {
//            return "Unknown error";
//        }
//        return error.getMessage() != null ? error.getMessage() : error.getClass().getSimpleName();
//    }
//
//    private StatusCode determineStatusCode(String errorMessage) {
//        if (errorMessage == null) {
//            return StatusCode.INTERNAL_SERVER_ERROR;
//        }
//
//        errorMessage = errorMessage.toLowerCase();
//        if (errorMessage.contains("not found") || errorMessage.contains("no such")) {
//            return StatusCode.NOT_FOUND;
//        } else if (errorMessage.contains("invalid") || errorMessage.contains("missing") ||
//                errorMessage.contains("required") || errorMessage.contains("validation")) {
//            return StatusCode.BAD_REQUEST;
//        } else {
//            return StatusCode.INTERNAL_SERVER_ERROR;
//        }
//    }
//}