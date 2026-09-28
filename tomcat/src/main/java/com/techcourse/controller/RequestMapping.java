package com.techcourse.controller;

import org.apache.coyote.http11.HttpRequest;

import java.util.Map;

public final class RequestMapping {

    private final Controller staticResourceController = new StaticResourceController();
    private final Map<String, Controller> controllers = Map.of(
            "/login", new LoginController(),
            "/register", new RegisterController()
    );

    public Controller getController(final HttpRequest request) {
        return controllers.getOrDefault(request.getPath(), staticResourceController);
    }
}
