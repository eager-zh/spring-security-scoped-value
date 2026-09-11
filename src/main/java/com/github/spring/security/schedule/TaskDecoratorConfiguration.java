package com.github.spring.security.schedule;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.TaskDecorator;
import org.springframework.core.task.support.CompositeTaskDecorator;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.github.spring.security.strategy.ScopedSecurityContextHolderStrategy;

import java.util.List;

@Configuration(proxyBeanMethods = false)
public class TaskDecoratorConfiguration {

    // Spring Boot 4 supports using multiple TaskDecorators at the same time
    // However, Spring Boot 3 only supports one, so the following code is used for compatibility handling
    @Bean
    TaskDecorator taskDecorator() {
        // return getTaskDecorator(List.of(requestAttributesTaskDecorator(), securityTaskDecorator()));
        return getTaskDecorator(List.of(requestAttributesTaskDecorator()));
    }

    // @Bean
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
    // TaskDecorator securityTaskDecorator() {
	// 	return (runnable) -> () -> ScopedSecurityContextHolderStrategy.getSecuriyContextCarrier().run(runnable);
    // }

    private static @Nullable TaskDecorator getTaskDecorator(List<TaskDecorator> taskDecorators) {
        if (taskDecorators.size() == 1) {
            return taskDecorators.getFirst();
        }
        return (!taskDecorators.isEmpty()) ? new CompositeTaskDecorator(taskDecorators) : null;
    }

}
