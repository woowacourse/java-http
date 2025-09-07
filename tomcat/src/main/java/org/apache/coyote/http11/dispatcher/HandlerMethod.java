package org.apache.coyote.http11.dispatcher;

import java.lang.reflect.Method;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class HandlerMethod {

    private final Object controller;
    private final Method method;

    public HandlerMethod(Object controller, Method method) {
        this.controller = controller;
        this.method = method;
    }

    public HttpResponse invoke(HttpRequest httpRequest) {
        try {
            return (HttpResponse) method.invoke(controller);
        } catch (ReflectiveOperationException e) {
            throw new IllegalArgumentException();
        }
    }

    public Object getController() {
        return controller;
    }

    public Method getMethod() {
        return method;
    }
}
