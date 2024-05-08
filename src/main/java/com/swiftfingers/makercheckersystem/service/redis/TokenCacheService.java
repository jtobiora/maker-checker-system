package com.swiftfingers.makercheckersystem.service.redis;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Repository
@Slf4j
@RequiredArgsConstructor
public class TokenCacheService {

    private static final String KEY = "user-token";

    private final RedisTemplate<String, String> redisTemplate;

    private HashOperations hashOperations;

    @Value("${token-timeout}")
    private int tokenTimeout;

    @PostConstruct
    private void init(){
        hashOperations = redisTemplate.opsForHash();
    }

    public Object findUserToken(String sessionId) {
        return hashOperations.get(KEY+sessionId, sessionId);
    }

    public boolean saveUserToken(String sessionId, String userToken) {
        if (StringUtils.isEmpty(userToken)){
            log.error("token is null or undefined");
            return false;
        }
        hashOperations.put(KEY+sessionId, sessionId , userToken);
        redisTemplate.expire(KEY+sessionId, tokenTimeout, TimeUnit.SECONDS);
        return true;
    }

    public boolean isValidUserToken(String sessionId) {
        Object token = this.findUserToken(sessionId);
        return token != null;
    }

    public void deleteUserToken(String sessionId) {
        Set keys = hashOperations.keys(KEY + sessionId);
        keys.forEach(o -> {
            hashOperations.delete(KEY + sessionId, o);
        });
    }
}
