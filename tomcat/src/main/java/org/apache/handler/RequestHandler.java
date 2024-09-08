package org.apache.handler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.coyote.controller.Controller;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public class RequestHandler implements Handler {

    private final Controller controller;
    private final Method method;

    public RequestHandler(Controller controller, Method method) {
        this.controller = controller;
        this.method = method;
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response) {
        try {
            method.invoke(controller, request, response);
        } catch (IllegalAccessException | InvocationTargetException exception){
            throw new IllegalArgumentException(exception.getMessage());
        }
    }
}
