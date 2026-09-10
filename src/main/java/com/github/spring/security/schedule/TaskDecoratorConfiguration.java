package com.github.spring.security.schedule;

import com.github.spring.security.strategy.ScopedSecurityContextHolderStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

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

    // @Bean
    TaskDecorator securityTaskDecorator() {
        return (runnable) -> {
            // The original SecurityContextHolderStrategy is compatible; the context is guaranteed to have a value, and no exception will be thrown.
            // If the default strategy inside SecurityContextHolder is changed to ScopedSecurityContextHolderStrategy, an exception will be thrown, resulting in incompatibility.
            SecurityContext context = SecurityContextHolder.getContext();
            return () -> {
                try {
                    ScopedSecurityContextHolderStrategy.getSecuriyContextCarrier().run(runnable);
                } finally {
                    SecurityContextHolder.clearContext();
                }
            };
        };
    }

}
