package com.swiftfingers.makercheckersystem.demos.upload_configurations.conf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swiftfingers.makercheckersystem.demos.upload_configurations.classes.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Obiora on 30-Jul-2024 at 10:44
 */
@Service
@Slf4j
public class ConfigService {


//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    private ApplicationSetting applicationSetting;
//    private SocialAuthenticationSettings socialAuthSettings;
//    private MaintenanceMode maintenanceMode;
//    private SocialSettings socialSettings;
//    private AdvancedSettings advancedSettings;
//    private ApplicationInfo applicationInfo;
//
//    public ConfigService() throws IOException {
//        ensureDefaultConfigurations();
//        loadConfigurations();
//    }
//
//    private void ensureDefaultConfigurations() throws IOException {
//        createDefaultFile("application_settings.json", ApplicationSetting.class, new ApplicationSetting());
//        createDefaultFile("social_authentication_settings.json", SocialAuthenticationSettings.class, new SocialAuthenticationSettings());
//        createDefaultFile("maintenance_mode.json", MaintenanceMode.class, new MaintenanceMode());
//        createDefaultFile("social_settings.json", SocialSettings.class, new SocialSettings());
//        createDefaultFile("advanced_settings.json", AdvancedSettings.class, new AdvancedSettings());
//        createDefaultFile("application_info_settings.json", ApplicationInfo.class, new ApplicationInfo());
//    }
//
//    private <T> void createDefaultFile(String fileName, Class<T> clazz, T defaultContent) throws IOException {
//        File file = new File("config/" + fileName);
//        if (!file.exists()) {
//            // Ensure the config directory exists
//            File dir = file.getParentFile();
//            if (!dir.exists()) {
//                if (!dir.mkdirs()) {
//                    throw new IOException("Failed to create config directory: " + dir.getAbsolutePath());
//                }
//            }
//            // Create the file with default content
//            objectMapper.writeValue(file, defaultContent);
//            log.info("Created default config file: " + file.getAbsolutePath());
//        }
//    }
//
//    public void loadConfigurations() throws IOException {
//        loadApplicationSettings();
//        loadSocialAuthSettings();
//        loadMaintenanceMode();
//        loadSocialSettings();
//        loadAdvancedSettings();
//        loadApplicationInfoSettings();
//    }
//
//    public void loadApplicationSettings() throws IOException {
//        File file = new File("config/application_settings.json");
//        if (file.exists()) {
//            applicationSetting = objectMapper.readValue(file, ApplicationSetting.class);
//        } else {
//            log.warn("Configuration file not found: {}",  file.getAbsolutePath());
//            // Optionally, load default values or handle accordingly
//        }
//    }
//
//    public void loadSocialAuthSettings() throws IOException {
//        File file = new File("config/social_authentication_settings.json");
//        if (file.exists()) {
//            socialAuthSettings = objectMapper.readValue(file, SocialAuthenticationSettings.class);
//        } else {
//            log.warn("Configuration file not found: ", file.getAbsolutePath());
//        }
//    }
//
//    public void loadApplicationInfoSettings() throws IOException {
//        File file = new File("config/application_info_settings.json");
//        if (file.exists()) {
//            applicationInfo = objectMapper.readValue(file, ApplicationInfo.class);
//        } else {
//            log.warn("Configuration file not found: ", file.getAbsolutePath());
//        }
//    }
//
//    public void loadMaintenanceMode() throws IOException {
//        File file = new File("config/maintenance_mode.json");
//        if (file.exists()) {
//            maintenanceMode = objectMapper.readValue(file, MaintenanceMode.class);
//        } else {
//            log.warn("Configuration file not found: ", file.getAbsolutePath());
//        }
//    }
//
//    public void loadSocialSettings() throws IOException {
//        File file = new File("config/social_settings.json");
//        if (file.exists()) {
//            socialSettings = objectMapper.readValue(file, SocialSettings.class);
//        } else {
//            log.warn("Configuration file not found: ", file.getAbsolutePath());
//        }
//    }
//
//    public void loadAdvancedSettings() throws IOException {
//        File file = new File("config/advanced_settings.json");
//        if (file.exists()) {
//            advancedSettings = objectMapper.readValue(file, AdvancedSettings.class);
//        } else {
//            log.warn("Configuration file not found: ", file.getAbsolutePath());
//        }
//    }
//
//    public ApplicationSetting getApplicationSetting() {
//        return applicationSetting;
//    }
//
//    public SocialAuthenticationSettings getSocialAuthSettings() {
//        return socialAuthSettings;
//    }
//
//    public MaintenanceMode getMaintenanceMode() {
//        return maintenanceMode;
//    }
//
//    public SocialSettings getSocialSettings() {
//        return socialSettings;
//    }
//
//    public AdvancedSettings getAdvancedSettings() {
//        return advancedSettings;
//    }
//
//    public ApplicationInfo getApplicationInfoSettings() {
//        return applicationInfo;
//    }
//
//    public <T> ResponseEntity<String> saveConfig(String fileName, T settings) {
//        File file = new File("config/" + fileName);
//        try {
//            // Ensure the config directory exists
//            File dir = file.getParentFile();
//            if (!dir.exists()) {
//                if (!dir.mkdirs()) {
//                    return ResponseEntity.status(500).body("Failed to create config directory");
//                }
//            }
//
//            // Write settings to file
//            objectMapper.writeValue(file, settings);
//            // Reload settings in service
//            reloadSettings(fileName);
//
//            return ResponseEntity.ok("Configuration saved successfully");
//
//        } catch (IOException e) {
//            return ResponseEntity.status(500).body("Error saving configuration: " + e.getMessage());
//        }
//    }
//
//    private void reloadSettings(String fileName) throws IOException {
//        switch (fileName) {
//            case "application_settings.json":
//                loadApplicationSettings();
//                break;
//            case "social_authentication_settings.json":
//                loadSocialAuthSettings();
//                break;
//            case "maintenance_mode.json":
//                loadMaintenanceMode();
//                break;
//            case "social_settings.json":
//                loadSocialSettings();
//                break;
//            case "advanced_settings.json":
//                loadAdvancedSettings();
//                break;
//            case "application_info_settings.json":
//               loadApplicationInfoSettings();
//                break;
//            default:
//                throw new IllegalArgumentException("Unknown configuration file: " + fileName);
//        }
//    }


    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, Object> settingsMap = new HashMap<>();

