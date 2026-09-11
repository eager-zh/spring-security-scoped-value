package com.github.spring.security.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.util.Assert;
import org.springframework.web.servlet.config.annotation.*;

@Configuration(proxyBeanMethods = false)
public class WebMvcConfiguration implements WebMvcConfigurer, InitializingBean {

    private SimpleAsyncTaskExecutor mvcTaskExecutor;

    @Autowired
    @Qualifier(TaskExecutorConfiguration.MVC_TASK_EXECUTOR_BEAN_NAME)
    public void setSimpleAsyncTaskExecutor(SimpleAsyncTaskExecutor mvcTaskExecutor) {
        Assert.notNull(mvcTaskExecutor, "'mvcTaskExecutor' must be not null");
        this.mvcTaskExecutor = mvcTaskExecutor;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(mvcTaskExecutor, "'mvcTaskExecutor' must be not null");
    }

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        // Set default values MvcSimpleAsyncTaskExecutor
        configurer.setTaskExecutor(mvcTaskExecutor);
        configurer.setDefaultTimeout(30_000L);
    }

}
