package com.github.spring.security.strategy;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.context.SecurityContextImpl;

public class ScopedSecurityContextHolderStrategy implements SecurityContextHolderStrategy {
	
	private static final ScopedValue<SecurityContextScopedValueHolder> SECURITY_CONTEXT = ScopedValue.newInstance();

	private static class SecurityContextScopedValueHolder {
		
		private SecurityContext securityContext;

		public SecurityContext getSecurityContext() {
			return securityContext;
		}

		public void setSecurityContext(SecurityContext securityContext) {
			this.securityContext = securityContext;
		}

	}
	
	@Override
	public void clearContext() {
		retrieveSecurityContextScopedValueHolder().setSecurityContext(null);
	}

	@Override
	public SecurityContext getContext() {
		return retrieveSecurityContextScopedValueHolder().getSecurityContext();
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
		if (SECURITY_CONTEXT.isBound()) {
			return SECURITY_CONTEXT.get();
		} else {
			throw new IllegalStateException("Security Context Scoped Value not bound");
		}
	}
	
	public static ScopedValue.Carrier getSecuriyContextCarrier() {
		return ScopedValue.where(SECURITY_CONTEXT, new SecurityContextScopedValueHolder());
	}

}
