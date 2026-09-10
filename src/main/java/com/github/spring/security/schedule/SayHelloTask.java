package com.github.spring.security.schedule;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Optional;

@Component
@EnableScheduling
public class SayHelloTask {

    private static final Log log = LogFactory.getLog(SayHelloTask.class);

    @Scheduled(initialDelay = 1000, fixedRate = Long.MAX_VALUE)
    public void say() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        String username = Optional.ofNullable(authentication).map(Principal::getName).orElse("anonymous");

        log.info("task context: " + context);
        log.info("task running: hello " + username);
    }

}
