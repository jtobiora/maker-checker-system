package com.swiftfingers.makercheckersystem.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Service;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;
import java.util.Map;

/**
 * Created by Obiora on 21-May-2024 at 13:00
 */
@Service
public class EncryptionUtil {
//    private static final String ALGORITHM = "AES";
//
//    private final ObjectMapper objectMapper;
//
//    @Autowired
//    public EncryptionUtil(ObjectMapper objectMapper) {
//        this.objectMapper = objectMapper;
//    }
//
//    private static SecretKey generateKey(String key) throws NoSuchAlgorithmException {
//        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
//        MessageDigest sha = MessageDigest.getInstance("SHA-1");
//        keyBytes = sha.digest(keyBytes);
//        keyBytes = java.util.Arrays.copyOf(keyBytes, 16); // use only first 128 bits
//        return new SecretKeySpec(keyBytes, ALGORITHM);
//    }
//
//    private static String encrypt(String data, SecretKey key) throws Exception {
//        Cipher cipher = Cipher.getInstance(ALGORITHM);
//        cipher.init(Cipher.ENCRYPT_MODE, key);
//        byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
//        return Base64.getEncoder().encodeToString(encryptedBytes);
//    }
//
//    private static String decrypt(String encryptedData, SecretKey key) throws Exception {
//        Cipher cipher = Cipher.getInstance(ALGORITHM);
//        cipher.init(Cipher.DECRYPT_MODE, key);
//        byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
//        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
//        return new String(decryptedBytes, StandardCharsets.UTF_8);
//    }
//
//    public String encryptObjectToJsonString(String jsonString, String secretKey) throws Exception {
//        SecretKey key = generateKey(secretKey);
//        return encrypt(jsonString, key);
//    }
//
//    public <T> T decryptJsonStringToObject(String encryptedJsonString, String secretKey, Class<T> clazz) throws Exception {
//        SecretKey key = generateKey(secretKey);
//        String jsonString = decrypt(encryptedJsonString, key);
//        return objectMapper.readValue(jsonString, clazz);
//    }

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
