package com.github.spring.security.config;

import org.apache.tomcat.util.threads.VirtualThreadExecutor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

import com.github.spring.security.strategy.ScopedSecurityContextHolderStrategy;

/**
 * If <code>spring.security.scoped.mode</code> property is set to <code>tomcat-thread-executor</code>,
 * configures Tomcat Protocol Handlers with a special 
 * {@link TomcatVirtualThreadExecutorCustomizer.ScopedVirtualThreadExecutor Executor}.
 */
@Component
@ConditionalOnProperty(name = "spring.security.scoped.mode", havingValue = "tomcat-thread-executor")
public class TomcatVirtualThreadExecutorCustomizer implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

	/**
	 * A subclass of {@link VirtualThreadExecutor} which binds an instance of {@link ScopedValue}
	 * to a holder of {@link org.springframework.security.core.context.SecurityContext SecuriyContext}
	 * <i>for the</i> current virtual thread.
	 */
	private static class ScopedVirtualThreadExecutor extends VirtualThreadExecutor {

		public ScopedVirtualThreadExecutor(String namePrefix) {
			super(namePrefix);
		}

		@Override
		public void execute(Runnable command) {
			super.execute(() -> ScopedSecurityContextHolderStrategy.getSecuriyContextCarrier().run(command));
		}

	}

	@Override
	public void customize(TomcatServletWebServerFactory factory) {
		factory.addProtocolHandlerCustomizers((protocolHandler) -> protocolHandler
				.setExecutor(new ScopedVirtualThreadExecutor("tomcat-handler-")));
	}

}