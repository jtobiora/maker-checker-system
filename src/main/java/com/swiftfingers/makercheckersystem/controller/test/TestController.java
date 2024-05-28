package com.swiftfingers.makercheckersystem.controller.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.swiftfingers.makercheckersystem.model.role.Role;
import com.swiftfingers.makercheckersystem.payload.request.RoleRequest;
import com.swiftfingers.makercheckersystem.payload.response.EmailValidatorResponse;
import com.swiftfingers.makercheckersystem.service.EmailSender;
import com.swiftfingers.makercheckersystem.service.redis.LoginTokenService;
import com.swiftfingers.makercheckersystem.utils.EncryptionUtil;
import com.swiftfingers.makercheckersystem.utils.MapperUtils;
import com.swiftfingers.makercheckersystem.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Base64;
import java.util.Map;

@RequestMapping("/api/test")
@RequiredArgsConstructor
@RestController
public class TestController {


    private final EncryptionUtil encryptionUtil;
    private final ObjectMapper objectMapper;

//    @PostMapping("/encrypt")
//    @PostMapping(value = "/encrypt")
//    public ResponseEntity<?> encrypt(@RequestBody RoleRequest roleRequest, @RequestParam String key) {
//        try {
//            // Create an ObjectMapper instance
//            ObjectMapper objectMapper = new ObjectMapper();
//            Role role = Role.builder()
//                    .authorizationRole(Utils.getBoolean(roleRequest.isAuthorizationRole()))
//                    .systemRole(Utils.getBoolean(roleRequest.isSystemRole()))
//                    .description(roleRequest.getDescription())
//                    .name(roleRequest.getName())
//                    .roleCode("role_code")
//                    .ownerUserName(roleRequest.getOwnerUserName())
//                    .build();
//            String stringifiedRole = MapperUtils.toJSON(role);
//            String encryptedData = encryptionUtil.encryptObjectToJsonString(stringifiedRole, key);
//
//
//            // Create a new ObjectNode
//            ObjectNode objectNode = objectMapper.createObjectNode();
//
//            // Add values to the ObjectNode
//            objectNode.put("encrypted", encryptedData);
//            return ResponseEntity.ok(objectNode);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(500).body("Encryption failed: " + e.getMessage());
//        }
//    }
//
//    //@PostMapping("/decrypt")
//    @PostMapping(value = "/decrypt", consumes = MediaType.TEXT_PLAIN_VALUE,
//            produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<?> decrypt(@RequestBody String encryptedMessage, @RequestParam String key) {
//        try {
//            Role role = encryptionUtil.decryptJsonStringToObject(encryptedMessage, key, Role.class);
//            return ResponseEntity.ok(role);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(500).body("Decryption failed: " + e.getMessage());
//        }
//    }


}
