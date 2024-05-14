package com.swiftfingers.makercheckersystem.service.redis;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

import static com.swiftfingers.makercheckersystem.constants.AppConstants.MAX_SESSION_IDLE_TIME_SECONDS;

@Repository
@Slf4j
@RequiredArgsConstructor
public class LoginTokenService {

    private static final String KEY = "user-token";

    private final RedisTemplate<String, String> redisTemplate;

    private HashOperations hashOperations;

    @Value("${token-timeout}")
    private int tokenTimeout;

    @PostConstruct
    private void init(){
        hashOperations = redisTemplate.opsForHash();
    }

    public Object findUserLoginToken(String sessionId) {
        return hashOperations.get(KEY+sessionId, sessionId);
    }

    public boolean saveUserLoginToken(String sessionId, String userToken) {
        if (StringUtils.isEmpty(userToken)){
            log.error("token is null or undefined");
            return false;
        }
        hashOperations.put(KEY+sessionId, sessionId , userToken);
        updateTokenTimeout(sessionId); // Update token expiry time
        return true;
    }

    public boolean isValidUserLoginToken(String sessionId) {
        Object token = this.findUserLoginToken(sessionId);
        return token != null;
    }

    public void updateTokenTimeout(String sessionId) {
        redisTemplate.expire(KEY + sessionId, MAX_SESSION_IDLE_TIME_SECONDS, TimeUnit.SECONDS);
    }


    public void deleteUserLoginToken(String sessionId) {
        hashOperations.delete(KEY + sessionId, sessionId);
    }



}
