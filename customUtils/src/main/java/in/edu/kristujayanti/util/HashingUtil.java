package in.edu.kristujayanti.util;

import org.mindrot.jbcrypt.BCrypt;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashingUtil {

    public static String hashSHA256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing input", e);
        }
    }

    public static String hashBcrypt(String input) {
        // Generate a salt with 10 rounds
        String salt = BCrypt.gensalt(10);
        // Hash the password with the salt
        return BCrypt.hashpw(input, salt);
    }

    public static boolean verifyBcrypt(String input, String storedHash) {
        // Verify the password against the stored hash
        return BCrypt.checkpw(input, storedHash);
    }
}
