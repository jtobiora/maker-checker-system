package com.swiftfingers.makercheckersystem.controller.upload_configurations.conf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swiftfingers.makercheckersystem.controller.upload_configurations.classes.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

/**
 * Created by Obiora on 30-Jul-2024 at 10:44
 */
@Service
public class ConfigService {


    private final ObjectMapper objectMapper = new ObjectMapper();

    private ApplicationSetting applicationSetting;
    private SocialAuthenticationSettings socialAuthSettings;
    private MaintenanceMode maintenanceMode;
    private SocialSettings socialSettings;
    private AdvancedSettings advancedSettings;
    private ApplicationInfo applicationInfo;

    public ConfigService() throws IOException {
        ensureDefaultConfigurations();
        loadConfigurations();
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
            // Ensure the config directory exists
            File dir = file.getParentFile();
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    throw new IOException("Failed to create config directory: " + dir.getAbsolutePath());
                }
            }
            // Create the file with default content
            objectMapper.writeValue(file, defaultContent);
            System.out.println("Created default config file: " + file.getAbsolutePath());
        }
    }

    public void loadConfigurations() throws IOException {
        loadApplicationSettings();
        loadSocialAuthSettings();
        loadMaintenanceMode();
        loadSocialSettings();
        loadAdvancedSettings();
        loadApplicationInfoSettings();
    }

    public void loadApplicationSettings() throws IOException {
        File file = new File("config/application_settings.json");
        if (file.exists()) {
            applicationSetting = objectMapper.readValue(file, ApplicationSetting.class);
        } else {
            System.err.println("Configuration file not found: " + file.getAbsolutePath());
            // Optionally, load default values or handle accordingly
        }
    }

    public void loadSocialAuthSettings() throws IOException {
        File file = new File("config/social_authentication_settings.json");
        if (file.exists()) {
            socialAuthSettings = objectMapper.readValue(file, SocialAuthenticationSettings.class);
        } else {
            System.err.println("Configuration file not found: " + file.getAbsolutePath());
            // Optionally, load default values or handle accordingly
        }
    }

    public void loadApplicationInfoSettings() throws IOException {
        File file = new File("config/application_info_settings.json");
        if (file.exists()) {
            applicationInfo = objectMapper.readValue(file, ApplicationInfo.class);
        } else {
            System.err.println("Configuration file not found: " + file.getAbsolutePath());
            // Optionally, load default values or handle accordingly
        }
    }

    public void loadMaintenanceMode() throws IOException {
        File file = new File("config/maintenance_mode.json");
        if (file.exists()) {
            maintenanceMode = objectMapper.readValue(file, MaintenanceMode.class);
        } else {
            System.err.println("Configuration file not found: " + file.getAbsolutePath());
            // Optionally, load default values or handle accordingly
        }
    }

    public void loadSocialSettings() throws IOException {
        File file = new File("config/social_settings.json");
        if (file.exists()) {
            socialSettings = objectMapper.readValue(file, SocialSettings.class);
        } else {
            System.err.println("Configuration file not found: " + file.getAbsolutePath());
            // Optionally, load default values or handle accordingly
        }
    }

    public void loadAdvancedSettings() throws IOException {
        File file = new File("config/advanced_settings.json");
        if (file.exists()) {
            advancedSettings = objectMapper.readValue(file, AdvancedSettings.class);
        } else {
            System.err.println("Configuration file not found: " + file.getAbsolutePath());
            // Optionally, load default values or handle accordingly
        }
    }

    public ApplicationSetting getApplicationSetting() {
        return applicationSetting;
    }

    public SocialAuthenticationSettings getSocialAuthSettings() {
        return socialAuthSettings;
    }

    public MaintenanceMode getMaintenanceMode() {
        return maintenanceMode;
    }

    public SocialSettings getSocialSettings() {
        return socialSettings;
    }

    public AdvancedSettings getAdvancedSettings() {
        return advancedSettings;
    }

    public ApplicationInfo getApplicationInfoSettings() {
        return applicationInfo;
    }

    public <T> ResponseEntity<String> saveConfig(String fileName, T settings) {
        File file = new File("config/" + fileName);
        try {
            // Ensure the config directory exists
            File dir = file.getParentFile();
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    return ResponseEntity.status(500).body("Failed to create config directory");
                }
            }

            // Write settings to file
            objectMapper.writeValue(file, settings);
            // Reload settings in service
            reloadSettings(fileName);

            return ResponseEntity.ok("Configuration saved successfully");

        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error saving configuration: " + e.getMessage());
        }
    }

    private void reloadSettings(String fileName) throws IOException {
        switch (fileName) {
            case "application_settings.json":
                loadApplicationSettings();
                break;
            case "social_authentication_settings.json":
                loadSocialAuthSettings();
                break;
            case "maintenance_mode.json":
                loadMaintenanceMode();
                break;
            case "social_settings.json":
                loadSocialSettings();
                break;
            case "advanced_settings.json":
                loadAdvancedSettings();
                break;
            case "application_info_settings.json":
               loadApplicationInfoSettings();
                break;
            default:
                throw new IllegalArgumentException("Unknown configuration file: " + fileName);
        }
    }
}
