package in.edu.kristujayanti.handlers.otp;

import in.edu.kristujayanti.services.UserServices.OTPService;
import in.edu.kristujayanti.enums.ResponseType;
import in.edu.kristujayanti.enums.StatusCode;
//import in.edu.kristujayanti.services.service.RegisterService;
import in.edu.kristujayanti.services.UserServices.UserService;
import in.edu.kristujayanti.util.ResponseUtil;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import static in.edu.kristujayanti.propertyBinder.Gym.GymKeysPBinder.*;
public class VerifyOTPHandler {

    private static final Logger LOGGER = LogManager.getLogger(VerifyOTPHandler.class);
    private final OTPService otpService;
    private final UserService userService;
    private static final String EMAIL_KEY = EMAIL_ID.getPropertyName();
    private static final String OTP_KEY = "otp";
    private static final String EMAIL_DOMAIN = "@kristujayanti.com";

    public VerifyOTPHandler(OTPService otpService,UserService userService) {
        this.otpService = otpService;
        this.userService=userService;
    }

    public void verifyOTP(RoutingContext context) {
        try {
            JsonObject body = context.getBodyAsJson();
            if (body == null) {
                LOGGER.warn("Request body is empty");
                ResponseUtil.createResponse(
                        context.response(),
                        ResponseType.VALIDATION,
                        StatusCode.BAD_REQUEST,
                        new JsonArray(),
                        new JsonArray().add("Request body is empty")
                );
                return;
            }

            String email = body.getString(EMAIL_KEY);
            String otp = body.getString(OTP_KEY);

            // Validate required fields
            JsonArray validationErrors = new JsonArray();
            if (email == null || email.trim().isEmpty()) {
                validationErrors.add("Email is required");
            }
            if (otp == null || otp.trim().isEmpty()) {
                validationErrors.add("OTP is required");
            }

            if (!validationErrors.isEmpty()) {
                LOGGER.warn("Validation failed for request: {}. Errors: {}", body, validationErrors);
                ResponseUtil.createResponse(
                        context.response(),
                        ResponseType.VALIDATION,
                        StatusCode.BAD_REQUEST,
                        new JsonArray(),
                        validationErrors
                );
                return;
            }

            LOGGER.info("Verifying OTP for email: {}", email);
            otpService.verifyOTP(email, otp)
                    .onSuccess(result -> {
                        LOGGER.info("OTP verified successfully for email: {}", email);
                        boolean isRegistered=  userService.getUserByEmailId(email.toUpperCase());

                        // Extract the roll number (userID) from the email
                        String userID = email.replace(EMAIL_DOMAIN, ""); // e.g., "23macb33@kristujayanti.com" -> "23macb33"

                        // Include userID and email in the response data
                        JsonObject responseData = new JsonObject()
                                .put(USER_OID.getPropertyName(), userID)
                                .put("isRegistered",isRegistered)
                                .put(EMAIL_ID.getPropertyName(), email);

                        if (isRegistered) {
                            userService.getUserDetailsByEmail(email.toUpperCase())
                                    .onSuccess(userDetails -> {
                                        responseData.put(NAME.getPropertyName(), userDetails.getString("name_Gym_Text"))
                                                .put(CONTACT_NUMBER.getPropertyName(), userDetails.getLong("contactNumber_Gym_Long"))
                                                .put(USER_ID.getPropertyName(), userDetails.getString("userId_Gym_Text", userID))
                                                .put(EMAIL_ID.getPropertyName(), userDetails.getString("emailId_Gym_Text", email));

                                        ResponseUtil.createResponse(
                                                context.response(),
                                                ResponseType.SUCCESS,
                                                StatusCode.TWOHUNDRED,
                                                responseData,
                                                new JsonArray().add(result)
                                        );
                                    })
                                    .onFailure(err -> {
                                        ResponseUtil.createResponse(
                                                context.response(),
                                                ResponseType.SUCCESS,
                                                StatusCode.TWOHUNDRED,
                                                responseData,
                                                new JsonArray().add(result)
                                        );
                                    });
                        } else {
                            ResponseUtil.createResponse(
                                    context.response(),
                                    ResponseType.SUCCESS,
                                    StatusCode.TWOHUNDRED,
                                    responseData,
                                    new JsonArray().add(result)
                            );
                        }
                    })
                    .onFailure(err -> {
                        LOGGER.error("Failed to verify OTP for email: {}", email, err);
                        ResponseUtil.createResponse(
                                context.response(),
                                ResponseType.ERROR,
                                StatusCode.BAD_REQUEST,
                                new JsonArray(),
                                new JsonArray().add(
                                        new JsonObject()
                                                .put("message", "Failed to verify OTP")
                                                .put("details", err.getMessage())
                                )
                        );
                    });
        } catch (Exception e) {
            LOGGER.error("Unexpected error while processing OTP verification request: {}", e.getMessage(), e);
            ResponseUtil.createResponse(
                    context.response(),
                    ResponseType.ERROR,
                    StatusCode.INTERNAL_SERVER_ERROR,
                    new JsonArray(),
                    new JsonArray().add(
                            new JsonObject()
                                    .put("message", "Unexpected error while verifying OTP")
                                    .put("details", e.getMessage())
                    )
            );
        }
    }
}