package org.apache.catalina.servlet;

import java.util.HashMap;
import java.util.Map;

import org.apache.catalina.servlet.exceptionhandler.NotFoundExceptionHandler;
import org.apache.catalina.servlet.exceptionhandler.UnAuthorizedExceptionHandler;
import org.apache.tomcat.http.exception.AuthenticationException;
import org.apache.tomcat.http.exception.NotFoundException;

public class ExceptionHandlerMapping {

    private final Map<Class<? extends Exception>, ExceptionHandler> registry;

    public ExceptionHandlerMapping() {
        registry = new HashMap<>();
        registry.put(AuthenticationException.class, new UnAuthorizedExceptionHandler());
        registry.put(NotFoundException.class, new NotFoundExceptionHandler());
    }

    public ExceptionHandler get(final Class<? extends Exception> clazz) {
        if (!registry.containsKey(clazz)) {
            return registry.get(NotFoundException.class);
        }
        return registry.get(clazz);
    }
}
