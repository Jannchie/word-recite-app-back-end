package com.jannchie.word.schedule;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jannchie
 */
public class StatisticScheduler {
    @Scheduled(cron = "0 0 3 * * ?")
    @Async
    public void recordSpiderQueueStatus() {

    }
}
