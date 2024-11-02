package com.github.spring.security.filter;

import java.io.IOException;

import org.springframework.security.core.context.DeferredSecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.util.Assert;

import com.github.spring.security.strategy.ScopedSecurityContextHolderStrategy;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Most of this code is adopted from Spring's {@link SecurityContextHolderFilter}.
 */
public class ScopedSecurityContextHolderFilter extends SecurityContextHolderFilter {
	
	/**
	 * The marker string duplicates its namesake from {@link SecurityContextHolderFilter} intentionally.
	 * This allows to effectively turn off the functionality of {@link SecurityContextHolderFilter}, 
	 * if it is present in the filter chain; if it is not, then it does not do any harm.  
	 */
	private static final String FILTER_APPLIED = SecurityContextHolderFilter.class.getName() + ".APPLIED";
	
	private final SecurityContextRepository securityContextRepository;
	
	private ScopedSecurityContextHolderStrategy securityContextHolderStrategy; 

	public ScopedSecurityContextHolderFilter(SecurityContextRepository securityContextRepository) {
		super(securityContextRepository);
		this.securityContextRepository = securityContextRepository;
		setSecurityContextHolderStrategy(SecurityContextHolder.getContextHolderStrategy());
	}	
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
	}

	private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		if (request.getAttribute(FILTER_APPLIED) != null) {
			chain.doFilter(request, response);
			return;
		}
		request.setAttribute(FILTER_APPLIED, Boolean.TRUE);
		DeferredSecurityContext deferredContext = securityContextRepository.loadDeferredContext(request);
		try {
			ScopedSecurityContextHolderStrategy.runWhere(deferredContext, () -> {
				securityContextHolderStrategy.setDeferredContext(deferredContext);
				try {
					chain.doFilter(request, response);
				} catch (IOException | ServletException e) {
					throw new RuntimeException(e);
				}
			});
		} catch (RuntimeException e) {
			final Throwable cause = e.getCause();
			if (cause instanceof ServletException)
				throw (ServletException)cause;
			if (cause instanceof IOException)
				throw (IOException)cause;
			throw e;
		} finally {
			request.removeAttribute(FILTER_APPLIED);
		}
	}	
	
	public void setSecurityContextHolderStrategy(SecurityContextHolderStrategy securityContextHolderStrategy) {
		Assert.isInstanceOf(ScopedSecurityContextHolderStrategy.class, securityContextHolderStrategy,
				"Security Context Holder Strategy is not of type "
						+ ScopedSecurityContextHolderStrategy.class.getSimpleName());
		this.securityContextHolderStrategy = (ScopedSecurityContextHolderStrategy) securityContextHolderStrategy;
	}

}
