package com.github.spring.security.schedule;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.TaskDecorator;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.github.spring.security.strategy.ScopedSecurityContextHolderStrategy;

@Configuration(proxyBeanMethods = false)
public class TaskDecoratorConfiguration {

    @Bean
    TaskDecorator requestAttributesTaskDecorator() {
        return (runnable) -> {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            return () -> {
                try {
                    RequestContextHolder.setRequestAttributes(requestAttributes);
                    runnable.run();
                } finally {
                    RequestContextHolder.resetRequestAttributes();
                }
            };
        };
    }

    @Bean
    @Primary
    public TaskDecorator securityTaskDecorator() {
		return (runnable) -> () -> ScopedSecurityContextHolderStrategy.getSecuriyContextCarrier().run(runnable);
    }

}
