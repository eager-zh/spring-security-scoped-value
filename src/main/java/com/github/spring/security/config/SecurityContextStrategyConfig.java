package com.github.spring.security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;

import com.github.spring.security.strategy.ScopedSecurityContextHolderStrategy;

import jakarta.annotation.PostConstruct;

/**
 * Sets {@link ScopedSecurityContextHolderStrategy} as current 
 * {@link org.springframework.security.core.context.SecurityContextHolderStrategy SecurityContextHolderStrategy}.
 */
@Configuration
public class SecurityContextStrategyConfig {
	
	@PostConstruct
	private void init() {
		SecurityContextHolder.setContextHolderStrategy(new ScopedSecurityContextHolderStrategy());
	}

}
