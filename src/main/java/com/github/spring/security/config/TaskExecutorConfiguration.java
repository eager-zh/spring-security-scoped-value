package com.github.spring.security.config;

import com.github.spring.security.annotation.FutureTaskExecutor;
import com.github.spring.security.security.notmodified.DelegatingSecurityContextExecutor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.task.SimpleAsyncTaskExecutorBuilder;
import org.springframework.boot.task.ThreadPoolTaskExecutorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration(proxyBeanMethods = false)
public class TaskExecutorConfiguration {

    public static final String MVC_TASK_EXECUTOR_BEAN_NAME = "mvcTaskExecutor";

    public static final String ASYNC_TASK_EXECUTOR_BEAN_NAME = "asyncTaskExecutor";

    public static final String ASYNC_DELEGATING_SECURITY_CONTEXT_TASK_EXECUTOR_BEAN_NAME = "asyncDelegatingSecurityContextTaskExecutor";

    public static final String FUTURE_TASK_EXECUTOR_BEAN_NAME = "futureTaskExecutor";

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
        return builder
                .threadNamePrefix("async-")
                .build();
    }

    @Bean(ASYNC_DELEGATING_SECURITY_CONTEXT_TASK_EXECUTOR_BEAN_NAME)
    DelegatingSecurityContextExecutor asyncDelegatingSecurityContextTaskExecutor(@Qualifier(ASYNC_TASK_EXECUTOR_BEAN_NAME) ThreadPoolTaskExecutor taskExecutor) {
        return new DelegatingSecurityContextExecutor(taskExecutor);
    }

    @Bean(FUTURE_TASK_EXECUTOR_BEAN_NAME)
    ThreadPoolTaskExecutor futureThreadPoolTaskExecutor(ThreadPoolTaskExecutorBuilder builder) {
        return builder
                .threadNamePrefix("future-")
                .build();
    }


    @Bean
    @FutureTaskExecutor
    DelegatingSecurityContextExecutor futureDelegatingThreadPoolTaskExecutor(@Qualifier(FUTURE_TASK_EXECUTOR_BEAN_NAME) ThreadPoolTaskExecutor taskExecutor) {
        return new DelegatingSecurityContextExecutor(taskExecutor);
    }
}
