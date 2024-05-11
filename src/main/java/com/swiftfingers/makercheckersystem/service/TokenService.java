package com.swiftfingers.makercheckersystem.service;

import com.swiftfingers.makercheckersystem.model.user.Token;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

@Service
@Slf4j
public class TokenService {

    private static final String SECRET_KEY = "546tehsf3#6df56FDSG3g3f@1&^@%fsg#6566DSF38^SG3sf5SXAXvdg";
    private static final int TOKEN_EXPIRATION_SECONDS = 30; // Token expiration time in seconds

    public String generateToken(String uniqueIdentifier) {
        try {
            // Concatenate unique identifier with secret key
            String data = uniqueIdentifier + SECRET_KEY;

            // Generate SHA-256 hash
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));

            // Take the first four bytes of the hash
            byte[] truncatedHash = new byte[4];
            System.arraycopy(hash, 0, truncatedHash, 0, 4);

            // Convert the truncated hash to an integer
            int tokenValue = byteArrayToInt(truncatedHash);

            // Ensure token is positive and four digits
            tokenValue = Math.abs(tokenValue) % 10000;

            // Format token as four-digit string
            return String.format("%04d", tokenValue);
        } catch (NoSuchAlgorithmException e) {
            log.error("Token generation failed ", e);
            return null;
        }
    }

    public boolean isTokenValid(Token token) {
        // Calculate token expiration time
        Instant expirationTime = token.getCreationTime().plusSeconds(TOKEN_EXPIRATION_SECONDS);

        // Check if current time is before token expiration time
        return Instant.now().isBefore(expirationTime);
    }

    private int byteArrayToInt(byte[] bytes) {
        return bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
    }
}

