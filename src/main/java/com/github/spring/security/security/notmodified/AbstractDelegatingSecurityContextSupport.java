package com.github.spring.security.security.notmodified;

import com.github.spring.security.security.DelegatingSecurityContextCallable;
import com.github.spring.security.security.DelegatingSecurityContextRunnable;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.util.Assert;

import java.util.concurrent.Callable;

abstract class AbstractDelegatingSecurityContextSupport {

	private SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder
		.getContextHolderStrategy();

	private final SecurityContext securityContext;

	/**
	 * Creates a new {@link AbstractDelegatingSecurityContextSupport} that uses the
	 * specified {@link SecurityContext}.
	 * @param securityContext the {@link SecurityContext} to use for each
	 * {@link DelegatingSecurityContextRunnable} and each
	 * {@link DelegatingSecurityContextCallable} or null to default to the current
	 * {@link SecurityContext}.
	 */
	AbstractDelegatingSecurityContextSupport(SecurityContext securityContext) {
		this.securityContext = securityContext;
	}

	void setSecurityContextHolderStrategy(SecurityContextHolderStrategy securityContextHolderStrategy) {
		Assert.notNull(securityContextHolderStrategy, "securityContextHolderStrategy cannot be null");
		this.securityContextHolderStrategy = securityContextHolderStrategy;
	}

	protected final Runnable wrap(Runnable delegate) {
		return DelegatingSecurityContextRunnable.create(delegate, this.securityContext,
				this.securityContextHolderStrategy);
	}

	protected final <T> Callable<T> wrap(Callable<T> delegate) {
		return DelegatingSecurityContextCallable.create(delegate, this.securityContext,
				this.securityContextHolderStrategy);
	}

}