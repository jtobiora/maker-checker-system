package com.swiftfingers.makercheckersystem.service;

import com.swiftfingers.makercheckersystem.enums.TokenDestination;
import com.swiftfingers.makercheckersystem.model.user.Token;
import com.swiftfingers.makercheckersystem.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

@Service
@Slf4j
@RequiredArgsConstructor
public class TwoFaTokenService {
    private static final String SECRET_KEY = "546tehsf3#6df56FDSG3g3f@1&^@%fsg#6566DSF38^SG3sf5SXAXvdg";
    private static final int TOKEN_EXPIRATION_SECONDS = 45; // Token expiration time in seconds
    private final TokenRepository tokenRepository;

    private final PasswordEncoder passwordEncoder;

    public String generate2FAToken(String uniqueIdentifier) {
        try {
            // Concatenate unique identifier with secret key
            String data = uniqueIdentifier + SECRET_KEY;

            // Generate SHA-256 hash
            byte[] hash = calculateHash(data);

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

    public boolean is2FATokenValid(Token token, String rawTokenPassed) {
        // Calculate token expiration time
        Instant expirationTime = token.getCreationTime().plusSeconds(TOKEN_EXPIRATION_SECONDS);
        try {
            boolean isActive = Instant.now().isBefore(expirationTime);  //Check if current time is before token expiration time

            // Calculate hash of the token to make sure it has not been tampered with
            String calculatedHash = generateTokenHash(rawTokenPassed, token.getCreationTime());

            // Compare calculated hash with the stored token hash
            boolean isValid = calculatedHash.equals(token.getTokenHash());

            return isActive && isValid;
        } catch (NoSuchAlgorithmException e) {
            log.error("Token validation failed ", e);
            return false;
        }
    }

    private int byteArrayToInt(byte[] bytes) {
        return bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
    }

    private byte[] calculateHash(String data) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(data.getBytes(StandardCharsets.UTF_8));
    }

    private String generateTokenHash(String token, Instant creationTime) throws NoSuchAlgorithmException {
        String data = token + SECRET_KEY + creationTime.toString();
        byte[] hashBytes = calculateHash(data);
        return new String(hashBytes, StandardCharsets.UTF_8);
    }

    public String generateAndSaveToken(String loginId, TokenDestination destination, String uniqueIdentifier, String authPayload) {
        // Generate 2FA token
        String twoFaToken = generate2FAToken(uniqueIdentifier);
        // Calculate hash of the token
        Instant creationTime = Instant.now();
        String tokenHash = null;
        try {
            tokenHash = generateTokenHash(twoFaToken, creationTime);
        } catch (NoSuchAlgorithmException e) {
            // Handle hash generation error
            log.error("Error occured while generating token hash ", e);
        }

        // Create Token object
        Token token = Token.builder()
                .loginId(loginId)
                ._2faToken(passwordEncoder.encode(twoFaToken))
                .creationTime(creationTime)
                .destination(destination)
                .uniqueIdentifier(uniqueIdentifier)
                .authPayload(authPayload)
                .tokenHash(tokenHash)
                .build();

        // Save Token object
        tokenRepository.save(token);

        return twoFaToken;
    }
}
