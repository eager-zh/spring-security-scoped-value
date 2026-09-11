package com.github.spring.security.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.Assert;

import java.util.concurrent.Executor;

@EnableAsync
@Configuration(proxyBeanMethods = false)
public class AsyncConfiguration implements AsyncConfigurer, InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(AsyncConfiguration.class);

    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Autowired
    @Qualifier(TaskExecutorConfiguration.ASYNC_TASK_EXECUTOR_BEAN_NAME)
    public void setAsyncTaskExecutor(ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        Assert.notNull(threadPoolTaskExecutor, "'threadPoolTaskExecutor' must be not null");
        this.threadPoolTaskExecutor = threadPoolTaskExecutor;
    }

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(threadPoolTaskExecutor, "'threadPoolTaskExecutor' must be not null");
    }

    @Override
    public @Nullable Executor getAsyncExecutor() {
        return threadPoolTaskExecutor;
    }

    @Override
    public @Nullable AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) ->
                logger.error("Asynchronous method execution exception: method= {}, params= {}", method.getName(), params, ex);
    }

}
