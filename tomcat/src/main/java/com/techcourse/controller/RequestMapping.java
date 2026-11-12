package com.techcourse.controller;

import java.util.Map;
import org.apache.coyote.http11.request.HttpRequest;

public final class RequestMapping {
    private final Controller staticResourceController = new StaticResourceController();
    private final Map<String, Controller> controllers = Map.of(
        "/login", new LoginController(),
        "/register", new RegisterController(),
        "/index", new IndexController(),
        "/index.html", new IndexController()
    );

    public Controller getController(HttpRequest request) {
        return controllers.getOrDefault(request.requestLine().path().resource(), staticResourceController);
    }
}
