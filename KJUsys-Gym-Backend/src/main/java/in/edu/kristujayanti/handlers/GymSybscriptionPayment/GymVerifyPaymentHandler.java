package in.edu.kristujayanti.handlers.GymSybscriptionPayment;

import in.edu.kristujayanti.enums.ResponseType;
import in.edu.kristujayanti.enums.StatusCode;
import in.edu.kristujayanti.services.UserServices.GymPaymentService;
import in.edu.kristujayanti.util.CreateHttpClientUtil;
import in.edu.kristujayanti.util.DateUtils;
import in.edu.kristujayanti.util.ResponseUtil;
import in.edu.kristujayanti.util.UserInfoUtil;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.WebClient;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisAPI;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static in.edu.kristujayanti.constants.PaymentServiceKeys.*;
import static in.edu.kristujayanti.constants.PaymentServiceKeys.RAZORPAY_SIGNATURE;


//public class GymVerifyPaymentHandler implements Handler<RoutingContext> {
//    private static final Logger LOGGER = LoggerFactory.getLogger(GymVerifyPaymentHandler.class);
//    private final GymPaymentService gymPaymentService;
//    private final WebClient webClient;
//    private final Redis redisCommandConnection;
//    private final String redisHashKey;
//
//    public GymVerifyPaymentHandler(GymPaymentService gymPaymentService, Redis redisCommandConnection, String redisHashKey, WebClient webClient) {
//        this.gymPaymentService = gymPaymentService;
//        this.webClient = webClient;
//        this.redisCommandConnection = redisCommandConnection;
//        this.redisHashKey = redisHashKey;
//    }
//
//    @Override
//    public void handle(RoutingContext routingContext) {
//        HttpServerResponse response = routingContext.response();
//        String loggedInUserEmail = UserInfoUtil.getLoggedInUserEmail(routingContext.request().headers());
//        String currentDateTimeString = DateUtils.convertMillisToDateString(DateUtils.currentDateTimeInMillis());
//
//        if (loggedInUserEmail == null) {
//            LOGGER.warn("[{}] Access denied: User information is missing for request", currentDateTimeString);
//            ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.UNAUTHORIZED,
//                    new JsonArray(), new JsonArray().add("User information is missing"));
//            return;
//        }
//
//
//        try {
//            Document paymentDocument = Document.parse(routingContext.body().asJsonObject().toString());
//            if (paymentDocument.containsKey(RAZORPAY_PAYMENT_ID) &&
//                    paymentDocument.containsKey(RAZORPAY_ORDER_ID) &&
//                    paymentDocument.containsKey(RAZORPAY_SIGNATURE)) {
//
//                LOGGER.info("[{}] Gym Module Payment Verification - Payment ID: [{}], Order ID: [{}]",
//                        currentDateTimeString,
//                        paymentDocument.getString(RAZORPAY_PAYMENT_ID),
//                        paymentDocument.getString(RAZORPAY_ORDER_ID));
//
//                // Prepare payload with three keys
//                JsonObject jsonPayload = new JsonObject()
//                        .put(RAZORPAY_PAYMENT_ID, paymentDocument.getString(RAZORPAY_PAYMENT_ID))
//                        .put(RAZORPAY_ORDER_ID, paymentDocument.getString(RAZORPAY_ORDER_ID))
//                        .put(RAZORPAY_SIGNATURE, paymentDocument.getString(RAZORPAY_SIGNATURE))
//                        .put("loggerUserEmail", loggedInUserEmail);
//
//                RedisAPI redis = RedisAPI.api(redisCommandConnection);
//                redis.hget(redisHashKey, "payment-server-config").onComplete(configAr -> {
//                    if (configAr.succeeded() && configAr.result() != null) {
//                        try {
//                            JsonObject config = new JsonObject(configAr.result().toString());
//                            String ip = config.getString("ip");
//                            int port = config.getInteger("port");
//                            String verifyPaymentRoot = config.getString("verify_gym_payment_root");
//
//                            Future<JsonObject> jsonResponse = CreateHttpClientUtil.sendJsonPostRequest(
//                                    webClient,
//                                    ip,
//                                    verifyPaymentRoot,
//                                    jsonPayload,
//                                    port
//                            );
//
//
//                            jsonResponse.onComplete(result -> {
//                                if (result.succeeded()) {
//                                    JsonObject resultData = result.result();
//                                    JsonObject responseData = resultData.getJsonObject("data")
//                                            .getJsonObject("responseData");
//
//                                    // Directly send the response data back to the UI
//                                    ResponseUtil.createResponse(
//                                            response,
//                                            ResponseType.SUCCESS,
//                                            StatusCode.TWOHUNDRED,
//                                            new JsonArray().add(responseData.getJsonArray("data") != null && !responseData .getJsonArray("data").isEmpty() ? responseData.getJsonArray("data").getJsonObject(0) : new JsonObject()),
//                                            new JsonArray().add(responseData.getJsonArray("message") != null && !responseData.getJsonArray("message").isEmpty() ? responseData.getJsonArray("message").getString(0) : new JsonObject())
//                                    );
//                                } else {
//                                    LOGGER.error("Payment verification failed: {}", result.cause().getMessage());
//                                    ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.INTERNAL_SERVER_ERROR,
//                                            new JsonArray(), new JsonArray().add("Failed to process payment verification"));
//                                }
//                            });
//
//
//                        } catch (Exception e) {
//                            LOGGER.error("Error processing payment configuration", e);
//                            ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.INTERNAL_SERVER_ERROR,
//                                    new JsonArray(), new JsonArray().add("Error processing payment configuration"));
//                        }
//                    } else {
//                        LOGGER.error("Failed to retrieve payment server configuration");
//                        ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.INTERNAL_SERVER_ERROR,
//                                new JsonArray(), new JsonArray().add("Payment configuration retrieval failed"));
//                    }
//                });
//            } else {
//                LOGGER.warn("[{}] Incomplete payment details for user [{}]", currentDateTimeString, loggedInUserEmail);
//                ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.BAD_REQUEST,
//                        new JsonArray(), new JsonArray().add("Incomplete payment details"));
//            }
//        } catch (Exception e) {
//            LOGGER.error("[{}] Unexpected error during payment verification for user [{}]: {}",
//                    currentDateTimeString, loggedInUserEmail, e.getMessage(), e);
//            ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.INTERNAL_SERVER_ERROR,
//                    new JsonArray(), new JsonArray().add("Unexpected error occurred"));
//        }
//    }
//}


