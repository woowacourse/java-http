package com.techcourse.controller;

import org.apache.coyote.http11.request.HttpRequest;

import java.util.Map;

public class FrontController {

    private final Map<String, Controller> controllers = Map.of(
            "/", new RootController(),
            "/login", new LoginController(),
            "/register", new RegisterController()
    );
    private final Controller staticResourceController = new StaticResourceController();

    public Controller handle(final HttpRequest request) {
        return controllers.getOrDefault(request.getPath(), staticResourceController);
    }
}
