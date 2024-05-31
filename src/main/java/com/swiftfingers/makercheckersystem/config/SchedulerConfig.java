package com.swiftfingers.makercheckersystem.config;

import com.swiftfingers.makercheckersystem.utils.cron.DailyJob;
import org.quartz.*;
import org.springframework.boot.autoconfigure.quartz.SchedulerFactoryBeanCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
public class SchedulerConfig implements SchedulerFactoryBeanCustomizer {

    @Bean
    public JobDetail hourlyJobDetail() {
        return JobBuilder.newJob(DailyJob.class).withIdentity("hourly-job").storeDurably(true).build();
    }

    @Bean
    public Trigger hourlyJobTrigger() {
        return TriggerBuilder.newTrigger().forJob(hourlyJobDetail())
                .withIdentity("hourlyTrigger")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 * * * ?"))
                .build();
    }

    @Override
    public void customize(SchedulerFactoryBean schedulerFactoryBean) {

    }
}