//package in.edu.kristujayanti.handlers.GymSybscriptionPayment;

import in.edu.kristujayanti.enums.ResponseType;
import in.edu.kristujayanti.enums.StatusCode;
import in.edu.kristujayanti.services.UserServices.GymPaymentService;
import in.edu.kristujayanti.util.CreateHttpClientUtil;
import in.edu.kristujayanti.util.DateUtils;
import in.edu.kristujayanti.util.ResponseUtil;
import in.edu.kristujayanti.util.UserInfoUtil;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.WebClient;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisAPI;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static in.edu.kristujayanti.constants.PaymentServiceKeys.*;

public class GymVerifyPaymentHandler implements Handler<RoutingContext> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GymVerifyPaymentHandler.class);
    private final GymPaymentService gymPaymentService;
    private final WebClient webClient;
    private final Redis redisCommandConnection;
    private final String redisHashKey;

    public GymVerifyPaymentHandler(GymPaymentService gymPaymentService, Redis redisCommandConnection, String redisHashKey, WebClient webClient) {
        this.gymPaymentService = gymPaymentService;
        this.webClient = webClient;
        this.redisCommandConnection = redisCommandConnection;
        this.redisHashKey = redisHashKey;
    }

//    @Override
//    public void handle(RoutingContext routingContext) {
//        HttpServerResponse response = routingContext.response();
//        String loggedInUserEmail = UserInfoUtil.getLoggedInUserEmail(routingContext.request().headers());
//        String currentDateTimeString = DateUtils.convertMillisToDateString(DateUtils.currentDateTimeInMillis());
//
//        if (loggedInUserEmail == null) {
//            LOGGER.warn("[{}] Access denied: User information is missing for request", currentDateTimeString);
//            ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.UNAUTHORIZED,
//                    new JsonArray(), new JsonArray().add("User information is missing"));
//            return;
//        }
//
//        try {
//            // Check if the request contains a body
//            if (routingContext.body() == null || routingContext.body().asJsonObject() == null) {
//                LOGGER.warn("[{}] Request body is missing or not in JSON format", currentDateTimeString);
//                ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.BAD_REQUEST,
//                        new JsonArray(), new JsonArray().add("Request body is missing or invalid"));
//                return;
//            }
//
//            Document paymentDocument = Document.parse(routingContext.body().asJsonObject().toString());
//            if (paymentDocument.containsKey(RAZORPAY_PAYMENT_ID) &&
//                    paymentDocument.containsKey(RAZORPAY_ORDER_ID) &&
//                    paymentDocument.containsKey(RAZORPAY_SIGNATURE)) {
//
//                LOGGER.info("[{}] Gym Module Payment Verification - Payment ID: [{}], Order ID: [{}]",
//                        currentDateTimeString,
//                        paymentDocument.getString(RAZORPAY_PAYMENT_ID),
//                        paymentDocument.getString(RAZORPAY_ORDER_ID));
//
//                // Prepare payload with required keys
//                JsonObject jsonPayload = new JsonObject()
//                        .put(RAZORPAY_PAYMENT_ID, paymentDocument.getString(RAZORPAY_PAYMENT_ID))
//                        .put(RAZORPAY_ORDER_ID, paymentDocument.getString(RAZORPAY_ORDER_ID))
//                        .put(RAZORPAY_SIGNATURE, paymentDocument.getString(RAZORPAY_SIGNATURE))
//                        .put("loggerUserEmail", loggedInUserEmail);
//
//                RedisAPI redis = RedisAPI.api(redisCommandConnection);
//                redis.hget(redisHashKey, "payment-server-config").onComplete(configAr -> {
//                    if (configAr.succeeded() && configAr.result() != null) {
//                        try {
//                            JsonObject config = new JsonObject(configAr.result().toString());
//                            String ip = config.getString("ip");
//                            int port = config.getInteger("port");
//                            String verifyPaymentRoot = config.getString("verify_gym_payment_root");
//
//                            // Ensure the utility method returns a Future<JsonObject>
//                            CreateHttpClientUtil.sendJsonPostRequest(
//                                    webClient,
//                                    ip,
//                                    verifyPaymentRoot,
//                                    jsonPayload,
//                                    port
//                            ).onComplete(ar -> {
//                                if (ar.succeeded()) {
//                                    try {
//                                        JsonObject resultData = ar.result();
//                                        // Check if the response contains the expected structure
//                                        if (resultData != null && resultData.containsKey("data")) {
//                                            JsonObject responseData = resultData.getJsonObject("data")
//                                                    .getJsonObject("responseData");
//
//                                            JsonArray dataArray = responseData.getJsonArray("data");
//                                            JsonArray messageArray = responseData.getJsonArray("message");
//
//                                            // Safely extract data and message
//                                            JsonObject dataObj = (dataArray != null && !dataArray.isEmpty())
//                                                    ? dataArray.getJsonObject(0)
//                                                    : new JsonObject();
//
//                                            String message = (messageArray != null && !messageArray.isEmpty())
//                                                    ? messageArray.getString(0)
//                                                    : "Payment verification completed";
//
//                                            ResponseUtil.createResponse(
//                                                    response,
//                                                    ResponseType.SUCCESS,
//                                                    StatusCode.TWOHUNDRED,
//                                                    new JsonArray().add(dataObj),
//                                                    new JsonArray().add(message)
//                                            );
//                                        } else {
//                                            LOGGER.error("Invalid response format from payment server");
//                                            ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.INTERNAL_SERVER_ERROR,
//                                                    new JsonArray(), new JsonArray().add("Invalid response format from payment server"));
//                                        }
//                                    } catch (Exception e) {
//                                        LOGGER.error("Error processing payment response: {}", e.getMessage(), e);
//                                        ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.INTERNAL_SERVER_ERROR,
//                                                new JsonArray(), new JsonArray().add("Error processing payment response"));
//                                    }
//                                } else {
//                                    LOGGER.error("Payment verification failed: {}", ar.cause().getMessage());
//                                    ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.INTERNAL_SERVER_ERROR,
//                                            new JsonArray(), new JsonArray().add("Failed to process payment verification: " + ar.cause().getMessage()));
//                                }
//                            });
//                        } catch (Exception e) {
//                            LOGGER.error("Error processing payment configuration: {}", e.getMessage(), e);
//                            ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.INTERNAL_SERVER_ERROR,
//                                    new JsonArray(), new JsonArray().add("Error processing payment configuration"));
//                        }
//                    } else {
//                        LOGGER.error("Failed to retrieve payment server configuration: {}",
//                                configAr.cause() != null ? configAr.cause().getMessage() : "Unknown reason");
//                        ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.INTERNAL_SERVER_ERROR,
//                                new JsonArray(), new JsonArray().add("Payment configuration retrieval failed"));
//                    }
//                });
//            } else {
//                LOGGER.warn("[{}] Incomplete payment details for user [{}]", currentDateTimeString, loggedInUserEmail);
//                ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.BAD_REQUEST,
//                        new JsonArray(), new JsonArray().add("Incomplete payment details"));
//            }
//        } catch (Exception e) {
//            LOGGER.error("[{}] Unexpected error during payment verification for user [{}]: {}",
//                    currentDateTimeString, loggedInUserEmail, e.getMessage(), e);
//            ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.INTERNAL_SERVER_ERROR,
//                    new JsonArray(), new JsonArray().add("Unexpected error occurred"));
//        }
//    }
//}

    public void handle(RoutingContext routingContext) {
        HttpServerResponse response = routingContext.response();
        String loggedInUserEmail = UserInfoUtil.getLoggedInUserEmail(routingContext.request().headers());
        String currentDateTimeString = DateUtils.convertMillisToDateString(DateUtils.currentDateTimeInMillis());

        if (loggedInUserEmail == null) {
            LOGGER.warn("[{}] Access denied: User information is missing for request", currentDateTimeString);
            ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.UNAUTHORIZED,
                    new JsonArray(), new JsonArray().add("User information is missing"));
            return;
        }

        try {
            // Check if the request contains a body
            if (routingContext.body() == null) {
                LOGGER.warn("[{}] Request body is missing", currentDateTimeString);
                ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.BAD_REQUEST,
                        new JsonArray(), new JsonArray().add("Request body is missing"));
                return;
            }

            JsonObject requestBody = routingContext.body().asJsonObject();
            if (requestBody == null) {
                LOGGER.warn("[{}] Request body is not in JSON format", currentDateTimeString);
                ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.BAD_REQUEST,
                        new JsonArray(), new JsonArray().add("Request body is not in JSON format"));
                return;
            }

            Document paymentDocument = Document.parse(requestBody.toString());
            if (paymentDocument.containsKey(RAZORPAY_PAYMENT_ID) &&
                    paymentDocument.containsKey(RAZORPAY_ORDER_ID) &&
                    paymentDocument.containsKey(RAZORPAY_SIGNATURE)) {

                LOGGER.info("[{}] Gym Module Payment Verification - Payment ID: [{}], Order ID: [{}]",
                        currentDateTimeString,
                        paymentDocument.getString(RAZORPAY_PAYMENT_ID),
                        paymentDocument.getString(RAZORPAY_ORDER_ID));

                // Prepare payload with required keys
                JsonObject jsonPayload = new JsonObject()
                        .put(RAZORPAY_PAYMENT_ID, paymentDocument.getString(RAZORPAY_PAYMENT_ID))
                        .put(RAZORPAY_ORDER_ID, paymentDocument.getString(RAZORPAY_ORDER_ID))
                        .put(RAZORPAY_SIGNATURE, paymentDocument.getString(RAZORPAY_SIGNATURE))
                        .put("loggedUserEmail", loggedInUserEmail);

                RedisAPI redis = RedisAPI.api(redisCommandConnection);
                redis.hget(redisHashKey, "payment-server-config").onComplete(configAr -> {
                    if (configAr.succeeded() && configAr.result() != null) {
                        try {
                            JsonObject config = new JsonObject(configAr.result().toString());
                            String ip = config.getString("ip");
                            int port = config.getInteger("port");
                            String verifyPaymentRoot = config.getString("verify_gym_payment_root");

                            LOGGER.info("[{}] Sending request to payment server at {}:{}{}",
                                    currentDateTimeString, ip, port, verifyPaymentRoot);

                            // Make sure CreateHttpClientUtil.sendJsonPostRequest returns a Future<JsonObject>
                            webClient.post(port, ip, verifyPaymentRoot)
                                    .sendJson(jsonPayload)
                                    .onSuccess(httpResponse -> {
                                        try {
                                            if (httpResponse.statusCode() == 200) {
                                                System.out.println("httpResponse"+httpResponse.body());
                                                JsonObject resultData = httpResponse.bodyAsJsonObject();
                                                LOGGER.info("[{}] Received successful response from payment server", currentDateTimeString);

                                                if (resultData != null && resultData.containsKey("responseData")) {
                                                    JsonObject responseData = resultData.getJsonObject("responseData");

                                                    JsonArray dataArray = responseData.getJsonArray("data");
                                                    JsonArray messageArray = responseData.getJsonArray("message");

                                                    // Safely extract data and message
                                                    JsonObject dataObj = (dataArray != null && !dataArray.isEmpty())
                                                            ? dataArray.getJsonObject(0)
                                                            : new JsonObject();

                                                    String message = (messageArray != null && !messageArray.isEmpty())
                                                            ? messageArray.getString(0)
                                                            : "Payment verification completed";

                                                    ResponseUtil.createResponse(
                                                            response,
                                                            ResponseType.SUCCESS,
                                                            StatusCode.TWOHUNDRED,
                                                            new JsonArray().add(dataObj),
                                                            new JsonArray().add(message)
                                                    );
                                                } else {
                                                    LOGGER.error("[{}] Invalid response format from payment server", currentDateTimeString);
                                                    ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.INTERNAL_SERVER_ERROR,
                                                            new JsonArray(), new JsonArray().add("Invalid response format from payment server"));
                                                }
                                            } else {
                                                LOGGER.error("[{}] Payment server returned error status: {}",
                                                        currentDateTimeString, httpResponse.statusCode());
                                                ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.INTERNAL_SERVER_ERROR,
                                                        new JsonArray(), new JsonArray().add("Payment server error: " + httpResponse.statusMessage()));
                                            }
                                        } catch (Exception e) {
                                            LOGGER.error("[{}] Error processing payment response: {}", currentDateTimeString, e.getMessage(), e);
                                            ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.INTERNAL_SERVER_ERROR,
                                                    new JsonArray(), new JsonArray().add("Error processing payment response"));
                                        }
                                    })
                                    .onFailure(err -> {
                                        LOGGER.error("[{}] Payment verification request failed: {}", currentDateTimeString, err.getMessage(), err);
                                        ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.INTERNAL_SERVER_ERROR,
                                                new JsonArray(), new JsonArray().add("Failed to process payment verification: " + err.getMessage()));
                                    });
                        } catch (Exception e) {
                            LOGGER.error("[{}] Error processing payment configuration: {}", currentDateTimeString, e.getMessage(), e);
                            ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.INTERNAL_SERVER_ERROR,
                                    new JsonArray(), new JsonArray().add("Error processing payment configuration"));
                        }
                    } else {
                        LOGGER.error("[{}] Failed to retrieve payment server configuration: {}",
                                currentDateTimeString, configAr.cause() != null ? configAr.cause().getMessage() : "Unknown reason");
                        ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.INTERNAL_SERVER_ERROR,
                                new JsonArray(), new JsonArray().add("Payment configuration retrieval failed"));
                    }
                });
            } else {
                LOGGER.warn("[{}] Incomplete payment details for user [{}]", currentDateTimeString, loggedInUserEmail);
                ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.BAD_REQUEST,
                        new JsonArray(), new JsonArray().add("Incomplete payment details"));
            }
        } catch (Exception e) {
            LOGGER.error("[{}] Unexpected error during payment verification for user [{}]: {}",
                    currentDateTimeString, loggedInUserEmail, e.getMessage(), e);
            ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.INTERNAL_SERVER_ERROR,
                    new JsonArray(), new JsonArray().add("Unexpected error occurred"));
        }
    }
}
