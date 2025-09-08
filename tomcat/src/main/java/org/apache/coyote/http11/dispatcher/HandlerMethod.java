package org.apache.coyote.http11.dispatcher;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import org.apache.coyote.http11.request.HttpRequest;

public class HandlerMethod {

    private final Object controller;
    private final Method method;

    public HandlerMethod(Object controller, Method method) {
        this.controller = controller;
        this.method = method;
    }

    public Object invoke(HttpRequest req) {
        try {
            Map<String, String> qp = req.getMappingLine().getParameters();

            Parameter[] params = method.getParameters();
            Object[] args = new Object[params.length];

            for (int i = 0; i < params.length; i++) {
                Class<?> t = params[i].getType();
                if (t == HttpRequest.class) {
                    args[i] = req;
                } else if (Map.class.isAssignableFrom(t)) {
                    args[i] = qp;
                } else {
                    throw new IllegalArgumentException("unsupported param type: " + t.getName());
                }
            }

            return method.invoke(controller, args);
        } catch (ReflectiveOperationException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Object getController() {
        return controller;
    }

    public Method getMethod() {
        return method;
    }
}
