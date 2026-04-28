package in.edu.kristujayanti.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.google.gson.Gson;

public final class CryptographyUtils {
    private static final Logger LOGGER = LogManager.getLogger(CryptographyUtils.class);
    private static final Gson gson = new Gson(); // Use Gson instead of ObjectMapper

    public static Cipher initializeCipherInstance() {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            return cipher;
        } catch (Exception var2) {
            LOGGER.error("Cipher creation failed due to: " + var2.getMessage());
            throw new IllegalStateException("Cipher creation failed due to: " + var2.getMessage());
        }
    }

    public static Cipher initializeEncryptCipher(Cipher cipher, String secretKey, String ivValue) {
        try {
            LOGGER.info("secretKey", secretKey);
            LOGGER.info("ivValue", ivValue);

            byte[] decodedKey = Base64.getDecoder().decode(secretKey);
            byte[] decodedIV = Base64.getDecoder().decode(ivValue);

            SecretKeySpec secretKeySpec = new SecretKeySpec(decodedKey, "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(decodedIV);

            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            return cipher;
        } catch (Exception var5) {
            LOGGER.error("Encrypt Cipher creation failed due to: " + var5.getMessage());
            throw new IllegalStateException("Encrypt Cipher creation failed due to: " + var5.getMessage());
        }
    }

    public static Cipher initializeDecryptCipher(Cipher cipher, String secretKey, String ivValue) {
        try {
            byte[] decodedKey = Base64.getDecoder().decode(secretKey);
            byte[] decodedIV = Base64.getDecoder().decode(ivValue);

            SecretKeySpec secretKeySpec = new SecretKeySpec(decodedKey, "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(decodedIV);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            return cipher;
        } catch (Exception var5) {
            LOGGER.error("Decrypt Cipher creation failed due to: " + var5.getMessage());
            throw new IllegalStateException("Decrypt Cipher creation failed due to: " + var5.getMessage());
        }
    }

    public static String getEncryptedValueForObject(Cipher encryptCipher, Object objectToEncrypt) {
        try {
            String jsonString = gson.toJson(objectToEncrypt); // Using Gson to serialize the object
            return getEncryptedValue(encryptCipher, jsonString);
        } catch (Exception e) {
            LOGGER.error("An error occurred while encrypting the object {} with exception {}", objectToEncrypt, e);
            throw new IllegalStateException("An error occurred while encrypting the object " + objectToEncrypt);
        }
    }

    public static String getEncryptedValue(Cipher encryptCipher, String stringToEncrypt) {
        try {
            return Base64.getEncoder().encodeToString(encryptCipher.doFinal(stringToEncrypt.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception var3) {
            LOGGER.error("An error occurred while encrypting the data {}: {}", stringToEncrypt, var3);
            throw new IllegalStateException("An error occurred while encrypting the data: " + stringToEncrypt);
        }
    }

    public static String getDecryptedValue(Cipher decryptCipher, String stringToDecrypt) {
        try {
            return new String(decryptCipher.doFinal(Base64.getDecoder().decode(stringToDecrypt)), StandardCharsets.UTF_8);
        } catch (Exception var3) {
            LOGGER.error("An error occurred while decrypting the data {}: {}", stringToDecrypt, var3);
            throw new IllegalStateException("Cipher decryption failed. Please review the code.");
        }
    }

    public static <T> T getDecryptedValueForObject(Cipher decryptCipher, String encryptedValue, Class<T> valueType) {
        try {
            String decryptedJson = getDecryptedValue(decryptCipher, encryptedValue);
            return gson.fromJson(decryptedJson, valueType); // Using Gson to deserialize the object
        } catch (Exception e) {
            LOGGER.error("An error occurred while decrypting and deserializing the object {} with exception {}", encryptedValue, e);
            throw new IllegalStateException("An error occurred while decrypting and deserializing the object " + encryptedValue);
        }
    }

    public static String getSearchKeyValue(String searchKey) {
        try {
            searchKey = searchKey.toUpperCase();
            searchKey = searchKey.trim().replaceAll("\\s+", " ");
            return searchKey.length() <= 3 ? searchKey : searchKey.substring(0, 3);
        } catch (Exception var2) {
            LOGGER.error("An error occurred while processing the search key {}: {}", searchKey, var2);
            throw new IllegalStateException("An error occurred while processing the search key: " + searchKey);
        }
    }
}
