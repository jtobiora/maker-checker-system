package com.swiftfingers.makercheckersystem.demos.upload_configurations.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swiftfingers.makercheckersystem.demos.upload_configurations.classes.NotificationsSetting;
import com.swiftfingers.makercheckersystem.demos.upload_configurations.conf.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Obiora on 30-Jul-2024 at 16:15
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationsController {

//    private final NotificationService notificationsService;
//    private final ObjectMapper objectMapper = new ObjectMapper();
//    private static final String FILE_PATH = "config/notifications_settings.json";
//
//    public NotificationsController(NotificationService notificationsService) {
//        this.notificationsService = notificationsService;
//    }
//
//    @PostMapping("/update")
//    public ResponseEntity<String> updateNotificationSettings(@RequestBody NotificationsSetting settings) {
//        try {
//            notificationsService.addOrUpdateSetting(settings.getEmail(), settings);
//            return ResponseEntity.ok("Notification settings updated successfully.");
//        } catch (IOException e) {
//            return ResponseEntity.status(500).body("Error updating notification settings: " + e.getMessage());
//        }
//    }
//
//    @GetMapping("/all")
//    public List<NotificationsSetting> getAllNotificationSettings() {
//        try {
//            File file = new File(FILE_PATH);
//            if (file.exists()) {
//                // Read the JSON file into a list of NotificationsSetting
//                return objectMapper.readValue(file, objectMapper.getTypeFactory().constructCollectionType(List.class, NotificationsSetting.class));
//            } else {
//                return List.of(); // Return an empty list if the file does not exist
//            }
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to read notification settings file", e);
//        }
//    }
//
//    @GetMapping("/get/{email}")
//    public ResponseEntity<NotificationsSetting> getNotificationSettings(@PathVariable String email) {
//        NotificationsSetting setting = notificationsService.getSetting(email);
//        if (setting != null) {
//            return ResponseEntity.ok(setting);
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }
//
//    @DeleteMapping("/delete/{email}")
//    public ResponseEntity<String> deleteNotificationSettings(@PathVariable String email) {
//        try {
//            notificationsService.deleteSetting(email);
//            return ResponseEntity.ok("Notification settings deleted successfully.");
//        } catch (IOException e) {
//            return ResponseEntity.status(500).body("Error deleting notification settings: " + e.getMessage());
//        }
//    }

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String FILE_PATH = "config/notifications_settings.json";

    public NotificationsController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateNotificationSettings(@RequestBody NotificationsSetting settings) {
        try {
            notificationService.addOrUpdateSetting(settings.getEmail(), settings);
            return ResponseEntity.ok("Notification settings updated successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error updating notification settings: " + e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<NotificationsSetting>> getAllNotificationSettings() {
        try {
            List<NotificationsSetting> settingsList = notificationService.getAllSettings().values().stream().toList();
            return ResponseEntity.ok(settingsList);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(List.of());
        }
    }

    @GetMapping("/get/{email}")
    public ResponseEntity<NotificationsSetting> getNotificationSettings(@PathVariable String email) {
        NotificationsSetting setting = notificationService.getSetting(email);
        if (setting != null) {
            return ResponseEntity.ok(setting);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete/{email}")
    public ResponseEntity<String> deleteNotificationSettings(@PathVariable String email) {
        try {
            notificationService.deleteSetting(email);
            return ResponseEntity.ok("Notification settings deleted successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error deleting notification settings: " + e.getMessage());
        }
    }
}
