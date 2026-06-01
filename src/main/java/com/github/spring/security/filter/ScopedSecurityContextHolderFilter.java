package com.github.spring.security.filter;

import java.io.IOException;

import org.springframework.security.core.context.DeferredSecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.util.Assert;
import org.springframework.web.filter.GenericFilterBean;

import com.github.spring.security.strategy.ScopedSecurityContextHolderStrategy;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * A Spring Security filter which 
 * orchestrates the usage of {@link ScopedSecurityContextHolderStrategy}
 * 
 * Due to the nature of {@link ScopedValue}, 
 * the generic {@link SecurityContextHolderFilter}, is not suitable for this purpose
 * because {@link jakarta.servlet.Filter#doFilter(ServletRequest, ServletResponse, FilterChain)}
 * method has to be invoked under <code>ScopedValue.Carrier.run</code> method. 
 * 
 * <p></p>
 * Most of the code, however, is adopted from {@link SecurityContextHolderFilter}.
 * The only difference is the implementation of 
 * {@link FilterChain#doFilter(ServletRequest, ServletResponse)} method.
 */
public class ScopedSecurityContextHolderFilter extends GenericFilterBean {
	
	private static final String FILTER_APPLIED = ScopedSecurityContextHolderFilter.class.getName() + ".APPLIED";
	
	private final SecurityContextRepository securityContextRepository;
	
	private ScopedSecurityContextHolderStrategy securityContextHolderStrategy; 

	public ScopedSecurityContextHolderFilter(SecurityContextRepository securityContextRepository) {
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
			securityContextHolderStrategy.clearContext();
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
