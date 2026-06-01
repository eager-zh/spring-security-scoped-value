# spring-security-scoped-value

This is a small POC to illustrate a discussion on the Stack Overflow thread [Spring Security Virtual Threads and ThreadLocal](https://stackoverflow.com/questions/78166517/spring-security-virtual-threads-and-threadlocal). It offers a custom, `ScopedValue`-based implementation of `SecurityContextHolderStrategy` interface - `ScopedSecurityContextHolderStrategy` class.

## Spring Security and `ScopedValue`s

While the implementation of `SecurityContextHolderStrategy` itself does not pose, it seems, any significant problem, there is one peculiarity of `ScopedValue` which makes the implementation somehow different from other existing implementations of `SecurityContextHolderStrategy`: before `SecurityContext` can be used in a thread, `ScopedValue` instance has be  _bound_  to a `SecurityContext` holder  _for_  this thread. That is, a code that invokes, for example, `SecurityContextHolderStrategy.setContext`, should be executed in a thread which has been, in a sense,  _already initiated_  before such invocation. 

Technically, in terms of `ScopedSecurityContextHolderStrategy`, method `runWhere(DeferredSecurityContext, Runnable)` should be down-stack of the code that invokes `setContext`. By other words, this code should be "inside of" `Runnable`, passed to this method.

In opposite, traditional `ThreadLocal`-based Strategies, let alone more global ones, do not require anything of that kind. Instead, `ThreadLocal` could be set at any time on any thread, `ThreadLocal.set` just "jumps in", could be called at any time and no thread  _initiation_  is necessary. 

The POC offers two practical solutions to this problem:

- special Security filter, which binds `ScopedValue` to `SecurityContext` holder for a thread upon handling a request/response pair, passed through the Filter chain; this is very similar to what `SecurityContextHolderFilter` is doing; 
- (if Tomcat is configured as a web server) customize Tomcat `ProtocolHandler` with an virtual thread executor which binds `ScopedValue` to `SecurityContext` holder for a virtual thread;

## Implementation details

`SecurityContextStrategyConfig` sets`ScopedSecurityContextHolderStrategy` globally upon Spring Context initialization.

There are two modes of how the Strategy can be used: 
- custom Spring Security Filter, `ScopedSecurityContextHolderFilter`, which binds the `ScopedValue` at the time of request handling, it has to be installed instead of/in front of `SecurityContextHolderFilter`.
- Tomcat Handler customizer registers an `ExecutorService` which starts a virtual thread, with the `ScopedValue` bound for it;

First mode is activated by setting custom `spring.security.scoped.mode` property to `security-filter`, second - to `tomcat-thread-executor`. Pros and cons of both modes are discussed in the Stack Overflow thread. For the first mode, `WebSecurityConfig` along with `ScopedSecurityContextConfigurer` replace stock `SecurityContextHolderFilter` in the Security Filter Chain with `ScopedSecurityContextHolderFilter`. For the second mode, `TomcatVirtualThreadExecutorCustomizer` with a help of special subclass of stock `VirtualThreadExecutor` binds `ScopedValue` to a `SecurityContext` holder for Tomcat worker threads. Obviously, as `ScopedValue` application is not limited to virtual threads, the same could be applied to the (pooled) platform threads, although `ScopedValue` might not be relevant to this kind of threads. 

#Running 

To test, run Spring Boot `SpringSecurityScopedValueApplication`, point your browser to [a protected resource](http://localhost:8080/handle), use `admin` as both user name and password, pay attention to `Controller.handle` method which asserts that current `SecurityContextHolderStrategy` is a `ScopedValue`-based one, and see the dump of current `Authentication` in the console log.

## Migration to 25 

Commit [e220c1a](https://github.com/eager-zh/spring-security-scoped-value/commit/e220c1aaa34cc408c7667e01584240ca34ebaf76) migrates the code to Java 25, where `ScopedValue` class is no longer annotated with `jdk.internal.javac.PreviewFeature`. 

To build and run the project by Maven, its version 3.9.15 is required along with JDK 25.
 

