package com.swiftfingers.makercheckersystem.utils.cron;

import com.swiftfingers.makercheckersystem.repository.PendingActionRepository;
import com.swiftfingers.makercheckersystem.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

/**
 * Created by Obiora on 30-May-2024 at 18:00
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DailyJob extends QuartzJobBean {

    private final NotificationService notificationService;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {

       notificationService.notifyAuthorizers();
    }
}
