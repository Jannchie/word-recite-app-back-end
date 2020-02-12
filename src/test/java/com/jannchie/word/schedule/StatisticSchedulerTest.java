package com.jannchie.word.schedule;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class StatisticSchedulerTest {

    @Autowired
    StatisticScheduler statisticScheduler;


    @Test
    void testRecordStatisticUserRecite() {
        //statisticScheduler.recordStatisticUserRecite();
    }
}