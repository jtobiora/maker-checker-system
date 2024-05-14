package com.swiftfingers.makercheckersystem;

import com.swiftfingers.makercheckersystem.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MakerCheckerSystemApplication {
	public static void main(String[] args) {
		SpringApplication.run(MakerCheckerSystemApplication.class, args);
	}

}
