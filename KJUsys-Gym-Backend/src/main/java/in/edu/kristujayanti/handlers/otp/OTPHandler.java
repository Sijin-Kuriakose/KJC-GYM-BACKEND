package in.edu.kristujayanti.handlers.otp;

import in.edu.kristujayanti.services.UserServices.OTPService;
import in.edu.kristujayanti.enums.ResponseType;
import in.edu.kristujayanti.enums.StatusCode;
import in.edu.kristujayanti.util.ResponseUtil;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static in.edu.kristujayanti.propertyBinder.Gym.GymKeysPBinder.EMAIL_ID;

public class OTPHandler {

    private static final Logger LOGGER = LogManager.getLogger(OTPHandler.class);
    private final OTPService otpService;

    public OTPHandler(OTPService otpService) {
        this.otpService = otpService;
    }

    public void sendOTP(RoutingContext context) {
        try {
            JsonObject body = context.body().asJsonObject();
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

            String emailLocalPart = body.getString(EMAIL_ID.getPropertyName()); // "emailId_Gym_Text"

            if (emailLocalPart == null || emailLocalPart.trim().isEmpty()) {
                LOGGER.warn("Email local part is missing in request: {}", body);
                ResponseUtil.createResponse(
                        context.response(),
                        ResponseType.VALIDATION,
                        StatusCode.BAD_REQUEST,
                        new JsonArray(),
                        new JsonArray().add("Email local part is required")
                );
                return;
            }

            LOGGER.info("Sending OTP to email: {}", emailLocalPart);

            otpService.sendOTP(emailLocalPart)
                    .onSuccess(otp -> {
                        LOGGER.info("OTP sent successfully to {}", emailLocalPart);
                        ResponseUtil.createResponse(
                                context.response(),
                                ResponseType.SUCCESS,
                                StatusCode.TWOHUNDRED,
                                new JsonArray(),
                                new JsonArray().add("OTP sent successfully to " + emailLocalPart)
                        );
                    })
                    .onFailure(err -> {
                        LOGGER.error("Failed to send OTP to {}: {}", emailLocalPart, err.getMessage(), err);
                        ResponseUtil.createResponse(
                                context.response(),
                                ResponseType.ERROR,
                                StatusCode.BAD_REQUEST,
                                new JsonArray(),
                                new JsonArray().add(
                                        new JsonObject()
                                                .put("message", "Failed to send OTP")
                                                .put("details", err.getMessage())
                                )
                        );
                    });
        } catch (Exception e) {
            LOGGER.error("Unexpected error while processing OTP request: {}", e.getMessage(), e);
            ResponseUtil.createResponse(
                    context.response(),
                    ResponseType.ERROR,
                    StatusCode.INTERNAL_SERVER_ERROR,
                    new JsonArray(),
                    new JsonArray().add(
                            new JsonObject()
                                    .put("message", "Unexpected error while sending OTP")
                                    .put("details", e.getMessage())
                    )
            );
        }
    }
}