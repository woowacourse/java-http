package com.techcourse.controller;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;

public class FrontController implements Controller {

    private static final FrontController instance = new FrontController();

    private final Map<String, Controller> controllers;

    private FrontController() {
        controllers = new ConcurrentHashMap<>();
        controllers.put("/", new RootController());
        controllers.put("/login", new LoginController());
        controllers.put("/register", new RegisterController());
        controllers.put("static", new StaticResourceController());
        controllers.put("/404.html", new NotFoundController());
    }

    public static FrontController getInstance() {
        return instance;
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
        controller = controllers.get("/404.html");
        return controller.handle(request);
    }
}
