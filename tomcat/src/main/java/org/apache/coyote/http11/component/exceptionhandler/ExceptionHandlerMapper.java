package org.apache.coyote.http11.component.exceptionhandler;

import java.util.HashMap;
import java.util.Map;

import org.apache.coyote.http11.component.exception.AuthenticationException;
import org.apache.coyote.http11.component.exception.NotFoundException;

public class ExceptionHandlerMapper {

    private static final Map<Class<? extends Exception>, ExceptionHandler> registry;

    static {
        registry = new HashMap<>();
        registry.put(AuthenticationException.class, new UnAuthorizedExceptionHandler());
        registry.put(NotFoundException.class, new NotFoundExceptionHandler());
    }

    private ExceptionHandlerMapper() {
    }

    public static ExceptionHandler get(final Class<? extends Exception> clazz) {
        if (!registry.containsKey(clazz)) {
            return registry.get(NotFoundException.class);
        }
        return registry.get(clazz);
    }
}
