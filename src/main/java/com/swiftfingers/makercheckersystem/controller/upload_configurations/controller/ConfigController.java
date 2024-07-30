package com.swiftfingers.makercheckersystem.controller.upload_configurations.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.swiftfingers.makercheckersystem.controller.upload_configurations.classes.*;
import com.swiftfingers.makercheckersystem.controller.upload_configurations.conf.ConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Obiora on 30-Jul-2024 at 10:52
 */
@RestController
@RequestMapping("/api/config")
@RequiredArgsConstructor
public class ConfigController {

    private final ConfigService configService;
    private final ObjectMapper objectMapper;

    @PostMapping("/application-settings")
    public ResponseEntity<String> saveApplicationSettings(@RequestBody ApplicationSetting settings) {
        return configService.saveConfig("application_settings.json", settings);
    }

    @PostMapping("/social-auth-settings")
    public ResponseEntity<String> saveSocialAuthSettings(@RequestBody SocialAuthenticationSettings settings) {
        return configService.saveConfig("social_authentication_settings.json", settings);
    }

    @PostMapping("/maintenance-mode")
    public ResponseEntity<String> saveMaintenanceMode(@RequestBody MaintenanceMode mode) {
        return configService.saveConfig("maintenance_mode.json", mode);
    }

    @PostMapping("/social-settings")
    public ResponseEntity<String> saveSocialSettings(@RequestBody SocialSettings settings) {
        return configService.saveConfig("social_settings.json", settings);
    }

    @PostMapping("/advanced-settings")
    public ResponseEntity<String> saveAdvancedSettings(@RequestBody AdvancedSettings settings) {
        return configService.saveConfig("advanced_settings.json", settings);
    }

    @GetMapping("/find-settings")
    public ResponseEntity<JsonNode> findSettings() {
        AdvancedSettings advancedSettings = configService.getAdvancedSettings();
        ApplicationSetting applicationSetting = configService.getApplicationSetting();
        SocialSettings socialSettings = configService.getSocialSettings();
        MaintenanceMode maintenanceMode = configService.getMaintenanceMode();
        SocialAuthenticationSettings socialAuthSettings = configService.getSocialAuthSettings();
        ApplicationInfo applicationInfoSettings = configService.getApplicationInfoSettings();


        // Create a root node
        ObjectNode rootNode = objectMapper.createObjectNode();

        // Add settings to the root node
        rootNode.set("advancedSettings", objectMapper.valueToTree(advancedSettings));
        rootNode.set("applicationSetting", objectMapper.valueToTree(applicationSetting));
        rootNode.set("socialSettings", objectMapper.valueToTree(socialSettings));
        rootNode.set("maintenanceMode", objectMapper.valueToTree(maintenanceMode));
        rootNode.set("socialAuthSettings", objectMapper.valueToTree(socialAuthSettings));
        rootNode.set("applicationInfoSettings", objectMapper.valueToTree(applicationInfoSettings));

        return ResponseEntity.ok(rootNode);
    }

}
