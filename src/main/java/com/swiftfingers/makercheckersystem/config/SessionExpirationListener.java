package com.swiftfingers.makercheckersystem.config;

import com.swiftfingers.makercheckersystem.service.redis.TokenCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.context.event.EventListener;
import org.springframework.session.events.SessionDestroyedEvent;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SessionExpirationListener {

    private final TokenCacheService tokenCacheService;

    @EventListener
    public void onSessionExpired(SessionDestroyedEvent event) {
        String sessionId = event.getSessionId();
        // Handle session expiration event
        System.out.println("Session expired: " + sessionId);
        // Add your custom logic here
    }

}
