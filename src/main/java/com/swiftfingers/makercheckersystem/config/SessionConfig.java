package com.swiftfingers.makercheckersystem.config;

import com.swiftfingers.makercheckersystem.constants.AppConstants;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer;

import static com.swiftfingers.makercheckersystem.constants.AppConstants.MAX_SESSION_IDLE_TIME_SECONDS;

/**
 * With this configuration, sessions will expire after 10 minutes of inactivity, and session attributes will be stored
 * in Redis under the namespace "user-session".
 * ***/

@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = MAX_SESSION_IDLE_TIME_SECONDS, redisNamespace = "user-session")
public class SessionConfig extends AbstractHttpSessionApplicationInitializer {

}
