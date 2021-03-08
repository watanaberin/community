package com.social.community.community;

import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class ThreadPoolTests {

    private static final Logger logger= LoggerFactory.getLogger(ThreadPoolTests.class);
    //普通JDK线程池
    private ExecutorService executorService= Executors.newFixedThreadPool(5);
    //可执行定时任务的线程池
    private ScheduledExecutorService scheduledExecutorService=Executors.newScheduledThreadPool(5);




}
