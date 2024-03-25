# spring-security-scoped-value

This is a small POC to illustrate a discussion on StackOverflow question [Spring Security Virtual Threads and ThreadLocal](https://stackoverflow.com/questions/78166517/spring-security-virtual-threads-and-threadlocal).

Custom `SecurityContextHolderStrategy` which retrieves and saves `SecurityContext` from and to a `ScopedValue` is installed. Tomcat Handler customizer registers an `ExecutorService` which starts a virtual thread, with the `ScopedValue` bound to it. 

To test,  run Spring Boot `SpringSecurityScopedValueApplication`, point your browser to [a protected resource](http://localhost:8080/handle), make sure that current `SecurityContextHolderStrategy` is our custom one, and see the dump of current `Authentication` in the console log. 

