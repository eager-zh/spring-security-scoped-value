package com.github.spring.security.security.notmodified;

import org.springframework.security.concurrent.DelegatingSecurityContextRunnable;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.util.Assert;

import java.util.concurrent.Executor;

/**
 * @see org.springframework.security.concurrent.DelegatingSecurityContextExecutor
 */
public class DelegatingSecurityContextExecutor extends AbstractDelegatingSecurityContextSupport implements Executor {

    private final Executor delegate;

    /**
     * Creates a new {@link org.springframework.security.concurrent.DelegatingSecurityContextExecutor} that uses the specified
     * {@link SecurityContext}.
     * @param delegateExecutor the {@link Executor} to delegate to. Cannot be null.
     * @param securityContext the {@link SecurityContext} to use for each
     * {@link DelegatingSecurityContextRunnable} or null to default to the current
     * {@link SecurityContext}
     */
    public DelegatingSecurityContextExecutor(Executor delegateExecutor, SecurityContext securityContext) {
        super(securityContext);
        Assert.notNull(delegateExecutor, "delegateExecutor cannot be null");
        this.delegate = delegateExecutor;
    }

    /**
     * Creates a new {@link org.springframework.security.concurrent.DelegatingSecurityContextExecutor} that uses the current
     * {@link SecurityContext} from the {@link SecurityContextHolder} at the time the task
     * is submitted.
     * @param delegate the {@link Executor} to delegate to. Cannot be null.
     */
    public DelegatingSecurityContextExecutor(Executor delegate) {
        this(delegate, null);
    }

    @Override
    public final void execute(Runnable task) {
        this.delegate.execute(wrap(task));
    }

    protected final Executor getDelegateExecutor() {
        return this.delegate;
    }

    /**
     * Sets the {@link SecurityContextHolderStrategy} to use. The default action is to use
     * the {@link SecurityContextHolderStrategy} stored in {@link SecurityContextHolder}.
     *
     * @since 5.8
     */
    public void setSecurityContextHolderStrategy(SecurityContextHolderStrategy securityContextHolderStrategy) {
        super.setSecurityContextHolderStrategy(securityContextHolderStrategy);
    }

}
