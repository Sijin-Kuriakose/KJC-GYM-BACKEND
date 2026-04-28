package in.edu.kristujayanti.services.UserServices;

import in.edu.kristujayanti.util.EmailVerificationAndLoginCredentialsSMTP;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.Request;
import io.vertx.redis.client.Command;
import io.vertx.redis.client.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

import static in.edu.kristujayanti.util.GenerateOtp.generateOTP;

public class OTPService {

    private static final Logger LOGGER = LogManager.getLogger(OTPService.class);
    private final EmailVerificationAndLoginCredentialsSMTP emailService;
    private final Redis redisClient;
    private final Vertx vertx;
    private final Random random = new Random();
    private static final long OTP_VALIDITY_DURATION = 60; // 60 seconds

    public OTPService(Redis redisClient, Vertx vertx) {
        this.emailService = new EmailVerificationAndLoginCredentialsSMTP();
        this.redisClient = redisClient;
        this.vertx = vertx;
    }

    // Helper method to execute SETEX
    private Future<Response> setex(String key, String seconds, String value) {
        Request request = Request.cmd(Command.SETEX)
                .arg(key)
                .arg(seconds)
                .arg(value);
        return redisClient.send(request)
                .compose(response -> {
                    if (response == null || !response.toString().equals("OK")) {
                        LOGGER.error("Failed to set OTP in Redis for key: {}", key);
                        return Future.failedFuture("Failed to set OTP in Redis");
                    }
                    LOGGER.info("Successfully set OTP in Redis for key: {}", key);
                    return Future.succeededFuture(response);
                });
    }

    // Helper method to execute GET
    private Future<Response> get(String key) {
        Request request = Request.cmd(Command.GET)
                .arg(key);
        return redisClient.send(request)
                .onSuccess(response -> LOGGER.info("Successfully retrieved value from Redis for key: {}", key))
                .onFailure(err -> LOGGER.error("Failed to retrieve value from Redis for key: {}", key, err));
    }

    // Helper method to execute DEL
    private Future<Response> del(String key) {
        Request request = Request.cmd(Command.DEL)
                .arg(key);
        return redisClient.send(request)
                .compose(response -> {
                    if (response == null) {
                        LOGGER.error("Failed to delete key from Redis: {}", key);
                        return Future.failedFuture("Failed to delete key from Redis");
                    }
                    LOGGER.info("Successfully deleted key from Redis: {}", key);
                    return Future.succeededFuture(response);
                });
    }

    public Future<String> sendOTP(String EMAIL_ID) {
        // Validate email
        if (EMAIL_ID == null || EMAIL_ID.trim().isEmpty() || !EMAIL_ID.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            LOGGER.warn("Invalid email address provided: {}", EMAIL_ID);
            return Future.failedFuture("Invalid email address");
        }

        String otpKey = "otp:" + EMAIL_ID;

        return get(otpKey)
                .compose(otpResponse -> {
                    if (otpResponse != null && !otpResponse.toString().isEmpty()) {
                        LOGGER.warn("An OTP is already valid for email: {}", EMAIL_ID);
                        return Future.failedFuture("An OTP is already valid for this email. Please wait to resend.");
                    }

                    String newOTP = generateOTP();
                    String subject = "Your OTP for Verification";
                    String body = "Dear User,\n\nYour One-Time Password (OTP) is: " + newOTP +
                            "\nPlease use this to verify your email.\n\nRegards,\nKristu Jayanti Team";

                    // Wrap the synchronous sendEmail call in executeBlocking
                    return vertx.<Boolean>executeBlocking(future -> {
                                try {
                                    boolean emailSent = emailService.sendEmail(EMAIL_ID, subject, body, true);
                                    future.complete(emailSent);
                                } catch (Exception e) {
                                    LOGGER.error("Error while sending email to {}", EMAIL_ID, e);
                                    future.fail(e);
                                }
                            })
                            .compose(emailSent -> {
                                if (!emailSent) {
                                    LOGGER.error("Failed to send OTP email to {}", EMAIL_ID);
                                    return Future.failedFuture("Failed to send OTP to " + EMAIL_ID);
                                }

                                LOGGER.info("Successfully sent OTP email to {}", EMAIL_ID);
                                System.out.println(newOTP);
                                return setex(otpKey, String.valueOf(OTP_VALIDITY_DURATION), newOTP)
                                        .compose(setResponse -> Future.succeededFuture(newOTP));
                            });
                })
                .onSuccess(otp -> LOGGER.info("OTP sent successfully for email: {}", EMAIL_ID))
                .onFailure(err -> LOGGER.error("Failed to send OTP for email: {}", EMAIL_ID, err));
    }

    // ✅ Verify OTP
    public Future<String> verifyOTP(String email, String otp) {
        // Validate inputs
        if (email == null || email.trim().isEmpty() || !email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            LOGGER.warn("Invalid email address provided for OTP verification: {}", email);
            return Future.failedFuture("Invalid email address");
        }
        if (otp == null || otp.trim().isEmpty()) {
            LOGGER.warn("OTP is required for verification for email: {}", email);
            return Future.failedFuture("OTP is required");
        }

        String otpKey = "otp:" + email;

        return get(otpKey)
                .compose(otpResponse -> {
                    if (otpResponse == null || otpResponse.toString().isEmpty()) {
                        LOGGER.warn("No valid OTP found for email: {}", email);
                        return Future.failedFuture("No valid OTP found for this email. Please request a new OTP.");
                    }

                    String storedOTP = otpResponse.toString();
                    if (!storedOTP.equals(otp)) {
                        LOGGER.warn("Invalid OTP provided for email: {}", email);
                        return Future.failedFuture("Invalid OTP. Please try again.");
                    }

                    // OTP is valid, delete the OTP from Redis
                    return del(otpKey)
                            .compose(delOtpResponse -> {
                                LOGGER.info("OTP verified successfully for email: {}", email);
                                return Future.succeededFuture("OTP verified successfully!");
                            });
                })
                .onSuccess(result -> LOGGER.info("OTP verification completed for email: {}", email))
                .onFailure(err -> LOGGER.error("Failed to verify OTP for email: {}", email, err));
    }
}