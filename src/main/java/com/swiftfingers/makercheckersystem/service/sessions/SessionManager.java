package com.swiftfingers.makercheckersystem.service.sessions;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@Slf4j
@RequiredArgsConstructor
public class SessionManager {
    private static final String KEY = "user-session:sessions:";

    private final RedisTemplate<String, String> redisTemplate;

    private HashOperations hashOperations;

    @Value("${session-timeout}")
    private Long sessionTimeout;

    @PostConstruct
    private void init(){
        hashOperations = redisTemplate.opsForHash();
    }

    public void updateSessionTimeout(String sessionId) {
        hashOperations.getOperations().expire(KEY+sessionId,sessionTimeout, TimeUnit.SECONDS);
    }
    public boolean isSessionValid(String sessionId) {

        return hashOperations.getOperations().hasKey(KEY+sessionId);
    }

    public void deleteSession(String sessionId) {
        log.debug("Deleting user session {}",  KEY+sessionId);
        hashOperations.getOperations().delete(KEY+sessionId);
    }

    public boolean isSessionExpired(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // false means do not create a new session if it doesn't exist
        if (session != null) {
            long currentTime = System.currentTimeMillis();
            long lastAccessedTime = session.getLastAccessedTime();
            int maxInactiveInterval = session.getMaxInactiveInterval();
            long sessionDuration = currentTime - lastAccessedTime;
            if (sessionDuration > (maxInactiveInterval * 1000L)) { // Convert seconds to milliseconds
                return true;
            } else {
                // Update session time
                session.setMaxInactiveInterval(maxInactiveInterval);
                return false;
            }
        }
        return true; // If session is null, consider it expired
    }
}
