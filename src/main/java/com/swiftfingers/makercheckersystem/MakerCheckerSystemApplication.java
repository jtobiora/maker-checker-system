package com.swiftfingers.makercheckersystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
//@EnableCaching
public class MakerCheckerSystemApplication {
	public static void main(String[] args) {
		SpringApplication.run(MakerCheckerSystemApplication.class, args);
	}

}
