package com.swiftfingers.makercheckersystem.controller.upload_configurations.conf;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Obiora on 30-Jul-2024 at 10:50
 */

@Slf4j
public class FileWatcher {
//    private final ConfigService configService;
//    private final Map<WatchKey, Path> keyMap = new HashMap<>();
//
//    public FileWatcher(ConfigService configService) {
//        this.configService = configService;
//    }
//
//    public void startWatching() throws IOException {
//        log.info("Started watching files in `uploads` folder for changes");
//        Path path = Paths.get("config");
//        // Ensure the config directory exists
//        if (!Files.exists(path)) {
//            try {
//                Files.createDirectories(path);
//                log.info("Config directory created: " + path.toAbsolutePath());
//            } catch (IOException e) {
//                log.error("Failed to create config directory: " + e.getMessage());
//                throw e;
//            }
//        } else if (!Files.isDirectory(path)) {
//            throw new IOException("Config path is not a directory: " + path.toAbsolutePath());
//        }
//
//        WatchService watchService = FileSystems.getDefault().newWatchService();
//        WatchKey watchKey = path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
//        keyMap.put(watchKey, path);
//        log.info("Started watching directory: " + path.toAbsolutePath());
//
//        // Monitor the directory for changes
//        while (true) {
//            WatchKey key;
//            try {
//                key = watchService.take(); // Blocking call
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//                return;
//            }
//
//
//            Path dir = keyMap.get(key);
//            if (dir == null) {
//                log.error("WatchKey not recognized! Re-registering directory.");
//                try {
//                    // Re-register the directory with the WatchService
//                    WatchKey newKey = dir.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
//                    keyMap.put(newKey, dir);
//                } catch (IOException e) {
//                    log.error("Failed to re-register directory with WatchService: " + e.getMessage());
//                    continue;
//                }
//                continue;
//            }
//
//            for (WatchEvent<?> event : key.pollEvents()) {
//                WatchEvent.Kind<?> kind = event.kind();
//                if (kind == StandardWatchEventKinds.OVERFLOW) {
//                    continue;
//                }
//
//                WatchEvent<Path> ev = (WatchEvent<Path>) event;
//                Path fileName = ev.context();
//
//                try {
//                    if (fileName.toString().equals("application_settings.json")) {
//                        log.info("+++++++++++++++++++++++ Loading application settings ++++++++++++++++");
//                        configService.loadApplicationSettings();
//                    } else if (fileName.toString().equals("social_authentication_settings.json")) {
//                        log.info("+++++++++++++++++++++++ loading authentication settings ++++++++++++++++");
//                        configService.loadSocialAuthSettings();
//                    } else if (fileName.toString().equals("maintenance_mode.json")) {
//                        log.info("+++++++++++++++++++++++ loading maintenace mode ++++++++++++++++");
//                        configService.loadMaintenanceMode();
//                    } else if (fileName.toString().equals("social_settings.json")) {
//                        log.info("+++++++++++++++++++++++ loading social settings ++++++++++++++++");
//                        configService.loadSocialSettings();
//                    } else if (fileName.toString().equals("advanced_settings.json")) {
//                        log.info("+++++++++++++++++++++++ loading advanced settings ++++++++++++++++");
//                        configService.loadAdvancedSettings();
//                    } else if (fileName.toString().equals("application_info_settings.json")) {
//                        log.info("+++++++++++++++++++++++ loading application info settings ++++++++++++++++");
//                        configService.loadApplicationInfoSettings();
//                    }
//                } catch (IOException e) {
//                    log.error("Error reloading file " + fileName + ": " + e.getMessage());
//                }
//            }
//
//            boolean valid = key.reset();
//            if (!valid) {
//                keyMap.remove(key);
//                if (keyMap.isEmpty()) {
//                    break;
//                }
//            }
//        }
//    }


    private final ConfigService configService;
    private final Map<String, Runnable> fileHandlers = new HashMap<>();
    private final Map<WatchKey, Path> keyMap = new HashMap<>();

    public FileWatcher(ConfigService configService) {
        this.configService = configService;
        initializeFileHandlers();
    }

    private void initializeFileHandlers() {
        fileHandlers.put("application_settings.json", () -> reloadConfiguration("application_settings.json"));
        fileHandlers.put("social_authentication_settings.json", () -> reloadConfiguration("social_authentication_settings.json"));
        fileHandlers.put("maintenance_mode.json", () -> reloadConfiguration("maintenance_mode.json"));
        fileHandlers.put("social_settings.json", () -> reloadConfiguration("social_settings.json"));
        fileHandlers.put("advanced_settings.json", () -> reloadConfiguration("advanced_settings.json"));
        fileHandlers.put("application_info_settings.json", () -> reloadConfiguration("application_info_settings.json"));
    }

    private void reloadConfiguration(String fileName) {
        try {
            configService.reloadConfiguration(fileName);
        } catch (IOException e) {
            log.error("Error reloading configuration file {}: {}", fileName, e.getMessage());
        }
    }

    public void startWatching() throws IOException {
        Path path = Paths.get("config");
        // Ensure the config directory exists
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
                log.info("Config directory created: " + path.toAbsolutePath());
            } catch (IOException e) {
                log.error("Failed to create config directory: " + e.getMessage());
                throw e;
            }
        } else if (!Files.isDirectory(path)) {
            throw new IOException("Config path is not a directory: " + path.toAbsolutePath());
        }

        WatchService watchService = FileSystems.getDefault().newWatchService(); //Creates a WatchService instance to monitor file changes.

       //Registers the directory with the WatchService to monitor modifications (ENTRY_MODIFY) to files in that directory.
        WatchKey watchKey = path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

        keyMap.put(watchKey, path);
        log.info("Started watching directory: " + path.toAbsolutePath());

        // Monitor the directory for changes
        while (true) {
            WatchKey key;
            try {
               // Blocking call that waits for a key to be available. This key represents a change event.
                key = watchService.take();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }

            Path dir = keyMap.get(key);
            if (dir == null) {
                log.error("WatchKey not recognized! Re-registering directory.");
                try {
                    WatchKey newKey = dir.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
                    keyMap.put(newKey, dir);
                } catch (IOException e) {
                    log.error("Failed to re-register directory with WatchService: " + e.getMessage());
                }
                continue;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();
                if (kind == StandardWatchEventKinds.OVERFLOW) {
                    continue;
                }

                WatchEvent<Path> ev = (WatchEvent<Path>) event;
                Path fileName = ev.context();
                String fileNameStr = fileName.toString();

                Runnable handler = fileHandlers.get(fileNameStr);
                if (handler != null) {
                    handler.run();
                } else {
                    log.warn("No handler for file: {}", fileNameStr);
                }
            }

            boolean valid = key.reset();
            if (!valid) {
                keyMap.remove(key);
                if (keyMap.isEmpty()) {
                    break;
                }
            }
        }
    }
}