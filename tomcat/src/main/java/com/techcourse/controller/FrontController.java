package com.techcourse.controller;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;
import org.apache.coyote.http.NoHandlerFoundException;
import org.apache.coyote.http.StaticResourceHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FrontController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(FrontController.class);
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
        log.info("FrontController.handle(): Request path: {}", path);
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
