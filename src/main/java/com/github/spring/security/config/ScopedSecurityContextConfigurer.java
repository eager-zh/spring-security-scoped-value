package com.github.spring.security.config;

import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.security.web.context.SecurityContextRepository;

import com.github.spring.security.filter.ScopedSecurityContextHolderFilter;

/**
 * Installs {@link ScopedSecurityContextHolderFilter} in front of 
 * {@link org.springframework.security.web.context.SecurityContextHolderFilter SecurityContextHolderFilter}.
 * See {@link ScopedSecurityContextHolderFilter} for the details.
 */
public final class ScopedSecurityContextConfigurer<H extends HttpSecurityBuilder<H>>
		extends AbstractHttpConfigurer<ScopedSecurityContextConfigurer<H>, H> {

	/**
	 * Adopted from 
	 * {@link org.springframework.security.config.annotation.web.configurers.SecurityContextConfigurer SecurityContextConfigurer}.
	 */
	private SecurityContextRepository getSecurityContextRepository() {
		SecurityContextRepository securityContextRepository = getBuilder()
				.getSharedObject(SecurityContextRepository.class);
		if (securityContextRepository == null) {
			securityContextRepository = new DelegatingSecurityContextRepository(
					new RequestAttributeSecurityContextRepository(), new HttpSessionSecurityContextRepository());
		}
		return securityContextRepository;
	}

	@Override
	public void configure(H http) {
		final SecurityContextHolderFilter securityContextHolderFilter = 
			postProcess(new ScopedSecurityContextHolderFilter(getSecurityContextRepository()));
		securityContextHolderFilter.setSecurityContextHolderStrategy(getSecurityContextHolderStrategy());
		http.addFilterBefore(securityContextHolderFilter, SecurityContextHolderFilter.class);
	}

}
