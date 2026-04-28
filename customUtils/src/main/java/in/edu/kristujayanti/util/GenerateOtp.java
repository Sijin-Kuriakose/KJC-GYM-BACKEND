package in.edu.kristujayanti.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class GenerateOtp{

    private static final Logger LOGGER = LoggerFactory.getLogger(GenerateOtp.class);
    public static String generateOTP() {
        try {
            // Attempt to use a strong SecureRandom instance
            SecureRandom secureRandom = SecureRandom.getInstanceStrong();
            int otp = 100000 + secureRandom.nextInt(900000);
            return String.valueOf(otp);
        } catch (NoSuchAlgorithmException e) {
            // Fallback to the default SecureRandom instance if a strong one is not available
            LOGGER.warn("Strong SecureRandom algorithm not available. Falling back to default.");
            SecureRandom secureRandom = new SecureRandom();
            int otp = 100000 + secureRandom.nextInt(900000);
            return String.valueOf(otp);
        }
    }

}
