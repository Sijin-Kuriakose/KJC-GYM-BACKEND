package in.edu.kristujayanti.util;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PasswordUtil {

    // Private constructor to prevent instantiation
    private PasswordUtil() {
    }

    // Method to generate a random password with specified length
    public static String generateRandomPassword(int length) {
        final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
        final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final String DIGITS = "0123456789";
        final String SPECIAL_CHARACTERS = "!@#$%^&*";

        final String ALL_CHARACTERS = LOWERCASE + UPPERCASE + DIGITS + SPECIAL_CHARACTERS;
        final SecureRandom secureRandom = new SecureRandom();

        if (length < 8) {
            throw new IllegalArgumentException("Password length must be at least 8 characters");
        }

        StringBuilder password = new StringBuilder(length);

        // Ensure the password contains at least one of each required character type
        password.append(LOWERCASE.charAt(secureRandom.nextInt(LOWERCASE.length())));
        password.append(UPPERCASE.charAt(secureRandom.nextInt(UPPERCASE.length())));
        password.append(DIGITS.charAt(secureRandom.nextInt(DIGITS.length())));
        password.append(SPECIAL_CHARACTERS.charAt(secureRandom.nextInt(SPECIAL_CHARACTERS.length())));

        // Fill the rest of the password length with random characters from all available characters
        for (int i = 4; i < length; i++) {
            password.append(ALL_CHARACTERS.charAt(secureRandom.nextInt(ALL_CHARACTERS.length())));
        }

        // Convert StringBuilder to a list of characters for shuffling
        List<Character> passwordChars = new ArrayList<>();
        for (int i = 0; i < password.length(); i++) {
            passwordChars.add(password.charAt(i));
        }

        // Shuffle the characters to avoid predictable patterns
        Collections.shuffle(passwordChars, secureRandom);

        // Convert the list back to a string
        StringBuilder shuffledPassword = new StringBuilder(length);
        for (Character c : passwordChars) {
            shuffledPassword.append(c);
        }

        return shuffledPassword.toString();
    }
}
