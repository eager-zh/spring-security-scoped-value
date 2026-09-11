package com.github.spring.security.strategy;

import org.springframework.security.core.context.DeferredSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.context.SecurityContextImpl;

/**
 * A <code>ScopedValue</code>-based implementation of
 * {@link SecurityContextHolderStrategy}.
 *
 * @see java.lang.ScopedValue
 * @see com.github.spring.security.filter.ScopedSecurityContextHolderFilter
 * @see com.github.spring.security.config.TomcatVirtualThreadExecutorCustomizer 
 */
public class ScopedSecurityContextHolderStrategy implements SecurityContextHolderStrategy {
	
	/**
	 * An instance of {@link ScopedValue} which has to be bound to an instance 
	 * of {@link SecurityContextScopedValueHolder}.
	 */
	private static final ScopedValue<SecurityContextScopedValueHolder> SECURITY_CONTEXT = ScopedValue.newInstance();

	/**
	 * A structure that holds {@link SecurityContext}.
	 * An instance of {@link ScopedValue}, {@link ScopedSecurityContextHolderStrategy#SECURITY_CONTEXT}, 
	 * has to be bound to an instance of this class. 
	 */
	private static class SecurityContextScopedValueHolder {
		
		private SecurityContext securityContext;

		public SecurityContextScopedValueHolder() {
		}

		public SecurityContextScopedValueHolder(SecurityContext securityContext) {
			this.securityContext = securityContext;
		}

		public SecurityContext getSecurityContext() {
			return securityContext;
		}

		public void setSecurityContext(SecurityContext securityContext) {
			this.securityContext = securityContext;
		}

	}
	
	@Override
	public void clearContext() {
		if (SECURITY_CONTEXT.isBound()) {
			// otherwise, if Scoped Security Context Filter is used,
			// we have unbound ISE upon a call securityContextHolderStrategy.clearContext(); 
			// in FilterChainProxy.doFilter.
			// This check is unnecessary if Scoped Value is engaged in Tomcat Thread Executor 
			retrieveSecurityContextScopedValueHolder().setSecurityContext(null);
		}
	}

	@Override
	public SecurityContext getContext() {
		final SecurityContextScopedValueHolder holder = retrieveSecurityContextScopedValueHolder();
		SecurityContext context = holder.getSecurityContext();
		if (context == null) {
			context = createEmptyContext();
			holder.setSecurityContext(context);
		}
		return context;
	}
	
	@Override
	public void setContext(SecurityContext context) {
		retrieveSecurityContextScopedValueHolder().setSecurityContext(context);
	}

	@Override
	public SecurityContext createEmptyContext() {
		return new SecurityContextImpl();
	}

	private SecurityContextScopedValueHolder retrieveSecurityContextScopedValueHolder() {
		if (isBound()) {
			return SECURITY_CONTEXT.get();
		} else {
			throw new IllegalStateException("Security Context Scoped Value not bound");
		}
	}

	public boolean isBound() {
		return SECURITY_CONTEXT.isBound();
	}
	
	/**
	 * Binds an instance of {@link ScopedValue}, {@link ScopedSecurityContextHolderStrategy#SECURITY_CONTEXT}, 
	 * to an instance of {@link SecurityContextScopedValueHolder} 
	 * <i>for current thread</i>.
	 */
	public static void runWhere(DeferredSecurityContext deferredContext, Runnable r) {
		ScopedValue.where(SECURITY_CONTEXT, new SecurityContextScopedValueHolder(deferredContext.get())).run(r);
	}

	public static <R, X extends Throwable> R callWhere(DeferredSecurityContext deferredContext, ScopedValue.CallableOp<? extends R, X> op) throws X {
		return ScopedValue.where(SECURITY_CONTEXT, new SecurityContextScopedValueHolder(deferredContext.get())).call(op);
	}
	
	/**
	 * A convenience version of {@link #runWhere(DeferredSecurityContext, Runnable)} method.
	 */
	public static ScopedValue.Carrier getSecuriyContextCarrier() {
		return ScopedValue.where(SECURITY_CONTEXT, new SecurityContextScopedValueHolder());
	}

}
