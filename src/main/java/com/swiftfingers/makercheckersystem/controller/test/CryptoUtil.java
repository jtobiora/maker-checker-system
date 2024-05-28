package com.swiftfingers.makercheckersystem.controller.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swiftfingers.makercheckersystem.utils.EncryptionUtil;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Created by Obiora on 26-May-2024 at 11:44
 */
public class CryptoUtil {
//    private static final String ALGORITHM = "AES";
//    private static CryptoUtil instance;
//
//    private final ObjectMapper objectMapper;
//
//    public CryptoUtil(ObjectMapper objectMapper) {
//        this.objectMapper = objectMapper;
//    }
//
//    public static CryptoUtil getInstance(ObjectMapper objectMapper) {
//        if (instance == null) {
//            instance = new CryptoUtil(objectMapper);
//        }
//        return instance;
//    }
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
//    public String encryptObjectToJsonString(String jsonString, String secretKey) {
//        try {
//            SecretKey key = generateKey(secretKey);
//            return encrypt(jsonString, key);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "Encryption failed: " + e.getMessage();
//        }
//    }
//
//    public <T> T decryptJsonStringToObject(String encryptedJsonString, String secretKey, Class<T> clazz) throws Exception {
//        SecretKey key = generateKey(secretKey);
//        String jsonString = decrypt(encryptedJsonString, key);
//        return objectMapper.readValue(jsonString, clazz);
//    }

    private static final String ALGORITHM = "AES";

    public static String encrypt (String chargeResponse, String privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        SecretKeySpec secretKey = new SecretKeySpec(privateKey.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedValue = cipher.doFinal(chargeResponse.getBytes());

        return Base64.getEncoder().encodeToString(encryptedValue);
    }

    public static String decrypt (String encryptedValue, String privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        SecretKeySpec secretKey = new SecretKeySpec(privateKey.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decodedValue = Base64.getDecoder().decode(encryptedValue);
        byte[] decryptedValue = cipher.doFinal(decodedValue);
        return new String(decryptedValue);
    }
}
