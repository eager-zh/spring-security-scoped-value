package com.github.spring.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskDecorator;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.SimpleAsyncTaskScheduler;

@Configuration
public class TaskConfig {
	
	@Autowired()
	@Qualifier("securityTaskDecorator")
	private TaskDecorator taskDecorator;

	// may also consider autowiring SimpleAsyncTaskExecutorBuilder and SimpleAsyncTaskSchedulerBuilder
	
	@Bean 
	public TaskExecutor taskExecutor() {
		SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor();
		executor.setTaskDecorator(taskDecorator);
		return executor;
	}
	
	@Bean 
	public TaskScheduler taskScheduler() {
		SimpleAsyncTaskScheduler scheduler = new SimpleAsyncTaskScheduler();
		scheduler.setTaskDecorator(taskDecorator);
		return scheduler;
	}
	
}
