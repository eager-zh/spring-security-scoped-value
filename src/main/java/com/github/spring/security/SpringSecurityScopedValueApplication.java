package com.github.spring.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.github.spring.security.config.SecurityContextStrategyConfig;
import com.github.spring.security.config.TomcatVirtualThreadExecutorCustomizer;
import com.github.spring.security.controller.Controller;

@SpringBootApplication
@ComponentScan(basePackageClasses = { Controller.class, TomcatVirtualThreadExecutorCustomizer.class,
		SecurityContextStrategyConfig.class })
public class SpringSecurityScopedValueApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringSecurityScopedValueApplication.class, args);
	}

}