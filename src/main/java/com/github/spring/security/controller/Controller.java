package com.github.spring.security.controller;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.spring.security.strategy.ScopedSecurityContextHolderStrategy;

import jakarta.servlet.http.HttpServletRequest;

@org.springframework.stereotype.Controller
public class Controller {
	
	private static final Logger logger = LoggerFactory.getLogger(Controller.class);

	private final Executor executor;

    public Controller(Executor executor) {
        this.executor = executor;
    }

    @RequestMapping("/handle")
	@ResponseBody
	public String handle(HttpServletRequest request) throws InterruptedException, ExecutionException {
		Assert.isTrue(SecurityContextHolder.getContextHolderStrategy() instanceof ScopedSecurityContextHolderStrategy, 
				ScopedSecurityContextHolderStrategy.class.getSimpleName() + " not installed as Context Holder Strategy");
		logger.info("Current authentication is " + SecurityContextHolder.getContext().getAuthentication());

		CompletableFuture<Void> async = CompletableFuture.runAsync(() -> {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			System.out.println("authentication = " + authentication);
		}, executor);

		async.join();

		return "OK";
	}

}
