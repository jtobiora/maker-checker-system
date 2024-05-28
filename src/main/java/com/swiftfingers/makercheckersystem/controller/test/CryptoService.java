package com.swiftfingers.makercheckersystem.controller.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;

/**
 * Created by Obiora on 21-May-2024 at 15:17
 */

@Service
public class CryptoService {
    private static final int GCM_NONCE_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 16;

    private final ObjectMapper objectMapper;

    public CryptoService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String encryptAndSign(Object data, SecretKey encryptionKey) throws JsonProcessingException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        SecureRandom secureRandom = new SecureRandom();
        byte[] iv = new byte[GCM_NONCE_LENGTH];
        secureRandom.nextBytes(iv);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);

        cipher.init(Cipher.ENCRYPT_MODE, encryptionKey, parameterSpec);
        byte[] cipherText = cipher.doFinal(objectMapper.writeValueAsBytes(data));

        String encryptedData = Base64.getEncoder().encodeToString(cipherText);
        String encodedIv = Base64.getEncoder().encodeToString(iv);
        return encryptedData + ":" + encodedIv;
    }

    public Map<String, Object> decryptAndValidate(String encryptedData, SecretKey encryptionKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, IOException {
        String[] parts = encryptedData.split(":");
        byte[] cipherText = Base64.getDecoder().decode(parts[0]);
        byte[] iv = Base64.getDecoder().decode(parts[1]);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
        cipher.init(Cipher.DECRYPT_MODE, encryptionKey, parameterSpec);
        byte[] decryptedData = cipher.doFinal(cipherText);

        return objectMapper.readValue(decryptedData, Map.class);
    }
}
