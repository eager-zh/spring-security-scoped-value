package com.github.spring.security.config;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@ConditionalOnProperty(name = "spring.security.scoped.mode", havingValue = "security-filter")
public class WebSecurityConfig {
	
	@Value("${spring.security.user.name}")
	private String username;

	@Value("${spring.security.user.password}")
	private String password;

	@Value("${spring.security.user.roles}")
	private String roles;

	@SuppressWarnings("deprecation")
	@Bean
	public UserDetailsService userDetailsService() {
		final InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
		manager.createUser(
				User.withDefaultPasswordEncoder().username(username).password(password).roles(roles).build());
		return manager;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests((requests) -> requests.anyRequest().authenticated());

		http.formLogin(withDefaults());
		http.httpBasic(withDefaults());
		
		http.with(new ScopedSecurityContextConfigurer<>(), (c) -> {});

		return http.build();
	}

}