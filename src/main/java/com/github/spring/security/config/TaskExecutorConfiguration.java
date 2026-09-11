package com.github.spring.security.config;

import com.github.spring.security.annotation.FutureTaskExecutor;
import org.springframework.boot.task.SimpleAsyncTaskExecutorBuilder;
import org.springframework.boot.task.ThreadPoolTaskExecutorBuilder;
import org.springframework.boot.task.ThreadPoolTaskSchedulerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration(proxyBeanMethods = false)
public class TaskExecutorConfiguration {

    public static final String MVC_TASK_EXECUTOR_BEAN_NAME = "mvcTaskExecutor";

    public static final String ASYNC_TASK_EXECUTOR_BEAN_NAME = "asyncTaskExecutor";

    // SimpleAsyncTaskExecutorBuilder (ThreadPoolTaskExecutorBuilder, SimpleAsyncTaskSchedulerBuilder, ThreadPoolTaskSchedulerBuilder)
    // These classes have all been successfully auto-configured, and they all support customizers similar to SimpleAsyncTaskExecutorCustomizer, and will also automatically configure TaskDecorator
    // Finally, you only need to use the already configured builder to customize your configuration and finally build the thread pool you want
    // For details of the auto-configuration steps, see TaskExecutorConfigurations

    @Bean(MVC_TASK_EXECUTOR_BEAN_NAME)
    SimpleAsyncTaskExecutor mvcSimpleAsyncTaskExecutor(SimpleAsyncTaskExecutorBuilder builder) {
        return builder
                .threadNamePrefix("mvc-")
                .build();
    }

    @Bean(ASYNC_TASK_EXECUTOR_BEAN_NAME)
    ThreadPoolTaskExecutor asyncThreadPoolTaskExecutor(ThreadPoolTaskExecutorBuilder builder) {
        // async-pool-task-executor-
        return builder
                .threadNamePrefix("async-")
                .build();
    }

    @Bean
    @FutureTaskExecutor
    ThreadPoolTaskExecutor futureThreadPoolTaskExecutor(ThreadPoolTaskExecutorBuilder builder) {
        return builder
                .threadNamePrefix("future-")
                .build();
    }

}
