package com.swiftfingers.makercheckersystem;

import com.swiftfingers.makercheckersystem.controller.upload_configurations.conf.ConfigService;
import com.swiftfingers.makercheckersystem.controller.upload_configurations.conf.FileWatcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

import java.io.IOException;

@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
//@EnableCaching
public class MakerCheckerSystemApplication implements CommandLineRunner{

	private final FileWatcher fileWatcher;
	private final ConfigService configService;
	public static void main(String[] args) {
		SpringApplication.run(MakerCheckerSystemApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		try {
			configService.loadConfigurations();  // Ensure configurations are loaded
			fileWatcher.startWatching();
		} catch (IOException e) {
			log.error("Error starting file watcher: " + e.getMessage());
		}
	}
}
