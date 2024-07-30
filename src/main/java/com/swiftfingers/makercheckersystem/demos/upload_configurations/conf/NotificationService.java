package com.swiftfingers.makercheckersystem.demos.upload_configurations.conf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swiftfingers.makercheckersystem.demos.upload_configurations.classes.NotificationsSetting;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Obiora on 30-Jul-2024 at 16:14
 */
@Service
@Slf4j
public class NotificationService {

//    private final ObjectMapper objectMapper = new ObjectMapper();
//    private final File file = new File("config/notifications_settings.json");
//    private Map<String, NotificationsSetting> notificationsMap = new HashMap<>();
//
//    public NotificationService() throws IOException {
//        initializeFile();
//        loadSettings();
//    }
//
//    private void initializeFile() throws IOException {
//        if (!file.exists()) {
//            // Ensure the config directory exists
//            File dir = file.getParentFile();
//            if (!dir.exists() && !dir.mkdirs()) {
//                throw new IOException("Failed to create config directory: " + dir.getAbsolutePath());
//            }
//            // Create the file with an empty JSON object
//            objectMapper.writeValue(file, new HashMap<String, NotificationsSetting>());
//            log.info("Created default notifications settings file: " + file.getAbsolutePath());
//        }
//    }
//
//    private void loadSettings() throws IOException {
//        // Load settings from file into map
//        notificationsMap = objectMapper.readValue(file, objectMapper.getTypeFactory().constructMapLikeType(HashMap.class, String.class, NotificationsSetting.class));
//        log.info("Loaded settings from file.");
//    }
//
//    private void saveSettings() throws IOException {
//        objectMapper.writeValue(file, notificationsMap);
//        log.info("Saved settings to file.");
//    }
//
//    public void addOrUpdateSetting(String email, NotificationsSetting setting) throws IOException {
//        if (notificationsMap.containsKey(email)) {
//            log.info("Updating notification settings for email: {}", email);
//        } else {
//            log.info("Adding new notification settings for email: {}", email);
//        }
//        notificationsMap.put(email, setting);
//        saveSettings();
//    }
//
//    public NotificationsSetting getSetting(String email) {
//        return notificationsMap.get(email);
//    }
//
//    public void deleteSetting(String email) throws IOException {
//        if (notificationsMap.remove(email) != null) {
//            saveSettings();
//            log.info("Deleted notification settings for email: {}", email);
//        } else {
//            log.warn("No settings found for email: {}", email);
//        }
//    }

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final File file = new File("config/notifications_settings.json");
    private Map<String, NotificationsSetting> notificationsMap = new HashMap<>();

    public NotificationService() throws IOException {
        initializeFile();
        loadSettings();
    }

    private void initializeFile() throws IOException {
        if (!file.exists()) {
            // Ensure the config directory exists
            File dir = file.getParentFile();
            if (!dir.exists() && !dir.mkdirs()) {
                throw new IOException("Failed to create config directory: " + dir.getAbsolutePath());
            }
            // Create the file with an empty JSON object
            objectMapper.writeValue(file, new HashMap<String, NotificationsSetting>());
            log.info("Created default notifications settings file: " + file.getAbsolutePath());
        }
    }

    private void loadSettings() throws IOException {
        // Load settings from file into map
        notificationsMap = objectMapper.readValue(file, objectMapper.getTypeFactory().constructMapLikeType(Map.class, String.class, NotificationsSetting.class));
        log.info("Loaded settings from file.");
    }

    private void saveSettings() throws IOException {
        objectMapper.writeValue(file, notificationsMap);
        log.info("Saved settings to file.");
    }

    public void addOrUpdateSetting(String email, NotificationsSetting setting) throws IOException {
        notificationsMap.put(email, setting);
        saveSettings();
        log.info("Updated notification settings for email: {}", email);
    }

    public NotificationsSetting getSetting(String email) {
        return notificationsMap.get(email);
    }

    public void deleteSetting(String email) throws IOException {
        if (notificationsMap.remove(email) != null) {
            saveSettings();
            log.info("Deleted notification settings for email: {}", email);
        } else {
            log.warn("No settings found for email: {}", email);
        }
    }

    public Map<String, NotificationsSetting> getAllSettings() {
        return notificationsMap;
    }
}
