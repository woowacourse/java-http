package com.techcourse.controller;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;
import org.apache.coyote.http.NoHandlerFoundException;
import org.apache.coyote.http.StaticResourceHandler;

public class FrontController implements Controller {

    private final Map<String, Controller> controllers;

    public FrontController() {
        controllers = new ConcurrentHashMap<>();
        controllers.put("/", new RootController());
        controllers.put("/login", new LoginController());
        controllers.put("/register", new RegisterController());
        controllers.put("static", new StaticResourceController());
    }

    @Override
    public HttpResponse handle(final HttpRequest request) {
        String path = request.getPath();
        Controller controller;
        if (controllers.containsKey(path)) {
            controller = controllers.get(path);
            return controller.handle(request);
        }
        if (StaticResourceHandler.isStaticResource(request)) {
            controller = controllers.get("static");
            return controller.handle(request);
        }
        throw new NoHandlerFoundException(path);
    }
}
