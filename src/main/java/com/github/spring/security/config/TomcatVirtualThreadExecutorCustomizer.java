package com.github.spring.security.config;

import org.apache.tomcat.util.threads.VirtualThreadExecutor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

import com.github.spring.security.strategy.ScopedSecurityContextHolderStrategy;

@Component
@ConditionalOnProperty(name = "spring.security.scoped.mode", havingValue = "tomcat-thread-executor")
public class TomcatVirtualThreadExecutorCustomizer implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

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