    public ConfigService() throws IOException {
        ensureDefaultConfigurations();
        loadAllConfigurations();
    }

    private void ensureDefaultConfigurations() throws IOException {
        createDefaultFile("application_settings.json", ApplicationSetting.class, new ApplicationSetting());
        createDefaultFile("social_authentication_settings.json", SocialAuthenticationSettings.class, new SocialAuthenticationSettings());
        createDefaultFile("maintenance_mode.json", MaintenanceMode.class, new MaintenanceMode());
        createDefaultFile("social_settings.json", SocialSettings.class, new SocialSettings());
        createDefaultFile("advanced_settings.json", AdvancedSettings.class, new AdvancedSettings());
        createDefaultFile("application_info_settings.json", ApplicationInfo.class, new ApplicationInfo());
    }

    private <T> void createDefaultFile(String fileName, Class<T> clazz, T defaultContent) throws IOException {
        File file = new File("config/" + fileName);
        if (!file.exists()) {
            File dir = file.getParentFile();
            if (!dir.exists() && !dir.mkdirs()) {
                throw new IOException("Failed to create config directory: " + dir.getAbsolutePath());
            }
            objectMapper.writeValue(file, defaultContent);
            log.info("Created default config file: " + file.getAbsolutePath());
        }
    }

    public void loadAllConfigurations() throws IOException {
        settingsMap.put("application_settings.json", loadFile("application_settings.json", ApplicationSetting.class));
        settingsMap.put("social_authentication_settings.json", loadFile("social_authentication_settings.json", SocialAuthenticationSettings.class));
        settingsMap.put("maintenance_mode.json", loadFile("maintenance_mode.json", MaintenanceMode.class));
        settingsMap.put("social_settings.json", loadFile("social_settings.json", SocialSettings.class));
        settingsMap.put("advanced_settings.json", loadFile("advanced_settings.json", AdvancedSettings.class));
        settingsMap.put("application_info_settings.json", loadFile("application_info_settings.json", ApplicationInfo.class));
    }

    private <T> T loadFile(String fileName, Class<T> clazz) throws IOException {
        File file = new File("config/" + fileName);
        if (file.exists()) {
            return objectMapper.readValue(file, clazz);
        } else {
            log.warn("Configuration file not found: {}", file.getAbsolutePath());
            return null; // Handle nulls or provide default values as needed
        }
    }

    public void reloadConfiguration(String fileName) throws IOException {
        switch (fileName) {
            case "application_settings.json":
                settingsMap.put(fileName, loadFile(fileName, ApplicationSetting.class));
                break;
            case "social_authentication_settings.json":
                settingsMap.put(fileName, loadFile(fileName, SocialAuthenticationSettings.class));
                break;
            case "maintenance_mode.json":
                settingsMap.put(fileName, loadFile(fileName, MaintenanceMode.class));
                break;
            case "social_settings.json":
                settingsMap.put(fileName, loadFile(fileName, SocialSettings.class));
                break;
            case "advanced_settings.json":
                settingsMap.put(fileName, loadFile(fileName, AdvancedSettings.class));
                break;
            case "application_info_settings.json":
                settingsMap.put(fileName, loadFile(fileName, ApplicationInfo.class));
                break;
            default:
                log.warn("Unknown configuration file: {}", fileName);
                break;
        }
    }

    public <T> ResponseEntity<String> saveConfig(String fileName, T settings) {
        File file = new File("config/" + fileName);
        try {
            File dir = file.getParentFile();
            if (!dir.exists() && !dir.mkdirs()) {
                return ResponseEntity.status(500).body("Failed to create config directory");
            }
            objectMapper.writeValue(file, settings);
            // Reload settings in service
            settingsMap.put(fileName, settings);
            return ResponseEntity.ok("Configuration saved successfully");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error saving configuration: " + e.getMessage());
        }
    }

    public ApplicationSetting getApplicationSetting() {
        return (ApplicationSetting) settingsMap.get("application_settings.json");
    }

    public SocialAuthenticationSettings getSocialAuthSettings() {
        return (SocialAuthenticationSettings) settingsMap.get("social_authentication_settings.json");
    }

    public MaintenanceMode getMaintenanceMode() {
        return (MaintenanceMode) settingsMap.get("maintenance_mode.json");
    }

    public SocialSettings getSocialSettings() {
        return (SocialSettings) settingsMap.get("social_settings.json");
    }

    public AdvancedSettings getAdvancedSettings() {
        return (AdvancedSettings) settingsMap.get("advanced_settings.json");
    }

    public ApplicationInfo getApplicationInfoSettings() {
        return (ApplicationInfo) settingsMap.get("application_info_settings.json");
    }
}
