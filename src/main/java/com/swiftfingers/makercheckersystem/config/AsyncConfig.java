package com.swiftfingers.makercheckersystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Created by Obiora on 29-May-2024 at 11:28
 */

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);  // The number of threads to keep in the pool, even if they are idle
        executor.setMaxPoolSize(10); // The maximum number of threads to allow in the pool
        executor.setQueueCapacity(500); // The capacity of the queue to use for holding tasks before they are executed
        executor.setThreadNamePrefix("Async-");  // The prefix for the names of the threads in the pool
        executor.initialize();  // Initialize the executor
        return executor;
    }
}
