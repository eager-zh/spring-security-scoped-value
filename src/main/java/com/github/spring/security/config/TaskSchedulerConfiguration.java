package com.github.spring.security.config;

import org.springframework.boot.task.ThreadPoolTaskSchedulerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import com.github.spring.security.strategy.ScopedSecurityContextHolderStrategy;

@EnableScheduling
@Configuration(proxyBeanMethods = false)
public class TaskSchedulerConfiguration {

    public static final String THREAD_POOL_TASK_SCHEDULER_BEAN_NAME = "threadPoolTaskScheduler";
 
    @Bean(THREAD_POOL_TASK_SCHEDULER_BEAN_NAME)
    ThreadPoolTaskScheduler threadPoolTaskScheduler(ThreadPoolTaskSchedulerBuilder builder) {
        return builder
                .poolSize(1)
                .taskDecorator(ScopedSecurityContextHolderStrategy.getTaskDecorator())
                .threadNamePrefix("scheduler-")
                .build();
    }

}
