package com.techcourse.controller;

import java.util.Map;
import org.apache.coyote.http11.request.HttpRequest;

public class RequestMapping {

    private final Controller defaultController;
    private final Map<String, Controller> controllers;

    public RequestMapping() {
        this.defaultController = new StaticResourceController();
        this.controllers = Map.of(
                "/", new HomeController(),
                "/register", new RegisterController(),
                "/login", new LoginController()
        );
    }

    public Controller getController(final HttpRequest request) {
        return controllers.getOrDefault(request.path(), defaultController);
    }
}
