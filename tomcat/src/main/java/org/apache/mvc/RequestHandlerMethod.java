package org.apache.mvc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import org.apache.coyote.http11.ResponseEntity;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.exception.TempException;

public class RequestHandlerMethod {

    private final Object instance;
    private final Method method;

    public RequestHandlerMethod(Object instance, Method method) {
        validateInvocable(instance, method);
        this.instance = instance;
        this.method = method;
    }

    private void validateInvocable(Object instance, Method method) {
        validateInstanceType(instance);
        validateReturnType(method.getReturnType());
        validateParameter(method.getParameters());
    }

    private void validateInstanceType(Object instance) {
        if (!(instance instanceof Controller)) {
            throw new TempException();
        }
    }

    private void validateReturnType(Class<?> clazz) {
        if (!clazz.equals(ResponseEntity.class)) {
            throw new TempException();
        }
    }

    private void validateParameter(Parameter[] parameters) {
        if (parameters.length != 1) {
            throw new TempException();
        }
        if (!HttpRequest.class.equals(parameters[0].getType())) {
            throw new TempException();
        }
    }

    public ResponseEntity handle(HttpRequest httpRequest) {
        try {
            Object result = method.invoke(instance, httpRequest);
            return castToResponseEntity(result);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    private ResponseEntity castToResponseEntity(Object result) {
        validateReturnType(result.getClass());
        return (ResponseEntity) result;
    }
}
