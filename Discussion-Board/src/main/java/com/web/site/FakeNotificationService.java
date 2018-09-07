package com.web.site;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Async;

import java.util.Collection;

public class FakeNotificationService implements NotificationService {

    private static Logger log = LogManager.getLogger();

    @Override
    @Async
    public void sendNotification(String subject, String message, Collection<String> recipients) {
        log.info("Started notifying recipients {}.", recipients);
        try{
            Thread.sleep(5_000L);
        } catch (InterruptedException ignore) { }

        log.info("Finished notifying recipients.");
    }
}
