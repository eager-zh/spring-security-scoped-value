package com.github.spring.security.config;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.util.Assert;

/**
 * If <code>spring.security.scoped.mode</code> property is set to <code>security-filter</code>,
 * adds {@link ScopedSecurityContextConfigurer} to a HTTP Security builder,
 * builds Security Filter Chain, 
 * and removes {@link SecurityContextHolderFilter} from its set of filters.
 */
@Configuration
@EnableWebSecurity
@ConditionalOnProperty(name = "spring.security.scoped.mode", havingValue = "security-filter")
public class WebSecurityConfig {
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests((requests) -> requests.anyRequest().authenticated());

		http.formLogin(withDefaults());
		http.httpBasic(withDefaults());
		
		http.with(new ScopedSecurityContextConfigurer<>(), (_) -> {});

		final DefaultSecurityFilterChain defaultSecurityFilterChain = http.build();
		Assert.isTrue(
				defaultSecurityFilterChain.getFilters()
						.removeIf(filter -> filter instanceof SecurityContextHolderFilter),
				SecurityContextHolderFilter.class.getSimpleName() + " is not present in the Filter Chain");
		return defaultSecurityFilterChain;
	}

}