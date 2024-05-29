package com.swiftfingers.makercheckersystem.utils;

import org.springframework.stereotype.Service;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

/**
 * Created by Obiora on 21-May-2024 at 13:00
 */
@Service
public class EncryptionUtil {
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int KEY_SIZE = 16; // 128 bits
    private static final int TAG_LENGTH_BIT = 128;
    private static final int IV_LENGTH_BYTE = 12;

    public static String encrypt(String json, String privateKey) {
        try {
            // Pad the key to 16 bytes
            byte[] keyBytes = padKey(privateKey, KEY_SIZE);
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");

            // Generate random IV
            byte[] iv = new byte[IV_LENGTH_BYTE];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);

            // Encrypt
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);
            byte[] encryptedValue = cipher.doFinal(json.getBytes(StandardCharsets.UTF_8));

            // Combine IV and encrypted value
            byte[] encryptedWithIv = new byte[IV_LENGTH_BYTE + encryptedValue.length];
            System.arraycopy(iv, 0, encryptedWithIv, 0, IV_LENGTH_BYTE);
            System.arraycopy(encryptedValue, 0, encryptedWithIv, IV_LENGTH_BYTE, encryptedValue.length);

            return Base64.getEncoder().encodeToString(encryptedWithIv);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String decrypt(String encryptedValue, String privateKey) {
        try {
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedValue);

            // Extract IV
            byte[] iv = new byte[IV_LENGTH_BYTE];
            System.arraycopy(encryptedBytes, 0, iv, 0, IV_LENGTH_BYTE);

            // Extract encrypted value
            byte[] encryptedData = new byte[encryptedBytes.length - IV_LENGTH_BYTE];
            System.arraycopy(encryptedBytes, IV_LENGTH_BYTE, encryptedData, 0, encryptedData.length);

            byte[] keyBytes = padKey(privateKey, KEY_SIZE);
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
            GCMParameterSpec parameterSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);

            // Decrypt
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);
            byte[] decryptedValue = cipher.doFinal(encryptedData);

            return new String(decryptedValue, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] padKey(String key, int length) {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        byte[] paddedKey = new byte[length];
        for (int i = 0; i < paddedKey.length; i++) {
            if (i < keyBytes.length) {
                paddedKey[i] = keyBytes[i];
            } else {
                paddedKey[i] = 0;
            }
        }
        return paddedKey;
    }
}
