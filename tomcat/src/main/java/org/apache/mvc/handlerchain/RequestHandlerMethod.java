package org.apache.mvc.handlerchain;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ResponseEntity;
import org.apache.mvc.Controller;

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
            throw new IllegalArgumentException("Instance of request handler must be class of controller");
        }
    }

    private void validateReturnType(Class<?> clazz) {
        if (!clazz.equals(ResponseEntity.class)) {
            throw new IllegalArgumentException("Return type of request handler must be ResponseEntity");
        }
    }

    private void validateParameter(Parameter[] parameters) {
        if (parameters.length < 1 || !HttpRequest.class.equals(parameters[0].getType())) {
            throw new IllegalArgumentException("First parameter of request handler must be HttpRequest");
        }
    }

    public ResponseEntity handle(HttpRequest httpRequest) {
        try {
            Object result = method.invoke(instance, httpRequest);
            return castToResponseEntity(result);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("exception on method invocation", e);
        }
    }

    private ResponseEntity castToResponseEntity(Object result) {
        validateReturnType(result.getClass());
        return (ResponseEntity) result;
    }
}
