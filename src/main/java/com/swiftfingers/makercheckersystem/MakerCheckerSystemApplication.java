package com.swiftfingers.makercheckersystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MakerCheckerSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(MakerCheckerSystemApplication.class, args);
	}

}
