package com.swiftfingers.makercheckersystem.service;

import com.swiftfingers.makercheckersystem.exceptions.ResourceNotFoundException;
import com.swiftfingers.makercheckersystem.model.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
@RequiredArgsConstructor
public class PasswordManager {
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    public static final String SYMBOLS = "!@#$%^&*()-_+=<>?";
    private static final String ALL_CHARS = UPPERCASE + LOWERCASE + DIGITS + SYMBOLS;

    private static final SecureRandom random = new SecureRandom();
    private static final int PASSWORD_LENGTH = 12; // Fixed length
    private final PasswordEncoder passwordEncoder;
    public void checkPassword (String rawPassword, User user) {
        boolean passwordMatched = passwordEncoder.matches(rawPassword, user.getPassword());

        if (!passwordMatched) {
            throw new ResourceNotFoundException("Incorrect username or password!");
        }
    }

    public static String generatePassword() {
        char[] password = new char[PASSWORD_LENGTH];

        // Ensure each character type is represented
        password[0] = UPPERCASE.charAt(random.nextInt(UPPERCASE.length()));
        password[1] = LOWERCASE.charAt(random.nextInt(LOWERCASE.length()));
        password[2] = DIGITS.charAt(random.nextInt(DIGITS.length()));
        password[3] = SYMBOLS.charAt(random.nextInt(SYMBOLS.length()));

        // Fill remaining characters with random selections from all character sets
        for (int i = 4; i < PASSWORD_LENGTH; i++) {
            password[i] = ALL_CHARS.charAt(random.nextInt(ALL_CHARS.length()));
        }

        // Shuffle the password array to remove predictability
        shuffleArray(password);

        return new String(password);
    }

    private static void shuffleArray(char[] array) {
        for (int i = array.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            // Simple swap
            char temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }
}
