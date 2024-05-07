package com.swiftfingers.makercheckersystem.runners;

import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AppInit {

    private final ApplicationContext applicationContext;

    public void simulateContextRefresh() {
        // Refresh the application context programmatically to seed data into database
        applicationContext.publishEvent(new ContextRefreshedEvent(applicationContext));
    }
}
