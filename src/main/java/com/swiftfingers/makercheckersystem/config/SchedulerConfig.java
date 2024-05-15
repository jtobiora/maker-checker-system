package com.swiftfingers.makercheckersystem.config;

import org.springframework.boot.autoconfigure.quartz.SchedulerFactoryBeanCustomizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
public class SchedulerConfig implements SchedulerFactoryBeanCustomizer {

    
    @Override
    public void customize(SchedulerFactoryBean schedulerFactoryBean) {

    }
}
