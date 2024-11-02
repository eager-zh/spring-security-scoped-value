# spring-security-scoped-value

This is a small POC to illustrate a discussion on the Stack Overflow thread [Spring Security Virtual Threads and ThreadLocal](https://stackoverflow.com/questions/78166517/spring-security-virtual-threads-and-threadlocal).

Custom `SecurityContextHolderStrategy` which retrieves and saves `SecurityContext` from and to a `ScopedValue` is installed.
There are two modes of how the Strategy can be used: 
- Tomcat Handler customizer registers an `ExecutorService` which starts a virtual thread, with the `ScopedValue` bound to it;
- custom Spring Security Filter, `ScopedSecurityContextHolderFilter`, which binds the `ScopedValue` at the time of request handling, it has to be installed instead of/in front of `SecurityContextHolderFilter`.

First mode is activated by setting custom `spring.security.scoped.mode` property to `tomcat-thread-executor`, second - to `security-filter`. Pros and ons of both modes are discussed in the Stack Overflow thread. 

To test,  run Spring Boot `SpringSecurityScopedValueApplication`, point your browser to [a protected resource](http://localhost:8080/handle), make sure that current `SecurityContextHolderStrategy` is our custom one, and see the dump of current `Authentication` in the console log. 

