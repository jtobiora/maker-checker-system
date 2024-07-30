package com.swiftfingers.makercheckersystem.demos.upload_configurations.conf;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * Created by Obiora on 30-Jul-2024 at 11:03
 */
@Configuration
@RequiredArgsConstructor
public class FileWatcherConfiguration {

    private final ConfigService configService;

    @Bean(name = "customFileWatcher")
    public FileWatcher fileWatcher() throws IOException {
        FileWatcher fileWatcher = new FileWatcher(configService);
        return fileWatcher;
    }
}
